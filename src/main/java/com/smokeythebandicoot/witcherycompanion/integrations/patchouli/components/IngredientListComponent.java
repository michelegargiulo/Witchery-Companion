package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.patches.mixin_invokers.IRenderItemInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;

public class IngredientListComponent implements ICustomComponent {

    // Json variables have to have the @SerializedName annotation
    /** ========== JSON VARIABLES ========== **/

    // The inputs. They are a String array, and can be formatted in multiple ways
    // If items are inserted manually into json, it is recommended to use one element per line
    // "inputs": ["item1", "item2", "item3", ... , "itemN"]
    // If processed by a processor, a String with multiple items can be processed instead. This is because
    // Patchouli limits Processors to return a single String (not String[] or other objects), so we serialize the list
    // So, this works, too
    // "inputs": ["item1,item2,item3", "item4", ... , "itemN"] -- spaces are removed
    // Inputs are not stacks, but ingredients, so they can contain "ore:ingotIron" and they'll be valid
    @SerializedName("inputs")
    @VariableHolder
    public String[] inputs = null;

    @SerializedName("input_spacing")
    @VariableHolder
    public String inputSpacing = null;

    @SerializedName("transparent_indices")
    @VariableHolder
    public String transparentIndices = null;

    @SerializedName("layout")
    @VariableHolder
    public String layout = null;

    @SerializedName("line_length")
    @VariableHolder
    public int lineLength = 100;

    @SerializedName("output")
    @VariableHolder
    public String output = null;

    @SerializedName("output_offset_x")
    @VariableHolder
    public String outputOffsetX = null;

    @SerializedName("output_offset_y")
    @VariableHolder
    public String outputOffsetY = null;


    /** ========== NON-JSON VARIABLES ========== **/
    private transient int x = 0;
    private transient int y = 0;
    private transient int spacing = 2;
    private transient int offsetX = 2;
    private transient int offsetY = 0;
    private transient List<Ingredient> stacks = null;
    private transient Ingredient outputStack = null;
    private transient GuiBookEntry guiBookEntry = null;
    private transient Set<Integer> optionalItems = null;


    @Override
    public void build(int componentX, int componentY, int pageNum) {

        if (inputs == null || output == null) return;

        // Store position variables
        this.x = componentX;
        this.y = componentY;

        try {
            if (inputSpacing != null) this.spacing = Integer.parseInt(inputSpacing);
            if (outputOffsetX != null) this.offsetX = Integer.parseInt(outputOffsetX);
            if (outputOffsetY != null) this.offsetY = Integer.parseInt(outputOffsetY);
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Error parsing variables for component IngredientListComponent: ", ex);
        }

        stacks = new ArrayList<>();
        optionalItems = new HashSet<>();
        ProcessorUtils.deserializeIngredientList(inputs, stacks);

        if (transparentIndices != null && !transparentIndices.isEmpty()) {
            String[] indices = transparentIndices.split(",");
            for (String index : indices) {
                try {
                    int i = Integer.parseInt(index);
                    optionalItems.add(i);
                } catch (Exception ignored) {
                }
            }
        }

        // Replace all the display names of all the matching stacks in the name with the "(Optional)" string
        // in the setup phase to avoid computing it every render tick
        for (int i = 0; i < stacks.size(); i++) {
            if (optionalItems.contains(i)) {
                ItemStack[] oldStacks = stacks.get(i).getMatchingStacks();
                ItemStack[] newStacks = new ItemStack[oldStacks.length];
                for (int j = 0; j < oldStacks.length; j++) {
                    newStacks[j] = oldStacks[j];
                    newStacks[j].setStackDisplayName(oldStacks[j].getDisplayName() + I18n.format("witcherycompanion.tooltip.optional"));
                }
                stacks.set(i, Ingredient.fromStacks(newStacks));
            }
        }

        if (output != null)
            outputStack = ItemStackUtil.loadIngredientFromString(output);

    }


    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (stacks == null || stacks.isEmpty()) return;

        if (guiBookEntry == null && context instanceof GuiBookEntry) {
            this.guiBookEntry = (GuiBookEntry) context;
        }

        int curX = this.x;
        int curY = this.y;
        int lineIndex = 1;

        if (this.isVertical()) {

            for (int i = 0; i < stacks.size(); i++) {
                Ingredient ingredient = stacks.get(i);
                ItemStack[] matchingStacks = ingredient.getMatchingStacks();
                if (matchingStacks.length > 0) {
                    ItemStack stack = matchingStacks[guiBookEntry.ticksInBook / 20 % matchingStacks.length];
                    renderOptionalItemStack(context, curX, curY, mouseX, mouseY, stack, optionalItems.contains(i));
                }

                curY += 16 + this.spacing;
                lineIndex++;
                if (lineIndex > lineLength) {
                    lineIndex = 0;
                    curY = this.y;
                    curX += 16 + this.spacing;
                }
            }

        } else {

            for (int i = 0; i < stacks.size(); i++) {

                Ingredient ingredient = stacks.get(i);
                ItemStack[] matchingStacks = ingredient.getMatchingStacks();
                if (matchingStacks.length > 0) {
                    ItemStack stack = matchingStacks[guiBookEntry.ticksInBook / 20 % matchingStacks.length];
                    renderOptionalItemStack(context, curX, curY, mouseX, mouseY, stack, optionalItems.contains(i));
                }

                curX += 16 + this.spacing;
                lineIndex++;
                if (lineIndex >= lineLength) {
                    lineIndex = 1;
                    curX = this.x;
                    curY += 16 + this.spacing;
                }
            }
        }

        if (outputStack != null) {
            // No transparency
            context.renderIngredient(this.x + offsetX, this.y + offsetY, mouseX, mouseY, outputStack);
        }
    }

    protected boolean isVertical() {
        if (this.layout == null) return false;
        return this.layout.equals("vertical");
    }

    protected void renderOptionalItemStack(IComponentRenderContext context, int x, int y, int mouseX, int mouseY, ItemStack stack, boolean optional) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        if (optional && renderItem instanceof IRenderItemInvoker) {
            IRenderItemInvoker invoker = (IRenderItemInvoker) renderItem;
            // Check if optional
            invoker.setColor(0x80FFFFFF);
        }
        context.renderItemStack(x, y, mouseX, mouseY, stack);
    }
    
}
