package com.smokeythebandicoot.witcherycompanion.mixins.witchery.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.vanillaaccessors.player.IEntityPlayerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.msrandom.witchery.network.PacketSyncEntitySize;
import net.msrandom.witchery.network.WitcheryNetworkPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix ResizingPotion not working on Players and fix Transform width/height compat with resizing potion
 */
@Mixin(PacketSyncEntitySize.class)
public abstract class PacketSyncEntitySizeMixin implements WitcheryNetworkPacket {

    private float playerResizingPotionScale;
    private float playerTransformWidthScale;
    private float playerTransformHeightScale;
    private float playerTransformEyeHeightScale;
    private float playerTransformStepHeightScale;

    /** This Mixin injects into write method to write new fields into the buffer */
    @Inject(method = "write", remap = false, at = @At("TAIL"))
    private void writeScaleSizes(PacketBuffer buffer, CallbackInfo ci) {
        buffer.writeFloat(this.playerResizingPotionScale);
        buffer.writeFloat(this.playerTransformWidthScale);
        buffer.writeFloat(this.playerTransformHeightScale);
        buffer.writeFloat(this.playerTransformEyeHeightScale);
        buffer.writeFloat(this.playerTransformStepHeightScale);
    }

    /** This Mixin injects just after the ResizingUtils.setSize to capture the target entity
     * and sets the new fields if it is an instanceof IEntityPlayerAccessor */
    @WrapOperation(method = "apply", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/ResizingUtils;setSize(Lnet/minecraft/entity/Entity;FF)V"))
    private void applyScaleSizes(Entity entity, float width, float height, Operation<Void> original) {
        original.call(entity, width, height);
        if (entity instanceof IEntityPlayerAccessor) {
            IEntityPlayerAccessor accessor = (IEntityPlayerAccessor) entity;
            if (this.playerResizingPotionScale != -1.0f) {
                accessor.witcherycompanion$accessor$setCurrentResizingScale(this.playerResizingPotionScale);
            }
            if (this.playerTransformWidthScale != -1.0f) {
                accessor.witcherycompanion$accessor$setCurrentFormWidthScale(this.playerTransformWidthScale);
            }
            if (this.playerTransformHeightScale != -1.0f) {
                accessor.witcherycompanion$accessor$setCurrentFormHeightScale(this.playerTransformHeightScale);
            }
            if (this.playerTransformEyeHeightScale != -1.0f) {
                accessor.witcherycompanion$accessor$setCurrentFormEyeHeightScale(this.playerTransformEyeHeightScale);
            }
            if (this.playerTransformStepHeightScale != -1.0f) {
                accessor.witcherycompanion$accessor$setCurrentFormStepHeightScale(this.playerTransformStepHeightScale);
            }
        }
    }

    /** This Mixin Injects the new Fields inside the packet, if the Entity is a EntityPlayer */
    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;)V", remap = false, at = @At("TAIL"))
    private void initNewFieldsFromEntity(Entity entity, CallbackInfo ci) {
        if (entity instanceof IEntityPlayerAccessor) {
            IEntityPlayerAccessor accessor = (IEntityPlayerAccessor) entity;
            this.playerResizingPotionScale = accessor.witcherycompanion$accessor$getCurrentResizingScale();
            this.playerTransformWidthScale = accessor.witcherycompanion$accessor$getCurrentFormWidthScale();
            this.playerTransformHeightScale = accessor.witcherycompanion$accessor$getCurrentFormHeightScale();
            this.playerTransformEyeHeightScale = accessor.witcherycompanion$accessor$getCurrentFormEyeHeightScale();
            this.playerTransformStepHeightScale = accessor.witcherycompanion$accessor$getCurrentFormStepHeightScale();
        } else {
            this.playerResizingPotionScale = -1.0f;
            this.playerTransformWidthScale = -1.0f;
            this.playerTransformHeightScale = -1.0f;
            this.playerTransformEyeHeightScale = -1.0f;
            this.playerTransformStepHeightScale = -1.0f;
        }
    }

    /** This Mixin reads the new fields from the buffer */
    @Inject(method = "<init>(Lnet/minecraft/network/PacketBuffer;)V", remap = false, at = @At("TAIL"))
    private void initNewFieldsFromBuffer(PacketBuffer buffer, CallbackInfo ci) {
        this.playerResizingPotionScale = buffer.readFloat();
        this.playerTransformWidthScale = buffer.readFloat();
        this.playerTransformHeightScale = buffer.readFloat();
        this.playerTransformEyeHeightScale = buffer.readFloat();
        this.playerTransformStepHeightScale = buffer.readFloat();
    }

}
