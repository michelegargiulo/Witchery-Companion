package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Bottling level skill not increasing
 */
@Mixin(value = BlockWitchCauldron.class, remap = false)
public class BlockWitchCauldronMixin {

    @Unique
    BrewActionList witchery_Patcher$preservedActions = null;

    // Save Cauldron actions before they get invalidated by the readNBT operation
    @Inject(method = "onBlockActivated", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;)V"))
    public void WPpreserveActionsBeforeEmptyingCauldron(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease) {
            TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
            if (cauldron != null) {
                witchery_Patcher$preservedActions = cauldron.getActions();
            }
        }
    }


    // Uses wrap operation to direct the original call with the always-empty brewActionList to a fixed
    // call that uses the preserved brewActionList
    @WrapOperation(method = "onBlockActivated", remap = false,
    at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/BlockWitchCauldron;processSkillChanges(Lnet/minecraft/entity/player/EntityPlayer;Lnet/msrandom/witchery/brewing/action/BrewActionList;)V"))
    public void WPrestorePreservedActions(BlockWitchCauldron instance, EntityPlayer player, BrewActionList actionList, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease
                && witchery_Patcher$preservedActions != null) {
            original.call(instance, player, witchery_Patcher$preservedActions);
        } else {
            original.call(instance, player, actionList);
        }
    }

}
