package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.CapacityBrewActionProcessor;
import net.minecraft.client.Minecraft;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class CapacityBrewActionComponent implements ICustomComponent {

    @SerializedName("capacity_brew_item")
    @VariableHolder
    public String serializedCapacityBrew = null;

    @SerializedName("secret_text")
    @VariableHolder
    public String secretText = "";

    @SerializedName("removes_ceiling_text")
    @VariableHolder
    public String removesCeilingText = "";

    @SerializedName("secret_tooltip")
    @VariableHolder
    public String secretTooltip = "";

    @SerializedName("removes_ceiling_tooltip")
    @VariableHolder
    public String removesCeilingTooltip = "";


    /** ========== NON-JSON VARIABLES ========== **/
    private transient int x = 0;
    private transient int y = 0;
    private transient CapacityBrewActionProcessor.CapacityBrewActionInfo info = null;
    private transient BookTextRenderer textRenderer;


    /** ========== OVERRIDES ========== **/
    /** Called when this component is built. Take the chance to read variables and set
     * any local positions here. */
    @Override
    public void build(int componentX, int componentY, int pageNum) {
        // Can return null
        info = new CapacityBrewActionProcessor.CapacityBrewActionInfo();
        info.deserialize(serializedCapacityBrew);
        this.x = componentX;
        this.y = componentY;
    }

    /** Called every render tick. No special transformations are applied, so you're responsible
     * for putting everything in the right place. */
    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {

        if (info == null || info.stack == null)
            return;

        // Draw the ItemStack
        context.renderItemStack(this.x, this.y, mouseX, mouseY, info.stack);

        if (this.textRenderer != null) {
            this.textRenderer.render(mouseX, mouseY);
        }

    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        if (context.getGui() instanceof GuiBook) {

            // Draw the "+X" string (always present)
            StringBuilder sb = new StringBuilder("+");
            sb.append(info.increment);

            // Decorate with more info, if any
            if (info.secret || info.removesCeiling) {
                sb.append(" (");
                if (info.secret)
                    sb.append("$(t:").append(secretTooltip).append(")").append(secretText).append("$(/t)");
                if (info.removesCeiling) {
                    if (info.secret) sb.append(", ");
                    sb.append("$(t:").append(removesCeilingTooltip).append(")").append(removesCeilingText).append("$(/t)");
                }
                sb.append(")");
            }

            GuiBook guiBook = (GuiBook) context.getGui();
            this.textRenderer = new BookTextRenderer(guiBook, sb.toString(), this.x + 18, this.y + 4);
        }
    }
}
