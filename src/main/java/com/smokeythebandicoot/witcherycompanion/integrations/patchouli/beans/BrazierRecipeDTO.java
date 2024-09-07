package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.api.brazier.IBrazierSummoningRecipeAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;

import java.util.ArrayList;
import java.util.List;

public class BrazierRecipeDTO extends AbstractDTO {

    // Set by recipe
    @Expose public String title;
    @Expose public String description;
    @Expose public List<Ingredient> inputs;
    @Expose public boolean hasExtraEntity;
    @Expose public String extraEntity;

    public String secretKey;

    @Override
    protected void initFields() {
        mapField("title",
                str -> this.title = str,
                () -> obfuscateIfSecret(this.title, EObfuscationMethod.MINECRAFT));

        mapField("description",
                str -> this.description = str,
                () -> obfuscateIfSecret(this.description, EObfuscationMethod.PATCHOULI));

        mapField("inputs",
                str -> {
                    this.inputs = new ArrayList<>();
                    ProcessorUtils.deserializeIngredientList(str, this.inputs);
                },
                () -> ProcessorUtils.serializeIngredientList(this.inputs));

        mapField("has_extra_entity",
                str -> this.hasExtraEntity = Utils.tryParseBool(str),
                () -> String.valueOf(this.hasExtraEntity));

        mapField("extra_entity",
                str -> this.extraEntity = str,
                () -> obfuscateIfSecret(this.extraEntity, EObfuscationMethod.PATCHOULI));
    }

    public BrazierRecipeDTO() { }

    public BrazierRecipeDTO(BrazierRecipe recipe) {
        String[] splits = recipe.getDescription(0.0f).split("\n\n");
        if (splits.length > 0) this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
        if (splits.length > 1) this.description = ProcessorUtils.reformatPatchouli(splits[1], false);
        this.inputs = recipe.getIngredients();
        this.isSecret = recipe.getHidden();
        if (recipe instanceof IBrazierSummoningRecipeAccessor) {
            IBrazierSummoningRecipeAccessor summonRecipe = (IBrazierSummoningRecipeAccessor) recipe;
            Class<EntityCreature> creatureClass = summonRecipe.getExtraSpawnedEntity().getEntityClass();

            ResourceLocation entityRegName = EntityList.getKey(creatureClass);
            if (entityRegName != null) {
                this.extraEntity = I18n.format("entity." + EntityList.getTranslationName(entityRegName) + ".name");
            }
        }
        this.hasExtraEntity = this.extraEntity != null;
        this.secretKey = recipe.getId().toString();
    }

    @Override
    public String getSecretKey() {
        return "conjuring/recipe/" + secretKey;
    }
}
