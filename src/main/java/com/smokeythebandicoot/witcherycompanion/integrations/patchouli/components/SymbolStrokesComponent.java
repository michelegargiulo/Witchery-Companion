package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

import java.util.*;

public class SymbolStrokesComponent implements ICustomComponent {

    @SerializedName("symbol_strokes")
    @VariableHolder
    public String[] strokes;

    @SerializedName("start_x")
    @VariableHolder
    public String offsetX;

    @SerializedName("start_y")
    @VariableHolder
    public String offsetY;

    @SerializedName("x")
    @VariableHolder
    public int x = 0;

    @SerializedName("y")
    @VariableHolder
    public int y = 0;


    private transient List<BranchStroke> strokeList;
    private transient int startX = 0;
    private transient int startY = 0;
    private static transient int ARROW_SIZE = 16;
    private static transient ResourceLocation arrowsResourceLocation =
            new ResourceLocation("witchery:textures/gui/grid.png");

    @Override
    public void build(int componentX, int componentY, int pageNum) {

        if (strokes == null) return;

        strokeList = new ArrayList<>();
        ProcessorUtils.deserializeStrokeArray(strokes, strokeList);

        try {
            startX = Integer.parseInt(offsetX);
            startY = Integer.parseInt(offsetY);
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Error reading offset values: {}", ex);
        }
    }

    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (strokeList == null)
            return;

        int curX = this.x + startX * ARROW_SIZE;
        int curY = this.y + startY * ARROW_SIZE;

        Set<Tuple<Integer, Integer>> prevSymbols = new HashSet<>();

        for (BranchStroke stroke : strokeList) {
            int u = getTextureHorizontalOffset(stroke);
            // If position was used before, override it with another color
            // (offset the texture to get the purple color)
            int v = prevSymbols.contains(new Tuple<>(curX, curY)) ? 113 : 1;
            prevSymbols.add(new Tuple<>(curX, curY));
            renderArrow(curX, curY, u, v);
            switch (stroke) {
                case DOWN:
                    curY += ARROW_SIZE;
                    break;
                case UP:
                    curY -= ARROW_SIZE;
                    break;
                case LEFT:
                    curX -= ARROW_SIZE;
                    break;
                case RIGHT:
                    curX += ARROW_SIZE;
                    break;
            }
        }
    }

    private static int getTextureHorizontalOffset(BranchStroke stroke) {
        switch (stroke) {
            case UP:
                return 1;
            case DOWN:
                return 17;
            case RIGHT:
                return 33;
            case LEFT:
                return 49;
            default:
                return 65; // A symbol is inside the texture here
        }
    }

    private static void renderArrow(int curX, int curY, int u, int v) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(arrowsResourceLocation);
        GlStateManager.pushMatrix();
        GlStateManager.translate(curX, curY, 0);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(0, 0, u, v, 14, 14, 256, 256);
        GlStateManager.popMatrix();
    }
}
