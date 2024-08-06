package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.init.WitcheryPotionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix eyeHeight not scaling with Player height
 */
@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {

    @Unique
    public float witchery_Patcher$currentResizingScale = 1.0f;

    @Shadow(remap = false)
    public float eyeHeight;


    private EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "updateSize", remap = true, cancellable = true, at = @At(value = "HEAD"))
    private void injectBeforeWitcheryTransformer(CallbackInfo ci) {
        // If Aqua Acrobatics mod is loaded, then do not perform this. AA compat
        // is handled on the AA side
        if (!ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers ||
                Loader.isModLoaded("aquaacrobatics")) {
            return;
        }

        float width;
        float height;

        if (this.isElytraFlying()) {
            width = 0.6F;
            height = 0.6F;
        } else if (this.isPlayerSleeping()) {
            width = 0.2F;
            height = 0.2F;
        } else if (this.isSneaking()) {
            width = 0.6F;
            height = 1.65F;
        } else {
            width = 0.6F;
            height = 1.8F;
        }

        // Include this so that when effect wears off the player is forced into
        // it's original size
        if (this.isPotionActive(WitcheryPotionEffects.RESIZING)) {
            width *= witchery_Patcher$currentResizingScale;
            height *= witchery_Patcher$currentResizingScale;
        } else {
            witchery_Patcher$currentResizingScale = 1.0f;
        }
        this.eyeHeight = height * 0.92f;

        if (width != this.width || height != this.height) {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)width, axisalignedbb.minY + (double)height, axisalignedbb.minZ + (double)width);

            if (!this.world.collidesWithAnyBlock(axisalignedbb)) {
                this.setSize(width, height);
            }
        }

        ci.cancel();
        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPlayerPostTick((EntityPlayer)(Object)this);
    }


    /** This Mixin makes it so that when returning player height the Brew of Resizing scale
     * is taken into account. Note that before invoking Entity.setSize() player height is re-set
     * to 1.8f or 1.65f if crouching, because for some reason this value is lost in EntityPlayer class
     * when checking if (f != this.width || f1 != this.height) on line 405. */
    /*@WrapOperation(method = "updateSize", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSize(FF)V", remap = true))
    private void checkResizeWhenUpdatingSize(EntityPlayer instance, float width, float height, Operation<Void> original) {

        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers) {

            //Utils.logChat(height * witchery_Patcher$resizeScaleHeight);
            if (instance.isSneaking()) {
                height = 1.65f;
            } else {
                height = 1.8f;
            }
            witchery_Patcher$effectiveWidth = width * witchery_Patcher$resizeScaleWidth;
            witchery_Patcher$effectiveHeight = height * witchery_Patcher$resizeScaleHeight;
            original.call(instance, witchery_Patcher$effectiveWidth, witchery_Patcher$effectiveHeight);
            return;
        }
        original.call(instance, width, height);

    }

     */

    /** This Mixin forces Vanilla to update player size even if there is no player-triggered size
     * change. This fixes a bug that makes Resizing potion effect start shrinking or enlarging player
     * only when the player sneaks or its height is changes in some other way */
    /*@Inject(method = "updateSize", remap = true, at = @At(value = "INVOKE", remap = false, shift = At.Shift.BEFORE,
            target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPlayerPostTick(Lnet/minecraft/entity/player/EntityPlayer;)V"))
    private void forceUpdateSize(CallbackInfo ci) {
        if (witchery_Patcher$forceUpdateSize && witchery_Patcher$effectiveWidth > 0 && witchery_Patcher$effectiveHeight > 0 &&
                ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers) {

            Utils.logChat("Force update");
            witchery_Patcher$forceUpdateSize = false;
            setSize(witchery_Patcher$effectiveWidth, witchery_Patcher$effectiveHeight);
            //this.eyeHeight = witchery_Patcher$effectiveHeight * 0.92;
        }
    }

     */
}
