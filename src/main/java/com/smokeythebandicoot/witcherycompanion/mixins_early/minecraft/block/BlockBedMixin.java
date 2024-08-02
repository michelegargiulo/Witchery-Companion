package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBed.class)
public abstract class BlockBedMixin extends BlockHorizontal implements ICursableTrigger {

    private BlockBedMixin(Material materialIn) {
        super(materialIn);
    }

    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = true,
          target = "Lnet/minecraft/entity/player/EntityPlayer;trySleep(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/player/EntityPlayer$SleepResult;"))
    private EntityPlayer.SleepResult triggerOnPlayerSleep(EntityPlayer instance, BlockPos bedLocation, Operation<EntityPlayer.SleepResult> original) {
        EntityPlayer.SleepResult result = original.call(instance, bedLocation);
        if (result == EntityPlayer.SleepResult.OK &&
            TriggeredDispersalTweaks.enable_dispersalRework &&
            TriggeredDispersalTweaks.enable_beds) {
            this.onTrigger(instance.world, bedLocation, instance);
        }
        return result;
    }
}
