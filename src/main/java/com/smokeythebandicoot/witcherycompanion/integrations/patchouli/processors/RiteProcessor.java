package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.rite.IItemRiteSacrificeAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.block.BlockCircleGlyph;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.resources.RiteManager;
import net.msrandom.witchery.rite.Rite;
import net.msrandom.witchery.rite.RitualCircle;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.PowerRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.RiteSacrifice;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RiteProcessor extends BaseProcessor {

    protected String riteId;
    protected String title;
    protected String description;
    protected String note;
    protected String circles;
    protected List<Ingredient> ingredients;
    protected float altarPower = 0;


    @Override
    public void setup(IVariableProvider<String> provider) {
        ingredients = new ArrayList<>();
        circles = "";

        this.riteId = readVariable(provider, "rite_id");
        if (riteId == null) return;

        Rite rite = RiteManager.INSTANCE.getRiteMap().get(new ResourceLocation(riteId));
        if (rite == null) return;

        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.note = readVariable(provider, "note");

        if (this.title == null || this.description == null) {
            // Info not provided, so compute split and auto-populate
            String[] riteFullDesc = rite.getDescription(0).split("\n\n");
            if (riteFullDesc.length >= 2) {
                if (this.title == null) this.title = riteFullDesc[0];
                if (this.description == null) this.description = riteFullDesc[1];
            }
        }

        this.ingredients = new ArrayList<>();
        for (RiteSacrifice sacrifice : rite.getSacrifices()) {
            if (sacrifice instanceof ItemRiteSacrifice && sacrifice instanceof IItemRiteSacrificeAccessor) {
                IItemRiteSacrificeAccessor accessor = (IItemRiteSacrificeAccessor) sacrifice;
                for (ItemRiteSacrifice.ItemRequirement req : accessor.getRequirements()) {
                    if (req.getOptional()) {
                        // If item is optional, cycle the item with AIR
                        ItemStack[] matching = req.getIngredient().matchingStacks;
                        ItemStack[] stacks = new ItemStack[matching.length + 1];
                        System.arraycopy(matching, 0, stacks, 0, matching.length);
                        stacks[stacks.length - 1] = ItemStack.EMPTY;
                        ingredients.add(Ingredient.fromStacks(stacks));
                        continue;
                    }
                    ingredients.add(req.getIngredient());
                }
            } else if (sacrifice instanceof PowerRiteSacrifice) {
                PowerRiteSacrifice powerSacrifice = (PowerRiteSacrifice) sacrifice;
                this.altarPower = powerSacrifice.powerRequired;
            }
        }

        StringBuilder sb = new StringBuilder();
        Iterator<RitualCircle> it = rite.getCircles().iterator();
        while (it.hasNext()) {
            RitualCircle circle = it.next();

            int size = circle.getSize();
            Block glyph = circle.getGlyph();

            String sizeStr = "small";
            String glyphStr = "ritual";

            if (size == 4) sizeStr = "medium";
            else if (size == 6) sizeStr = "large";

            if (glyph == WitcheryBlocks.GLYPH_INFERNAL) glyphStr = "infernal";
            else if (glyph == WitcheryBlocks.GLYPH_OTHERWHERE) glyphStr = "otherwhere";

            sb.append(sizeStr).append("_").append(glyphStr);
            if (it.hasNext())
                sb.append(",");
        }
        this.circles = sb.toString();


        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "note":
                return this.note;
            case "ingredients":
                return ProcessorUtils.serializeIngredientList(this.ingredients);
            case "circles":
                return this.circles;
            case "altar_power":
                return String.valueOf(this.altarPower);
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return ProgressUtils.getRiteSecret(this.riteId);
    }

    @Override
    protected void obfuscateFields() { }

    @Override
    protected void hideFields() { }



}
