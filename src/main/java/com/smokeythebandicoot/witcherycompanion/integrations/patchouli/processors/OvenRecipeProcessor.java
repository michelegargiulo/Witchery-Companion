package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.init.WitcheryWoodTypes;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.integration.IntegrationManager;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class OvenRecipeProcessor extends BaseProcessor {

    protected boolean isConfigured = false;
    protected String title = "";
    protected ItemStack input = ItemStack.EMPTY;
    protected ItemStack output = ItemStack.EMPTY;
    protected ItemStack fume = ItemStack.EMPTY;

    @Override
    public void setup(IVariableProvider<String> provider) {

        String inputString = readVariable(provider, "for_input");
        if (inputString == null) return;

        this.input = ItemStackUtil.loadStackFromString(inputString);
        if (input == null) return;

        ItemStack output = FurnaceRecipes.instance().getSmeltingResult(this.input);
        if (output.isEmpty()) return;
        this.output = output;

        Item byProduct = WitcheryWoodTypes.getSaplingFumes().get(ItemKey.fromStack(this.input));
        if (byProduct == null) {
            Item moddedByProduct = IntegrationManager.getByProduct(this.input);
            if (moddedByProduct == null) {
                this.fume = new ItemStack(WitcheryFumeItems.FOUL_FUME);
            } else {
                this.fume = new ItemStack(moddedByProduct);
            }

        } else {
            this.fume = new ItemStack(byProduct);
        }

        this.title = readVariable(provider, "title");
        if (title == null) this.title = this.fume.getDisplayName();
        this.isSecret = false; // No secret oven recipes
        this.isConfigured = true;
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "input":
                return ItemStackUtil.serializeStack(this.input);
            case "output":
                return ItemStackUtil.serializeStack(this.output);
            case "fume":
                return ItemStackUtil.serializeStack(this.fume);
            case "is_configured":
                return String.valueOf(this.isConfigured);
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return null;
    }

    @Override
    protected void obfuscateFields() { }

    @Override
    protected void hideFields() { }

}
