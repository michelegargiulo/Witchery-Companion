package com.smokeythebandicoot.witcherypatcher.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import net.msrandom.witchery.brewing.action.effect.BrewEffectSerializer;
import net.msrandom.witchery.brewing.action.effect.ErosionBrewEffect;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.BlockActionCircle;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 Mixins:
 [Bugfix] Fixes brew of erosion crashing due to non-positive integer passed to world.random.nextInt()
 [Gameplay] Set maximum Harvest Level for the brew
 [Gameplay] Optional Block Blacklist
 */
@Mixin(value = ErosionBrewEffect.class, remap = false)
public class ErosionBrewEffectMixin extends BrewActionEffect {
    private ErosionBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    @Inject(method = "doApplyToEntity", at = @At("HEAD"), remap = false)
    private void WPdoApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack actionStack, CallbackInfo cbi) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.fixBrewErosion) {
            int bound = MathHelper.ceil(5.0 / modifiers.powerScalingFactor);
            if (world.rand.nextInt(Math.max(bound, 1)) == 0) {
                targetEntity.attackEntityFrom(DamageSource.causeThrownDamage(targetEntity, modifiers.caster), (float) MathHelper.ceil(8.0 * modifiers.powerScalingFactor));
            }

            EntityEquipmentSlot[] var5 = EntityEquipmentSlot.values();

            for (EntityEquipmentSlot slot : var5) {
                ItemStack stack = targetEntity.getItemStackFromSlot(slot);
                if (!stack.isEmpty() && stack.isItemStackDamageable()) {
                    stack.damageItem(MathHelper.ceil((50.0 + (double) world.rand.nextInt(25)) * modifiers.powerScalingFactor), modifiers.caster);
                }
            }
        }
    }

    @Inject(method = "doApplyToBlock", at = @At("HEAD"), remap = false, cancellable = true)
    protected void WPdoApplyToBlock(World world, BlockPos pos, EnumFacing side, int radius, ModifiersEffect modifiers, ItemStack actionStack, CallbackInfo cbi) {
        final AtomicInteger obsidianCount = new AtomicInteger();

        for(int r = radius; r > 0; --r) {
            (new BlockActionCircle() {
                public void onBlock(World world, BlockPos pos) {
                    IBlockState state = world.getBlockState(pos);
                    if (WitcheryUtils.isBlockBreakable(world, pos, state) && WitcheryUtils.canBreak(state) && witchery_Patcher$canBeBroken(state)) {
                        world.setBlockToAir(pos);
                        WitcheryNetworkChannel.sendToAllAround(new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 0.5F, 0.5F, EnumParticleTypes.WATER_SPLASH), world, pos);
                        obsidianCount.addAndGet(state.getBlock() == Blocks.OBSIDIAN ? 1 : 0);
                    }

                }
            }).processFilledCircle(world, pos, r);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, modifiers.caster.getSoundCategory(), 1.0F, 2.0F);
        if (ModConfig.PatchesConfiguration.BrewsTweaks.dropObsidian) {
            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN, obsidianCount.get())));
        }
        cbi.cancel();
    }

    @Unique
    protected boolean witchery_Patcher$canBeBroken(IBlockState state) {
        return !(ModConfig.PatchesConfiguration.BrewsTweaks.stateBlacklist.contains(state) ||
            state.getBlock().getHarvestLevel(state) > ModConfig.PatchesConfiguration.BrewsTweaks.maxBlockHarvestLevel);
    }

}
