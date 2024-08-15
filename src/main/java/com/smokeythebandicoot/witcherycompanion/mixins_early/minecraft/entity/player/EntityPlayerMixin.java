package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.player.IEntityPlayerAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
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
 [Priority] After Aqua Acrobatics (1000). It cancels the event and patch is not executed
    if AA is installed. AA compat is added in other ways
 */
@Mixin(value = EntityPlayer.class, priority = 1001)
public abstract class EntityPlayerMixin extends EntityLivingBase implements IEntityPlayerAccessor {

    @Unique
    public float witchery_Patcher$currentResizingScale = 1.0f;

    @Unique
    private float witchery_Patcher$currentFormWidthScale = 1.0f;

    @Unique
    private float witchery_Patcher$currentFormHeightScale = 1.5f;

    @Unique
    private float witchery_Patcher$currentFormEyeHeightScale = 1.0f;

    @Unique
    private float witchery_Patcher$currentFormStepHeightScale = 1.0f;

    @Shadow(remap = false)
    public float eyeHeight;

    @Shadow(remap = false)
    public abstract float getDefaultEyeHeight();

    private EntityPlayerMixin(World worldIn) {
        super(worldIn);
    }

    /** This Mixin updates player size accounting for Resizing Potion and player transformation. This patch
     * disables itself if Aqua Acrobatic is loaded, as it is handled on AA side */
    @Inject(method = "updateSize", remap = true, cancellable = true, at = @At(value = "HEAD"))
    private void updateSizeBeforeWitcheryAsm(CallbackInfo ci) {
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
        this.stepHeight = witchery_Patcher$currentResizingScale * 0.5f;

        // Account for transformation when computing size
        // Those params are set in ShapeShiftMixin class
        width *= witchery_Patcher$currentFormWidthScale;
        height *= witchery_Patcher$currentFormHeightScale;
        this.eyeHeight = this.getDefaultEyeHeight()
                * witchery_Patcher$currentResizingScale
                * witchery_Patcher$currentFormHeightScale
                * witchery_Patcher$currentFormEyeHeightScale;
        this.stepHeight *= witchery_Patcher$currentFormStepHeightScale;

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

    /** This Mixin updates player eye height by cancelling Witchery ASM-injected call to ResizingUtils.getPlayerEyeHeight
     * and replacing it with a version that uses the injected variables. This patch
     *  disables itself if Aqua Acrobatic is loaded, as it is handled on AA side */
    @WrapOperation(method = "getEyeHeight", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/ResizingUtils;getPlayerEyeHeight(Lnet/minecraft/entity/player/EntityPlayer;F)F"))
    private float updateEyeHeightBeforeWitcheryAsm(EntityPlayer player, float eyeHeight, Operation<Float> original) {
        if (!ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers ||
                Loader.isModLoaded("aquaacrobatics")) {
            return original.call(player, eyeHeight);
        }
        return eyeHeight;
    }

    @Override
    public float accessor_getCurrentResizingScale() {
        return witchery_Patcher$currentResizingScale;
    }

    @Override
    public void accessor_setCurrentResizingScale(float scale) {
        this.witchery_Patcher$currentResizingScale = scale;
    }

    @Override
    public float accessor_getCurrentFormWidthScale() {
        return witchery_Patcher$currentFormWidthScale;
    }

    @Override
    public void accessor_setCurrentFormWidthScale(float scale) {
        this.witchery_Patcher$currentFormWidthScale = scale;
    }

    @Override
    public float accessor_getCurrentFormHeightScale() {
        return witchery_Patcher$currentFormHeightScale;
    }

    @Override
    public void accessor_setCurrentFormHeightScale(float scale) {
        this.witchery_Patcher$currentFormHeightScale = scale;
    }

    @Override
    public float accessor_getCurrentFormEyeHeightScale() {
        return witchery_Patcher$currentFormEyeHeightScale;
    }

    @Override
    public void accessor_setCurrentFormEyeHeightScale(float scale) {
        this.witchery_Patcher$currentFormEyeHeightScale = scale;
    }

    @Override
    public float accessor_getCurrentFormStepHeightScale() {
        return witchery_Patcher$currentFormStepHeightScale;
    }

    @Override
    public void accessor_setCurrentFormStepHeightScale(float scale) {
        this.witchery_Patcher$currentFormStepHeightScale = scale;
    }

}
