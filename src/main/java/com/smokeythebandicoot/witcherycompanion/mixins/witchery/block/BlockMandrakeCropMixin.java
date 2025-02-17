package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.mandrakecrop.IBlockMandrakeCropAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.msrandom.witchery.block.BlockMandrakeCrop;
import net.msrandom.witchery.entity.monster.EntityMandrake;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 Mixins:
 [Bugfix] Mandrake spawning even when they are not fully mature
 [Tweak] Mandrake spawn rate for both daytime and nighttime
 [Tweak] Mandrake root drop chance
 */
@ParametersAreNonnullByDefault
@Mixin(BlockMandrakeCrop.class)
public abstract class BlockMandrakeCropMixin extends BlockCrops implements IBlockMandrakeCropAccessor {

    /** This mixin replaces the harvestBlock function to improve with a custom shouldSpawnMandrake function */
    @Inject(method = "harvestBlock", remap = false, cancellable = true,
            at = @At(value = "HEAD"))
    private void fixMandrakeSpawnConditions(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack, CallbackInfo ci) {
        if (BlockTweaks.mandrakeCrop_fixMandrakeSpawningNotMature) {
            if (witcherycompanion$accessor$shouldSpawnMandrake(world, player, pos, state, stack)) {
                super.harvestBlock(world, player, pos, state, te, stack);
            }
            else {
                witcherycompanion$accessor$spawnMandrake(world, player, pos, state, stack);
            }
            ci.cancel();
        }
    }


    @Override
    public boolean witcherycompanion$accessor$shouldSpawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack) {

        // Pre-conditions
        if (!this.isMaxAge(state) ||
            world.getDifficulty() == EnumDifficulty.PEACEFUL ||
            world.isRemote
        ) {
            return false;
        }

        // Chance
        WorldProvider provider = world.provider;
        double chance = provider.isDaytime() ? BlockTweaks.mandrakeCrop_tweakDaytimeSpawnChance : BlockTweaks.mandrakeCrop_tweakNighttimeSpawnChance;

        return world.rand.nextDouble() < chance;
    }

    @Override
    public void witcherycompanion$accessor$spawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack) {
        EntityMandrake mandrake = new EntityMandrake(world);
        mandrake.setLocationAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
        world.spawnEntity(mandrake);
        WitcheryUtils.addNewParticles(world, EnumParticleTypes.EXPLOSION_NORMAL, mandrake.posX, mandrake.posY, mandrake.posZ, 0.0, 0, 0.5, 0.0);
        player.addStat(StatList.getBlockStats((Block)this));
        player.addExhaustion(0.005F);
    }
}
