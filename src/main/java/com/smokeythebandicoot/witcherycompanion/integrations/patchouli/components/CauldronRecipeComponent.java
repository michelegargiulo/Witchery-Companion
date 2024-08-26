package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;

public class CauldronRecipeComponent implements ICustomComponent {

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

    // The brew ID
    @SerializedName("output")
    @VariableHolder
    public String output = null;

    @SerializedName("x")
    @VariableHolder
    public int x = 0;

    @SerializedName("y")
    @VariableHolder
    public int y = 0;


    /** ========== NON-JSON VARIABLES ========== **/
    private transient List<Ingredient> stacks = null;
    private transient Ingredient outputStack = null;


    /** ========== OVERRIDES ========== **/
    /** Called when this component is built. Take the chance to read variables and set
     * any local positions here. */
    @Override
    public void build(int componentX, int componentY, int pageNum) {

        // Store position variables
        this.x = componentX;
        this.y = componentY;

        if (inputs == null) return;

        stacks = new ArrayList<>();
        ProcessorUtils.deserializeIngredientList(inputs, stacks);

        if (output != null)
            outputStack = ItemStackUtil.loadIngredientFromString(output);

    }

    /** Called every render tick. No special transformations are applied, so you're responsible
     * for putting everything in the right place. */
    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (stacks == null) return;
        int curX = this.x;
        int curY = this.y;
        for (Ingredient ingredient : stacks) {
            context.renderIngredient(curX, curY, mouseX, mouseY, ingredient);
            curX += 18; // 16 for the item, 2 for the spacing
        }

        if (outputStack != null) {
            context.renderIngredient(this.x + 70, this.y + 24, mouseX, mouseY, outputStack);
        }
    }

}
