package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.msrandom.witchery.block.entity.TileEntityPoppetShelf;
import net.msrandom.witchery.client.renderer.entity.block.RenderPoppetShelf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix Poppet rendering upside-down
 */
@Mixin(value = RenderPoppetShelf.class)
public abstract class RenderPoppetShelfMixin {

    /*@WrapOperation(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityPoppetShelf;DDDFIF)V", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V"))
    public void WPrenderPoppetsUpsideUp_1(RenderManager instance, Entity entity, double x, double y, double z,
                                        float yaw, float partialTicks, boolean b, Operation<Void> original) {

        double nx = ModConfig.newX;
        double ny = ModConfig.newY;
        double nz = ModConfig.newX;
        float nyaw = ModConfig.newYaw;
        original.call(instance, entity, nx, ny, nz, nyaw, partialTicks, b);
    }

    @WrapOperation(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityPoppetShelf;DDDFIF)V", remap = true,
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;")),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
    public void WPrenderPoppetsUpsideUp_2(float angle, float x, float y, float z, Operation<Void> original) {

        //float nXr = ModConfig.newXr;
        //float nYr = ModConfig.newYr;
        //float nZr = ModConfig.newZr;
        original.call(angle, x, y, z);
    }*/

    @Inject(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityPoppetShelf;DDDFIF)V", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V", remap = true))
    public void WPrenderPoppetsUpsideUp(TileEntityPoppetShelf te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.poppetShelf_fixUpsideDownPoppetRendering) {
            GlStateManager.translate(0.0f, 1.0f, 0.0f);
            GlStateManager.scale(1.0f, -1.0f, 1.0f);
        }
    }

}
