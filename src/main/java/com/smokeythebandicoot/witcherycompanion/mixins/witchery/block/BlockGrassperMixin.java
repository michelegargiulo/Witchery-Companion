package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockGrassper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Mixins:
 * [Feature] Make them Cursable triggers (trigger when right-clicked)
 */
@Mixin(BlockGrassper.class)
public abstract class BlockGrassperMixin extends BlockContainer implements ICursableTrigger {


    @Shadow(remap = false)  @Final @Mutable
    private static AxisAlignedBB AABB;


    private BlockGrassperMixin(Material materialIn) {
        super(materialIn);
    }


    @Inject(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityGrassper;getStackInSlot(I)Lnet/minecraft/item/ItemStack;"))
    private void triggerOnBlockActivation(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        this.onTrigger(world, pos, player);
    }

    @Inject(method = "<clinit>", remap = false, at = @At("TAIL"))
    private static void tightenAABB(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.grassper_tweakReduceAABB) {
            AABB = new AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 0.4, 0.8);
        }
    }

    @Override
    public boolean isTriggerEnabled() {
        return TriggeredDispersalTweaks.enable_dispersalRework &&
            TriggeredDispersalTweaks.enable_grasspers;
    }
}
