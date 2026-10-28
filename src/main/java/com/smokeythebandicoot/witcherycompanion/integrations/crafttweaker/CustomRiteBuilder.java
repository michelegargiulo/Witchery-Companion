package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.CustomRiteApi;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteContext;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteDefinition;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteEffectHandler;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.data.IData;
import crafttweaker.util.IEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder of a custom rite. Every method returns the builder, so calls can be chained. The methods that modify an item
 * (identified, optional, returned, returnedAs, action) apply to the last item added with addItem.
 */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.CustomRiteBuilder")
@ZenRegister
public class CustomRiteBuilder {

    /** Null if the rite could not be created (invalid id, feature disabled): every call is then a no-op */
    private final CustomRiteDefinition definition;
    /** An item of the rite while it is being defined: the ingredient is built when the rite is registered */
    private static class PendingItem {
        final CustomRiteDefinition.ItemSpec spec = new CustomRiteDefinition.ItemSpec(null);
        final IIngredient ingredient;
        NBTTagCompound requiredTag = null;
        boolean exactTag = false;

        PendingItem(IIngredient ingredient) {
            this.ingredient = ingredient;
        }
    }

    private final List<PendingItem> pendingItems = new ArrayList<>();
    private PendingItem lastItem = null;
    private IEventHandler<RiteContext> effect = null;
    private boolean registered = false;

    public CustomRiteBuilder(String id) {
        this.definition = id == null ? null : new CustomRiteDefinition(id);
    }

    private boolean ok() {
        return definition != null;
    }

    private void error(String message) {
        CraftTweakerAPI.logError("Custom rite " + (ok() ? definition.getId() : "<invalid>") + ": " + message);
    }

    // ------------------------------------------------------------------------------------------ Texts

    @ZenMethod
    @ZenDoc(value = "Name of the rite, as shown in the books. Plain text, or a lang key if you provide the translation")
    public CustomRiteBuilder setName(String name) {
        if (ok()) definition.setName(name);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Description of the rite, as shown in the books (\\n is a new line). Plain text, or a lang key. " +
            "Tip: mention here the coven requirement, if any")
    public CustomRiteBuilder setDescription(String description) {
        if (ok()) definition.setDescription(description);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Sacrifices

    @ZenMethod
    @ZenDoc(value = "Adds an item that must be thrown into the circle. Each call requires one item. Matching is the one of " +
            "CraftTweaker ingredients: items, ore dictionary entries, anyDamage(), only(function) for any condition. " +
            "Note that in CraftTweaker withTag(tag) alone does not filter by NBT, and withTag(tag, true) requires the item " +
            "to CONTAIN the tag (it may have more NBT). Use requireTag() or requireExactTag() on the item for an explicit " +
            "NBT match. Amounts and transformers are ignored (one unit is consumed)")
    public CustomRiteBuilder addItem(IIngredient ingredient) {
        if (!ok()) return this;
        if (ingredient == null || CraftTweakerIngredient.create(ingredient, null, false) == null) {
            error("addItem: the ingredient has no concrete item (wildcards like <*> are not supported, the books need an item to show)");
            lastItem = null;
            return this;
        }
        if (ingredient.getAmount() > 1) {
            CraftTweakerAPI.logWarning("Custom rite " + definition.getId() + ": the amount of the ingredient is ignored, " +
                    "each addItem consumes one item. Call addItem " + ingredient.getAmount() + " times to require that many");
        }
        lastItem = new PendingItem(ingredient);
        pendingItems.add(lastItem);
        definition.getItems().add(lastItem.spec);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "The last added item must contain at least these NBT entries (partial match: it may have more). " +
            "Lists match if each element of the required list is found in the item's list. Types matter: {a: 1 as byte} " +
            "does not match an int. Example: addItem(<minecraft:potion>).requireTag({Potion: \"minecraft:healing\"})")
    public CustomRiteBuilder requireTag(IData tag) {
        return setRequiredTag("requireTag", tag, false);
    }

    @ZenMethod
    @ZenDoc(value = "The last added item must have exactly this NBT: any other entry makes it not match. An item without " +
            "NBT matches only an empty tag. Example: addItem(<minecraft:stick>).requireExactTag({Magic: 1 as byte})")
    public CustomRiteBuilder requireExactTag(IData tag) {
        return setRequiredTag("requireExactTag", tag, true);
    }

    private CustomRiteBuilder setRequiredTag(String method, IData tag, boolean exact) {
        if (!ok()) return this;
        if (lastItem == null) {
            error(method + ": add an item first");
            return this;
        }
        NBTTagCompound nbt = tag == null ? null : CraftTweakerMC.getNBTCompound(tag);
        if (nbt == null) {
            error(method + ": the tag must be a map, like {key: value}");
            return this;
        }
        lastItem.requiredTag = nbt;
        lastItem.exactTag = exact;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Gives the last added item an identifier, used by RiteContext.getSacrifice(identifier) to find it. " +
            "Items without identifier can only be read from RiteContext.sacrifices")
    public CustomRiteBuilder identified(String identifier) {
        if (!ok()) return this;
        if (lastItem == null) error("identified: add an item first");
        else lastItem.spec.identifier = identifier;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "The last added item is optional: the rite works without it")
    public CustomRiteBuilder optional() {
        if (!ok()) return this;
        if (lastItem == null) error("optional: add an item first");
        else lastItem.spec.optional = true;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "The last added item is not consumed: it is given back (witchery:self)")
    public CustomRiteBuilder returned() {
        if (!ok()) return this;
        if (lastItem == null) error("returned: add an item first");
        else lastItem.spec.returns = CustomRiteDefinition.returnsSelf();
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "The last added item is consumed and replaced with this item, which receives the data (NBT) of the " +
            "sacrificed one (witchery:copy_data). Like W:R does with the Charged Attuned Stone -> Attuned Stone")
    public CustomRiteBuilder returnedAs(IItemStack replacement) {
        if (!ok()) return this;
        if (lastItem == null) {
            error("returnedAs: add an item first");
            return this;
        }
        ItemStack stack = replacement == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(replacement);
        if (stack.isEmpty()) {
            error("returnedAs: the replacement item is empty");
            return this;
        }
        lastItem.spec.returns = CustomRiteDefinition.returnsCopyData(
                String.valueOf(stack.getItem().getRegistryName()), stack.getMetadata() == 0 ? null : stack.getMetadata());
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Action performed on the last added item: 'witchery:set_location' (the item sets the location of the " +
            "rite, like a Bound Waystone) or 'witchery:keep'")
    public CustomRiteBuilder action(String action) {
        if (!ok()) return this;
        if (lastItem == null) error("action: add an item first");
        else lastItem.spec.action = action;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Altar power consumed by the rite")
    public CustomRiteBuilder setPower(float power) {
        if (ok()) definition.setPower(power);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Adds a living creature that is sacrificed: it must stand inside the circle. Entity registry name, " +
            "for example minecraft:zombie")
    public CustomRiteBuilder addEntity(String entityId) {
        if (ok()) definition.getEntities().add(entityId);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Requirements

    @ZenMethod
    @ZenDoc(value = "Adds a circle that must be drawn around the golden chalk. Glyph: ritual, infernal, otherwhere (or a " +
            "block id). Size: 1 (small), 2 (medium) or 3 (large). Call it more than once to require several circles")
    public CustomRiteBuilder addCircle(String glyph, int size) {
        if (ok()) definition.getCircles().add(new CustomRiteDefinition.CircleSpec(CustomRiteDefinition.normalizeGlyph(glyph), size));
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Minimum number of witches in the coven of the player that starts the rite. 0 = no coven")
    public CustomRiteBuilder setCovenSize(int size) {
        if (ok()) definition.setCovenSize(size);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Adds a condition of W:R that must be true: night, thunder or overworld (or a condition id)")
    public CustomRiteBuilder addCondition(String condition) {
        if (ok()) definition.getConditions().add(new CustomRiteDefinition.ConditionSpec(CustomRiteDefinition.normalizeCondition(condition), false));
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Adds a condition of W:R that must be false: night, thunder or overworld (or a condition id)")
    public CustomRiteBuilder addNegatedCondition(String condition) {
        if (ok()) definition.getConditions().add(new CustomRiteDefinition.ConditionSpec(CustomRiteDefinition.normalizeCondition(condition), true));
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "If true the rite is not listed in the W:R Rites book (it still works)")
    public CustomRiteBuilder setHidden(boolean hidden) {
        if (ok()) definition.setHidden(hidden);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Place in the books

    @ZenMethod
    @ZenDoc(value = "Places the rite right after another rite in the books of W:R, in the same section. The rite is a W:R " +
            "or a custom rite, for example \"witchery:light_infusion\" (\"witchery:\" can be omitted). Without insertAfter " +
            "and setSection, the rite is added at the end of all the rites, in its own section")
    public CustomRiteBuilder insertAfter(String riteId) {
        if (!ok()) return this;
        try {
            definition.setInsertAfter(riteId);
        } catch (IllegalArgumentException e) {
            error("insertAfter: " + e.getMessage());
        }
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Lists the rite in a section of the books of W:R, after the last rite of that section: curse, infusion, " +
            "nature, protection, setup, summons or transposition (\"witchery:\" can be omitted). With insertAfter, " +
            "the rite is placed after that rite and this section replaces the one of that rite")
    public CustomRiteBuilder setSection(String section) {
        if (!ok()) return this;
        try {
            definition.setSection(section);
        } catch (IllegalArgumentException e) {
            error("setSection: " + e.getMessage());
        }
        return this;
    }

    // ------------------------------------------------------------------------------------------ Effect

    @ZenMethod
    @ZenDoc(value = "The effect of the rite: a function that receives a RiteContext. It runs on the server, once, after " +
            "the coven check passed and the sacrifices have been consumed")
    public CustomRiteBuilder setEffect(IEventHandler<RiteContext> effect) {
        this.effect = effect;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Validates the rite and adds it. Errors are written in the CraftTweaker log")
    public void register() {
        if (!ok()) return;
        if (registered) {
            error("register was already called on this builder");
            return;
        }
        registered = true;

        Map<String, Ingredient> ingredients = new LinkedHashMap<>();
        for (int i = 0; i < pendingItems.size(); i++) {
            PendingItem item = pendingItems.get(i);
            String key = CustomRiteDefinition.ingredientKey(definition.getId(), i);
            ingredients.put(key, CraftTweakerIngredient.create(item.ingredient, item.requiredTag, item.exactTag));
            item.spec.ingredientJson = CustomRiteDefinition.ingredientRef(key);
        }
        CraftTweakerAPI.apply(new AddCustomRite(definition, effect, ingredients));
    }

    // ------------------------------------------------------------------------------------------ Helpers

    private static class AddCustomRite implements IAction {
        private final CustomRiteDefinition definition;
        private final IEventHandler<RiteContext> effect;
        private final Map<String, Ingredient> ingredients;

        AddCustomRite(CustomRiteDefinition definition, IEventHandler<RiteContext> effect, Map<String, Ingredient> ingredients) {
            this.definition = definition;
            this.effect = effect;
            this.ingredients = ingredients;
        }

        @Override
        public void apply() {
            CustomRiteEffectHandler handler = effect == null ? null : new ScriptHandler(effect);
            List<String> problems = CustomRiteApi.register(definition, handler, ingredients);
            for (String problem : problems) {
                CraftTweakerAPI.logError("Custom rite " + definition.getId() + " was not added: " + problem);
            }
        }

        @Override
        public String describe() {
            return "Adding custom rite " + definition.getId();
        }
    }

    /** Bridges the Java handler of the API to the ZenScript function. Not a mixin: lambdas are fine */
    private static class ScriptHandler implements CustomRiteEffectHandler {
        private final IEventHandler<RiteContext> function;

        ScriptHandler(IEventHandler<RiteContext> function) {
            this.function = function;
        }

        @Override
        public void run(CustomRiteContext context) {
            function.handle(new RiteContext(context));
        }
    }

}
