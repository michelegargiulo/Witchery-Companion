package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.DistilleryRecipeDTO;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components.base.BaseComponent;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

public class DistilleryRecipeComponent extends BaseComponent<DistilleryRecipeDTO> implements ICustomComponent {

    @SerializedName("first_ingredient")
    @VariableHolder
    public String _firstIngredient = null;

    @SerializedName("second_ingredient")
    @VariableHolder
    public String _secondIngredient = null;

    @SerializedName("clay_jars")
    @VariableHolder
    public String _clayJars = null;

    @SerializedName("outputs")
    @VariableHolder
    public String _outputs = null;


    private transient Ingredient firstIngredient = Ingredient.EMPTY;
    private transient Ingredient secondIngredient = Ingredient.EMPTY;
    private transient List<ItemStack> outputs = new ArrayList<>();
    private transient ItemStack clayJars = new ItemStack(Items.AIR);


    @Override
    public void onBuild() {

        this.firstIngredient = getTransform(this._firstIngredient, ItemStackUtil::loadIngredientFromString,
                dto, d -> d.firstIng, Ingredient.EMPTY);

        this.secondIngredient = getTransform(this._secondIngredient, ItemStackUtil::loadIngredientFromString,
                dto, d -> d.secondIng, Ingredient.EMPTY);

        this.clayJars = getTransform(this._clayJars, j -> {
                    int jarCount = Utils.tryParseInt(j);
                    return new ItemStack(WitcheryFumeItems.CLAY_JAR, jarCount);
                },
                dto, d -> new ItemStack(WitcheryFumeItems.CLAY_JAR, d.clayJars), ItemStack.EMPTY);

        this.outputs = getTransform(this._outputs, ProcessorUtils::deserializeItemStackList,
                dto, d -> d.outputs, new ArrayList<>());

    }


    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {

        context.renderIngredient(this.x, this.y, mouseX, mouseY, this.firstIngredient);
        context.renderIngredient(this.x, this.y + 18, mouseX, mouseY, this.secondIngredient);
        context.renderItemStack(this.x, this.y + 46, mouseX, mouseY, this.clayJars);

        int offsetX = this.x + 50;
        int curX = 0;
        int curY = 0;
        for (ItemStack stack : outputs) {
            context.renderItemStack(offsetX + curX * 18, this.y + curY * 18, mouseX, mouseY, stack);
            curX++;
            if (curX > 1) {
                curX = 0;
                curY++;
            }
        }
    }

}
