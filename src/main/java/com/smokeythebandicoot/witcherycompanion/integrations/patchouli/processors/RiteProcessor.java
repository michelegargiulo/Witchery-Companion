package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.rite.IItemRiteSacrificeAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.resources.RiteManager;
import net.msrandom.witchery.rite.Rite;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.PowerRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.RiteSacrifice;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;


public class RiteProcessor extends BaseProcessor {

    protected String riteId;
    protected String title;
    protected String description;
    protected List<Ingredient> ingredients;
    protected float altarPower = 0;


    @Override
    public void setup(IVariableProvider<String> provider) {
        ingredients = new ArrayList<>();

        this.riteId = readVariable(provider, "rite_id");
        if (riteId == null) return;

        Rite rite = RiteManager.INSTANCE.getRiteMap().get(new ResourceLocation(riteId));
        if (rite == null) return;

        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");

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
                    ingredients.add(req.getIngredient());
                }
            } else if (sacrifice instanceof PowerRiteSacrifice) {
                PowerRiteSacrifice powerSacrifice = (PowerRiteSacrifice) sacrifice;
                this.altarPower = powerSacrifice.powerRequired;
            }
        }

        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "ingredients":
                return ProcessorUtils.serializeIngredientList(this.ingredients);
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
