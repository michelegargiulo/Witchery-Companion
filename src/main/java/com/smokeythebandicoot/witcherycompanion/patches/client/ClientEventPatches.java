package com.smokeythebandicoot.witcherycompanion.patches.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.WitcheryResurrected;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.item.ItemWitchesClothes;


@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber
public class ClientEventPatches {

    private static final ResourceLocation BARK_TEXTURES = new ResourceLocation("witchery", "textures/gui/creatures.png");

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (!Minecraft.getMinecraft().playerController.isSpectator()) {

            EntityPlayerSP player = Minecraft.getMinecraft().player;
            ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());

            GlStateManager.pushMatrix();
            ItemStack belt = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            if (belt.getItem() == WitcheryEquipmentItems.BARK_BELT) {
                int i = Math.min(ItemWitchesClothes.getChargeLevel(belt), ItemWitchesClothes.getMaxChargeLevel(player));
                drawBarkBeltCharges(player, i, screen);
            }
            GlStateManager.popMatrix();
        }
    }

    private static void drawBarkBeltCharges(EntityPlayerSP player, int beltCharges, ScaledResolution screen) {

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

            //GlStateManager.disableDepth();
            //GlStateManager.depthMask(false);
            //GlStateManager.enableBlend();

            // Courtesy of https://forums.minecraftforge.net/topic/148833-how-can-i-draw-a-transparent-image-on-the-minecraft-hud-screen/
            //GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int iconOffsetX = 0;
            int ICON_WIDTH = 8;
            int ICON_HEIGHT = 8;
            int iconOffsetY = 248;

            GlStateManager.pushMatrix();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            for(int m = 0; m < beltCharges; ++m) {
                mc.ingameGUI.drawTexturedModalRect(tx + m * ICON_HEIGHT, l2, iconOffsetX, iconOffsetY, ICON_WIDTH, ICON_HEIGHT);
                //drawTexturedModalRect(tx + m * ICON_HEIGHT, l2, iconOffsetX, iconOffsetY, ICON_WIDTH, ICON_HEIGHT);
            }

            GlStateManager.popMatrix();

            //GlStateManager.depthMask(true);
            //GlStateManager.enableDepth();
            //GlStateManager.disableBlend();
        }

    }

    private static void drawTexturedModalRect(int x, int y, int z, int par4, int par5, int par6) {
        double zLevel = 0.0;
        float f = 0.00390625F;
        float f2 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + par6, zLevel).tex((float)z * f, (float)(par4 + par6) * f2).endVertex();
        buffer.pos((x + par5), y + par6, zLevel).tex((float)(z + par5) * f, ((float)(par4 + par6) * f2)).endVertex();
        buffer.pos((x + par5), y, zLevel).tex((float)(z + par5) * f, (float)par4 * f2).endVertex();
        buffer.pos(x, y, zLevel).tex((float)z * f, (float)par4 * f2).endVertex();
        tessellator.draw();
    }

}
