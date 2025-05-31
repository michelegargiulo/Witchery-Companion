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
    private static final transient int ARROW_SIZE = 16;
    private static final transient ResourceLocation arrowsResourceLocation =
            new ResourceLocation("witchery:textures/gui/grid.png");

    @Override
    public void build(int componentX, int componentY, int pageNum) {

        if (strokes == null) return;

        strokeList = new ArrayList<>();
        ProcessorUtils.deserializeStrokeArray(strokes, strokeList);

        try {
            if (offsetX == null || offsetX.isEmpty() || offsetY == null || offsetY.isEmpty())
                return;
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

        Set<StrokePosition> prevSymbols = new HashSet<>();
        boolean isFirst = true;

        for (BranchStroke stroke : strokeList) {
            int u = getTextureHorizontalOffset(stroke);
            // If position was used before, override it with another color
            // (offset the texture to get the purple color)
            int v = prevSymbols.contains(new StrokePosition(curX, curY)) ? 112 : 0;
            prevSymbols.add(new StrokePosition(curX, curY));
            renderArrow(isFirst, curX, curY, u, v);
            isFirst = false;
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
                return 0;
            case DOWN:
                return 16;
            case RIGHT:
                return 32;
            case LEFT:
                return 48;
            default:
                return 64; // A symbol is inside the texture here
        }
    }

    private static void renderArrow(boolean isFirst, int curX, int curY, int u, int v) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(arrowsResourceLocation);
        GlStateManager.pushMatrix();
        GlStateManager.translate(curX, curY, 0);
        GlStateManager.scale(1.2f, 1.2f, 1.2f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        if (!isFirst) {
            u++;
            v++;
        }
        int w = isFirst ? 16 : 14;
        int p = isFirst ? 0 : 1;
        Gui.drawModalRectWithCustomSizedTexture(p, p, u, v, w, w, 256, 256);
        GlStateManager.popMatrix();
    }

    private static class StrokePosition {
        private int posX;
        private int posY;

        private StrokePosition(int x, int y) {
            this.posX = x;
            this.posY = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StrokePosition that = (StrokePosition) o;
            return posX == that.posX && posY == that.posY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(posX, posY);
        }
    }

}
