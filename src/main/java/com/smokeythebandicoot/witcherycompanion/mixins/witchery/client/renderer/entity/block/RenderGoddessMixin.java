package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.msrandom.witchery.block.entity.TileEntityStatueGoddess;
import net.msrandom.witchery.client.model.ModelGoddess;
import net.msrandom.witchery.client.renderer.entity.block.RenderGoddess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


/**
 * Mixins:
 * [Bugfix] Render a small model if the small hitbox is enabled
 */
@Mixin(RenderGoddess.class)
public abstract class RenderGoddessMixin extends TileEntitySpecialRenderer<TileEntityStatueGoddess> {

    /** This Mixin injects around the model.render() call to set its scale to 0.8f and translates 0.3 units down **/
    @WrapOperation(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityStatueGoddess;DDDFIF)V", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/client/model/ModelGoddess;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderAtSmallScale(ModelGoddess instance, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.goddessStatue_fixFlyingOnServers) {
            GlStateManager.translate(0, 0.3, 0);
            original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale * 0.8f);
            GlStateManager.translate(0, -0.3, 0);
        } else {
            original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }


}
