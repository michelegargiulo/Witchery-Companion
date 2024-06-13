package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {

    @Shadow(remap = false)
    public abstract float getDefaultEyeHeight();

    @Unique
    public float witchery_Patcher$resizeScaleWidth = 1.0f;

    @Unique
    public float witchery_Patcher$resizeScaleHeight = 1.0f;

    private EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    @WrapOperation(method = "updateSize", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSize(FF)V", remap = true))
    public void checkResizeWhenUpdatingSize(EntityPlayer instance, float width, float height, Operation<Void> original) {

        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers) {
            original.call(instance, width * witchery_Patcher$resizeScaleWidth, height * witchery_Patcher$resizeScaleHeight);
            return;
        }
        original.call(instance, width, height);

    }

    @Inject(method = "getEyeHeight", remap = true, at = @At(value = "HEAD"), cancellable = true)
    public void modifyEyeHeight(CallbackInfoReturnable<Float> cir) {

        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers) {
            float f = getDefaultEyeHeight();

            if (this.isPlayerSleeping()) {
                f = 0.2F;

            } else if (!this.isSneaking() && this.height != 1.65F) {
                if (this.isElytraFlying() || this.height == 0.6F) {
                    f = 0.4F;
                }

            } else {
                f -= 0.08F;
            }

            cir.setReturnValue(f * witchery_Patcher$resizeScaleHeight);
        }

    }
}