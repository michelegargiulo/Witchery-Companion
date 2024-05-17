package com.smokeythebandicoot.witcherypatcher.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.integrations.crafttweaker.bridge.ErosionBrewBridge;
import com.smokeythebandicoot.witcherypatcher.integrations.crafttweaker.nonrecipes.ErosionBrewHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 Mixins:
 [Bugfix] Fixes brew of erosion crashing due to non-positive integer passed to world.random.nextInt()
 [Tweak] Complete CraftTweaker integration for behavious customization
 */
@Mixin(value = ErosionBrewEffect.class)
public abstract class ErosionBrewEffectMixin extends BrewActionEffect {
    private ErosionBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    @Inject(method = "doApplyToEntity", at = @At("HEAD"), remap = false)
    private void WPdoApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack actionStack, CallbackInfo cbi) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.erosion_fixRandomIntegerCrash) {
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
        if (ModConfig.PatchesConfiguration.BrewsTweaks.erosion_tweakEnableCrafttweaker) {

            HashMap<IBlockState, AtomicInteger> blockCount = new HashMap<>();
            for (int r = radius; r > 0; --r) {
                (new BlockActionCircle() {
                    public void onBlock(World world, BlockPos pos) {
                        IBlockState state = world.getBlockState(pos);

                        // If the brew should mine, then mine the blocks
                        if (ErosionBrewBridge.canMine(state)) {
                            if (blockCount.containsKey(state)) {
                                blockCount.get(state).addAndGet(1);
                            } else {
                                blockCount.put(state, new AtomicInteger(1));
                            }
                            world.setBlockToAir(pos);
                            WitcheryNetworkChannel.sendToAllAround(new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 0.5F, 0.5F, EnumParticleTypes.WATER_SPLASH), world, pos);

                        // Otherwise, if the brew can destroy the block, destroy it:
                        } else if (ErosionBrewBridge.canDestroy(state)) {
                            world.setBlockToAir(pos);
                            WitcheryNetworkChannel.sendToAllAround(new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 0.5F, 0.5F, EnumParticleTypes.WATER_SPLASH), world, pos);
                        }
                    }
                }).processFilledCircle(world, pos, r);
            }

            // Play sounds
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, modifiers.caster.getSoundCategory(), 1.0F, 2.0F);

            // Spawn eventual entities at blocks' position
            for (IBlockState state : blockCount.keySet()) {
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(),
                                    new ItemStack(state.getBlock().getItemDropped(state, world.rand, 0), blockCount.get(state).get())));
            }

            // Stop execution to avoid executing vanilla Witchery logic
            cbi.cancel();
        }
    }

}
