package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

        original.call(instance, width * witchery_Patcher$resizeScaleWidth, height * witchery_Patcher$resizeScaleHeight);

        // Before updating size, check if scale due to ResizingPotion needs to be applied
        /*for (PotionEffect pEffect : instance.getActivePotionEffects()) {
            if (pEffect.getPotion() instanceof PotionResizing) {

                float reductionFactor = 0.03F * (float)(instance.world.isRemote ? 1 : 20);
                EntitySizeInfo sizeInfo = new EntitySizeInfo(instance);
                float scale = PotionResizing.getScaleFactor(pEffect.getAmplifier());
                float requiredHeight = sizeInfo.defaultHeight * scale;
                float requiredWidth = sizeInfo.defaultWidth * scale;
                float currentHeight = instance.height;

                if (requiredHeight != currentHeight) {
                    if (!instance.world.isRemote) {
                        instance.eyeHeight = currentHeight * 0.92F;
                    }

                    instance.stepHeight = scale < 1.0F ? 0.0F : scale - 1.0F;
                    if (scale < 1.0F) {
                        original.call(instance, Math.max(instance.width - reductionFactor, requiredWidth), Math.max(currentHeight - reductionFactor, requiredHeight));
                    } else {
                        original.call(instance, Math.max(instance.width + reductionFactor, requiredWidth), Math.max(currentHeight + reductionFactor, requiredHeight));
                    }
                    instance.sendMessage(new TextComponentString("found resize"));
                    return;
                }

                // Found potion effect, but requiredHeight = currentHeight, so just call original
                break;

            }
        }

        // No ResizingPotion found or no size chance needed. Call original
        original.call(instance, width, height);
        instance.sendMessage(new TextComponentString("resize not found"));*/
    }

    @Inject(method = "getEyeHeight", remap = true, at = @At(value = "HEAD"), cancellable = true)
    public void modifyEyeHeight(CallbackInfoReturnable<Float> cir) {
        float f = getDefaultEyeHeight();

        if (this.isPlayerSleeping()) {
            f = 0.2F;

        } else if (!this.isSneaking() && this.height != 1.65F) {
            if (this.isElytraFlying() || this.height == 0.6F)
            {
                f = 0.4F;
            }

        } else {
            f -= 0.08F;
        }

        cir.setReturnValue(f * witchery_Patcher$resizeScaleHeight);
    }
}
