package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.api.MutandisApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.item.ItemMutandis;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(ItemMutandis.class)
public abstract class ItemMutandisMixin extends Item {


    @Shadow(remap = false) @Final
    private boolean extremis;

    @Inject(method = "onItemUse", remap = true, cancellable = true, at = @At("HEAD"))
    public void onItemUseOverwrite(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (!ItemTweaks.mutandis_tweakEnableCraftTweaker) return;

        if (!world.isRemote) {
            boolean hadEffect = false;

            // If extremis, try Extremis conversions first
            if (this.extremis) {
                hadEffect = witchery_Patcher$performClayPatchConversion(world, pos);
                if (!hadEffect) hadEffect = witchery_Patcher$performGrassConversion(world, pos);
            }

            // Then try plant conversion, accounting for extremis version of mutandis
            if (!hadEffect) hadEffect = witchery_Patcher$performMutandisConversion(world, pos, extremis);
            if (hadEffect) {
                ItemStack stack = player.getHeldItem(hand);
                stack.shrink(1);
            }

        }

        cir.setReturnValue(EnumActionResult.SUCCESS);
    }


    /** Returns false ONLY if the conversion CANNOT happen. Returns true if blocks are valid but chance prevented it **/
    @Unique
    private static boolean witchery_Patcher$performClayPatchConversion(World world, BlockPos pos) {
        final boolean chance = world.rand.nextBoolean();
        if (witchery_Patcher$tryPerformClayConversion(world, pos, chance)) {
            {
                witchery_Patcher$tryPerformClayConversion(world, pos.north(), false);
                witchery_Patcher$tryPerformClayConversion(world, pos.east(), false);
                witchery_Patcher$tryPerformClayConversion(world, pos.south(), false);
                witchery_Patcher$tryPerformClayConversion(world, pos.west(), false);
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean witchery_Patcher$tryPerformClayConversion(World world, BlockPos pos, boolean checkOnly) {
        IBlockState sourceBlock = world.getBlockState(pos);
        IBlockState targetBlock = MutandisApi.getClayConversion(sourceBlock);
        Block blockAbove = world.getBlockState(pos.up()).getBlock();

        if (targetBlock != null && (blockAbove == Blocks.WATER || blockAbove == Blocks.FLOWING_WATER)) {
            if (!checkOnly) {
                world.setBlockState(pos, targetBlock);
                if (!world.isRemote) {
                    world.playSound(null, 0.5 + (double) pos.getX(), 1.5 + (double) pos.getY(), 0.5 + (double) pos.getZ(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                    WitcheryNetworkChannel.sendToAllAround(new PacketParticles(0.5 + (double) pos.getX(), 1.5 + (double) pos.getY(), 0.5 + (double) pos.getZ(), 1.0F, 1.0F, EnumParticleTypes.SPELL_INSTANT), world, 0.5 + (double) pos.getX(), 1.5 + (double) pos.getY(), 0.5 + (double) pos.getZ());
                }
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean witchery_Patcher$performGrassConversion(World world, BlockPos pos) {
        IBlockState sourceBlock = world.getBlockState(pos);
        IBlockState targetBlock = MutandisApi.getGrassConversion(sourceBlock);

        if (targetBlock != null) {
            if (world.rand.nextBoolean()) {
                world.setBlockState(pos, targetBlock);
                world.playSound(null, pos.getX(), pos.getY() + 1, pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                WitcheryNetworkChannel.sendToAllAround(new PacketParticles(pos.getX(), pos.getY() + 1, pos.getZ(), 1.0F, 1.0F, EnumParticleTypes.SPELL_INSTANT), world, pos.up());
            }
            return true;
        }
        return false;
    }

    @Unique
    private static boolean witchery_Patcher$performMutandisConversion(World world, BlockPos pos, boolean extremis) {
        IBlockState source = world.getBlockState(pos);
        int dim = world.provider.getDimension();
        IBlockState target = MutandisApi.getConversion(source, extremis, dim);

        if (target != null) {

            // Try to retrieve the AGE property and be sure it's an INT value (was potential crash in Witchery)
            IProperty<?> sourceAge = source.getBlock().getBlockState().getProperty("age");
            int currentAge = !(sourceAge instanceof PropertyInteger) ? 0 : (Integer)source.getValue(sourceAge);

            // Set the AGE of the target block as the same of the source block, accounting for target max age
            IProperty<?> targetAge = target.getBlock().getBlockState().getProperty("age");
            if (targetAge instanceof PropertyInteger) {
                PropertyInteger pInt = (PropertyInteger) targetAge;
                int maxTargetAge = Collections.max(pInt.getAllowedValues());
                target = target.withProperty(pInt, Math.min(currentAge, maxTargetAge));
            }

            world.setBlockState(pos, target);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllAround(new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F, EnumParticleTypes.SPELL_INSTANT), world, pos);

            return true;
        }
        return false;
    }
}
