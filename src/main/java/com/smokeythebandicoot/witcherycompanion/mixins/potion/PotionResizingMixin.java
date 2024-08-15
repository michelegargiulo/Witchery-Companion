package com.smokeythebandicoot.witcherycompanion.mixins.potion;

import com.smokeythebandicoot.witcherycompanion.api.player.IEntityPlayerAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.PotionTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.msrandom.witchery.network.PacketSyncEntitySize;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.potion.PotionResizing;
import net.msrandom.witchery.resources.CreatureFormStatManager;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import net.msrandom.witchery.util.WitcheryUtils;
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

    /** This Mixin reworks the scale factors of the Resizing Potion, so that they can be customized */
    @Inject(method = "getScaleFactor", remap = false, cancellable = true, at = @At("HEAD"))
    private static void modifyResizingRates(int amplifier, CallbackInfoReturnable<Float> cir) {
        if (!PotionTweaks.resizing_tweakCustomSizes) return;
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

    /** This Mixin replaces vanilla logic for updating player size, by accounting for the PotionResizing effect.
     * Also modifies eyeHeight and stepHeight */
    @Inject(method = "onLivingUpdate", remap = false, cancellable = true, at = @At(value = "HEAD"))
    private void accountCrouchHeightForResizing(World world, EntityLivingBase entity, LivingEvent.LivingUpdateEvent event, int amplifier, int duration, CallbackInfo ci) {
        if (!PotionTweaks.resizing_fixEffectOnPlayers) {
            return;
        }

        // Client is updated every tick, server every second
        if (world.isRemote || entity.ticksExisted % 20 == 0) {

            float reductionFactor = 0.03F * (float)(entity.world.isRemote ? 1 : 20);
            EntitySizeInfo sizeInfo = new EntitySizeInfo(entity);
            float targetResizingScale = getScaleFactor(amplifier);

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                IEntityPlayerAccessor playerResizeInfo = (IEntityPlayerAccessor) player;
                float currentScale = playerResizeInfo.accessor_getCurrentResizingScale();
                // Current player height should be defaultHeight * currentScale;
                // Update current scale, then update player size
                if (currentScale != targetResizingScale) {
                    if (Math.abs(currentScale - targetResizingScale) < reductionFactor) {
                        currentScale = targetResizingScale;
                    } else if (currentScale > targetResizingScale) {
                        currentScale -= reductionFactor;
                    } else {
                        currentScale += reductionFactor;
                    }
                }
                // Set this parameter in EntityPlayer. This scale will be
                // accounted for in EntityPlayer.updateSize()
                playerResizeInfo.accessor_setCurrentResizingScale(currentScale);

            }
            else {
                float requiredHeight = sizeInfo.defaultHeight * targetResizingScale;
                float requiredWidth = sizeInfo.defaultWidth * targetResizingScale;
                float currentHeight = event.getEntityLiving().height;
                if (requiredHeight != currentHeight) {
                    if (targetResizingScale < 1.0F) {
                        ResizingUtils.setSize(entity, Math.max(entity.width - reductionFactor, requiredWidth), Math.max(currentHeight - reductionFactor, requiredHeight));
                    } else {
                        ResizingUtils.setSize(entity, Math.min(entity.width + reductionFactor, requiredWidth), Math.min(currentHeight + reductionFactor, requiredHeight));
                    }
                }

            }
            
            // Every second, send packet to update client entity size
            if (!world.isRemote) {
                WitcheryNetworkChannel.sendToAll(new PacketSyncEntitySize(entity));
            }
        }

        ci.cancel();
    }


}
