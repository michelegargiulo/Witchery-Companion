package com.smokeythebandicoot.witcherycompanion.mixins.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.PotionTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.msrandom.witchery.potion.PotionResizing;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixin:
 [Tweak] Set Custom sizes for the potion effect (Also allows to fix a glitch where the player eyes can x-ray through
    blocks at the smallest size
 */
@Mixin(PotionResizing.class)
public abstract class PotionResizingMixin {

    @Shadow(remap = false)
    public static float getScaleFactor(int amplifier) { return 0; }

    @Inject(method = "getScaleFactor", remap = false, cancellable = true, at = @At("HEAD"))
    private static void modifyResizingRates(int amplifier, CallbackInfoReturnable<Float> cir) {
        if (PotionTweaks.resizing_tweakCustomSizes) return;
        float customValue = 1.0f;
        switch (amplifier) {
            case 0:
                customValue = PotionTweaks.resizing_tweakCustomSizeSmallest;
                break;
            case 1:
                customValue = PotionTweaks.resizing_tweakCustomSizeSmaller;
                break;
            case 2:
                customValue = PotionTweaks.resizing_tweakCustomSizeBigger;
                break;
            case 3:
                customValue = PotionTweaks.resizing_tweakCustomSizeBiggest;
                break;
        }
        cir.setReturnValue(customValue);

    }

    @Inject(method = "onLivingUpdate", remap = false, at = @At(value = "HEAD"))
    private void accountCrouchHeightForResizing(World world, EntityLivingBase entity, LivingEvent.LivingUpdateEvent event, int amplifier, int duration, CallbackInfo ci) {
        if (!PotionTweaks.resizing_fixEffectOnPlayers) {
            return;
        }

        float reductionFactor = 0.03F * (float)(event.getEntity().world.isRemote ? 1 : 20);
        if (world.isRemote || entity.ticksExisted % 20 == 0) {
            EntitySizeInfo sizeInfo = new EntitySizeInfo(entity);
            float scale = getScaleFactor(amplifier);
            float requiredHeight = sizeInfo.defaultHeight * scale;
            float requiredWidth = sizeInfo.defaultWidth * scale;
            float currentHeight = event.getEntityLiving().height;
            if (requiredHeight != currentHeight) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entity;
                    if (!world.isRemote) {
                        player.eyeHeight = currentHeight * 0.92F;
                    }
                }

                entity.stepHeight = scale < 1.0F ? 0.0F : scale - 1.0F;
                if (scale < 1.0F) {
                    ResizingUtils.setSize(entity, Math.max(entity.width - reductionFactor, requiredWidth), Math.max(currentHeight - reductionFactor, requiredHeight));
                } else {
                    ResizingUtils.setSize(entity, Math.min(entity.width + reductionFactor, requiredWidth), Math.min(currentHeight + reductionFactor, requiredHeight));
                }
            }
        }
    }


}
