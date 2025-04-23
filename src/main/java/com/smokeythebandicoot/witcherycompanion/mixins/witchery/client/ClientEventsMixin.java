package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.msrandom.witchery.WitcheryResurrected;
import net.msrandom.witchery.client.ClientEvents;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/** Mixins:
 * [Bugfix] Fix Bark Belt charges rendering transparency issues over armor bar
 * **/
@Mixin(ClientEvents.class)
public abstract class ClientEventsMixin {

    @Shadow(remap = false) @Final
    private static ResourceLocation BARK_TEXTURES;

    @Shadow(remap = false)
    private static void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
    }

    /** Save GL state, apply correct blending function and only then call drawTexturedModalRect. Then restore state **/
    @Inject(method = "drawBarkBeltCharges", remap = false, at = @At("HEAD"), cancellable = true)
    private static void fixBarkBeltChargesRendering(EntityPlayerSP player, int beltCharges, ScaledResolution screen, CallbackInfo ci) {

        if (beltCharges > 0 && !player.capabilities.isCreativeMode) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.getTextureManager().bindTexture(BARK_TEXTURES);
            int tx = screen.getScaledWidth() / 2 - 91;
            int par2 = screen.getScaledHeight();
            IAttributeInstance attributeinstance = mc.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            int i2 = par2 - 39;
            float f = (float)attributeinstance.getAttributeValue();
            float f2 = mc.player.getAbsorptionAmount();
            int j2 = MathHelper.ceil((f + f2) / 2.0F / 10.0F);
            int k2 = Math.max(10 - (j2 - 2), 3);
            int l2 = WitcheryResurrected.Companion.isTinkersPresent() ? i2 - 10 : i2 - (j2 - 1) * k2 - 10;

            // Save state
            GlStateManager.pushMatrix();

            // Apply functions
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableAlpha();

            int iconOffsetX = 0;
            int ICON_WIDTH = 8;
            int ICON_HEIGHT = 8;
            int iconOffsetY = 248;

            // Draw the rect
            for(int m = 0; m < beltCharges; ++m) {
                drawTexturedModalRect(tx + m * ICON_HEIGHT, l2, iconOffsetX, iconOffsetY, ICON_WIDTH, ICON_HEIGHT);
            }

            // Restore the state
            GlStateManager.popMatrix();
        }

        ci.cancel();

    }



}
