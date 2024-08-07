package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player;

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

    @Override
    public float accessor_getCurrentResizingScale() {
        return witchery_Patcher$currentResizingScale;
    }

    @Override
    public void accessor_setCurrentResizingScale(float scale) {
        this.witchery_Patcher$currentResizingScale = scale;
    }

}
