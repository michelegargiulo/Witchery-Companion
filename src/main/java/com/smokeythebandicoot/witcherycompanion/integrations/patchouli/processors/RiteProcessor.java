package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.accessors.rite.IItemRiteSacrificeAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
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
    protected String transparentItems;
    protected float altarPower = 0;


    @Override
    public void setup(IVariableProvider<String> provider) {
        ingredients = new ArrayList<>();
        circles = "";
        transparentItems = "";

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
        StringBuilder optionalIndices = new StringBuilder();
        for (RiteSacrifice sacrifice : rite.getSacrifices()) {
            if (sacrifice instanceof ItemRiteSacrifice && sacrifice instanceof IItemRiteSacrificeAccessor) {
                IItemRiteSacrificeAccessor accessor = (IItemRiteSacrificeAccessor) sacrifice;
                Iterator<ItemRiteSacrifice.ItemRequirement> it = accessor.getRequirements().iterator();
                while (it.hasNext()) {
                    ItemRiteSacrifice.ItemRequirement req = it.next();
                    ingredients.add(req.getIngredient());
                    if (req.getOptional()) {
                        optionalIndices.append(ingredients.size() - 1);
                        if (it.hasNext()) {
                            optionalIndices.append(",");
                        }
                    }
                }
                this.transparentItems = optionalIndices.toString();

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
            case "transparent_indices":
                return this.transparentItems;
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
