package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public abstract class BrewActionProcessor extends BaseProcessor {

    protected ItemStack stack;
    protected String brewName;
    protected String brewType;
    protected String description;

    protected BrewAction currentAction = null;

    @Override
    public void setup(IVariableProvider<String> provider) {

        // Stack (mandatory)
        String serializedStack = readVariable(provider, "brew_item");
        if (serializedStack == null) return;
        ItemStack key = ItemStackUtil.loadStackFromString(serializedStack);
        if (key == null || key.isEmpty()) return;
        this.currentAction = BrewActionManager.INSTANCE.getAction(ItemKey.fromStack(key));

        // Brew name
        this.brewName = getBrewName();

        // Brew Type
        this.brewType = readVariable(provider, "brew_type");

        // Description
        this.description = readVariable(provider, "description");

        super.setup(provider);
    }

    @Override
    protected void obfuscateFields() {
        this.stack = OBFUSCATED_STACK;
        this.brewName = obfuscate(this.brewName, EObfuscationMethod.MINECRAFT);
        this.brewType = obfuscate(this.brewType, EObfuscationMethod.MINECRAFT);
        this.description = obfuscate(this.description, EObfuscationMethod.PATCHOULI);
    }

    @Override
    protected void hideFields() {
        this.stack = ItemStack.EMPTY;
        this.brewName = "";
        this.brewType = "";
        this.description = "";
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "stack":
                return ItemStackUtil.serializeStack(this.stack);
            case "brew_name":
                return this.brewName;
            case "brew_type":
                return this.brewType;
            case "description":
                return this.description;
            default:
                return super.process(key);
        }
    }

    @Override
    public final String getSecretKey() {
        if (this.stack == null) return ProgressUtils.getBrewActionSecret(new ItemStack(Items.AIR));
        return ProgressUtils.getBrewActionSecret(this.stack);
    }

    protected String getBrewName() {
        if (this.currentAction == null || this.currentAction.getNamePart() == null) return "";
        return I18n.format(currentAction.getNamePart().resource);
    }


    protected static class BrewActionInfo {

        public final String serializedStack;
        public final String description;

        public BrewActionInfo(String serializedStack, String description) {
            this.serializedStack = serializedStack;
            this.description = description;
        }
    }
}
