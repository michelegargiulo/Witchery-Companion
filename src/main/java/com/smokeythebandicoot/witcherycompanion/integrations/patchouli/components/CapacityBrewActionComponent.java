package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.CapacityBrewActionDTO;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;


public class CapacityBrewActionComponent extends BrewActionComponent<CapacityBrewActionDTO> {

    @SerializedName("increment")
    @VariableHolder
    public String _increment;

    @SerializedName("removes_ceiling")
    @VariableHolder
    public String _removesCeiling;

    @SerializedName("removes_ceiling_text")
    @VariableHolder
    public String _removesCeilingText;

    @SerializedName("removes_ceiling_tooltip")
    @VariableHolder
    public String _removesCeilingTooltip;


    private transient String increment = null;
    private transient Boolean removesCeiling = null;
    private transient String removesCeilingText = null;
    private transient String removesCeilingTooltip = null;
    private transient Boolean isSecret = null;
    private transient String secretText = null;
    private transient String secretTooltip = null;
    private transient BookTextRenderer textRenderer;


    @Override
    public void onBuild() {

        super.onBuild();
        this.increment = getTransform(this._increment, dto, d -> d.increment, "+?");
        this.isSecret = getTransform(this._isSecret, Utils::tryParseBool, dto, d -> d.isSecret, false);
        this.secretText = getTransform(this._secretText, dto, d -> d.secretText, "");
        this.secretTooltip = getTransform(this._secretTooltip, dto, d -> d.secretTooltip, "");
        this.removesCeiling = getTransform(this._removesCeiling, Utils::tryParseBool, dto, d -> d.removesCeiling, false);
        this.removesCeilingText = getTransform(this._removesCeilingText, dto, d -> d.removesCeilingText, "");
        this.removesCeilingTooltip = getTransform(this._removesCeilingTooltip, dto, d -> d.removesCeilingTooltip, "");

    }

    /** Called every render tick. No special transformations are applied, so you're responsible
     * for putting everything in the right place. */
    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {

        // Draw the ItemStack
        context.renderItemStack(this.x, this.y, mouseX, mouseY, this.stack);

        // Draw text
        if (this.textRenderer != null) {
            this.textRenderer.render(mouseX, mouseY);
        }

    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        if (context.getGui() instanceof GuiBook) {

            // Draw the "+X" string (always present)
            StringBuilder sb = new StringBuilder();
            sb.append(this.increment);

            // Decorate with more info, if any
            if (this.isSecret || this.removesCeiling) {
                sb.append(" (");
                if (this.isSecret)
                    sb.append("$(t:").append(secretTooltip).append(")").append(secretText).append("$(/t)");
                if (this.removesCeiling) {
                    if (this.isSecret) sb.append(", ");
                    sb.append("$(t:").append(removesCeilingTooltip).append(")").append(removesCeilingText).append("$(/t)");
                }
                sb.append(")");
            }

            GuiBook guiBook = (GuiBook) context.getGui();
            this.textRenderer = new BookTextRenderer(guiBook, sb.toString(), this.x + 18, this.y + 4);
        }
    }
}
