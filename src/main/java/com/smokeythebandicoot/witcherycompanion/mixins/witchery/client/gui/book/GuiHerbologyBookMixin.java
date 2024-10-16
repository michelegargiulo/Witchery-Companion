package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.gui.book;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.msrandom.witchery.client.gui.book.GuiHerbologyBook;
import net.msrandom.witchery.client.gui.book.WitcheryGuiDynamicBook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiHerbologyBook.class)
public abstract class GuiHerbologyBookMixin extends WitcheryGuiDynamicBook {

    @Final
    @Shadow(remap = false)
    public static List PAGES;

    private GuiHerbologyBookMixin(ItemStack stack, EnumHand hand, boolean b) {
        super(stack, hand, b);
    }

    @Inject(method = "drawScreen", remap = false, cancellable = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", remap = true))
    public void WPfixItemRendering(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        if (ModConfig.PatchesConfiguration.BookTweaks.herbologyBook_fixPlantRendering) {
            ItemStack plant = (ItemStack) PAGES.get(this.getPage() - 1);
            int xStart = (this.width - 166) / 2;

            GlStateManager.scale(1.5F, 1.5F, 1.5F);
            this.itemRender.renderItemAndEffectIntoGUI(plant, xStart, 29);
            GlStateManager.popMatrix();
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    protected abstract int getLastPage();
}
