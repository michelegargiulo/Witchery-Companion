package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.patches.mixin_invokers.IRenderItemInvoker;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin implements IRenderItemInvoker {

    @Unique
    private Integer witchery_Patcher$color = null;

    @WrapOperation(method = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/client/ForgeHooksClient;renderLitItem(Lnet/minecraft/client/renderer/RenderItem;Lnet/minecraft/client/renderer/block/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V"))
    private void injectRenderItem(RenderItem renderItem, IBakedModel model, int color, ItemStack stack, Operation<Void> original) {
        original.call(renderItem, model, witchery_Patcher$color == null ? color : this.witchery_Patcher$color, stack);
        restoreColor();
    }

    @Override
    public void setColor(int color) {
        this.witchery_Patcher$color = color;
    }

    @Override
    public int getColor() {
        return this.witchery_Patcher$color;
    }

    public void restoreColor() {
        this.witchery_Patcher$color = null;
    }
}
