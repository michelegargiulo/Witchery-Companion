package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.*;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ModifierBrewActionProcessor extends BrewActionProcessor {

    private String effectNoParticlesDescription;
    private String effectInvertDescription;
    private String effectSkipBlocksDescription;
    private String effectSkipEntitiesDescription;
    private String effectDisableLimitDescription;
    private String quaffingDescription;
    private String colorDescription;

    protected transient byte modifierType = -1;

    protected static final Map<Integer, BrewActionInfo> cache = new HashMap<>();

    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");

        // Read descriptions
        this.effectNoParticlesDescription = readVariable(provider, "no_particles_description");
        this.effectInvertDescription = readVariable(provider, "invert_description");
        this.effectSkipBlocksDescription = readVariable(provider, "skip_blocks_description");
        this.effectSkipEntitiesDescription = readVariable(provider, "skip_entities_description");
        this.effectDisableLimitDescription = readVariable(provider, "disable_limit_description");
        this.quaffingDescription = readVariable(provider, "quaffing_description");
        this.colorDescription = readVariable(provider, "color_description");

        int index = 0;
        String modifierType = readVariable(provider, "modifier_type");

        // Goal is to build non-lazy cache to store all relevant BrewActions
        // into an int <-> brew map (since we split key by index)
        switch (modifierType) {
            case "quaffing":
                // Json has been reloaded and type has changed. Invalidate cache
                if (this.modifierType != 1) cache.clear();
                this.modifierType = 1;
                for (BrewAction quaffingBrew : BrewRegistry.getQuaffingBrews()) {
                    this.stack = quaffingBrew.getKey().toStack();
                    this.description = getDescription(quaffingBrew.getDrinkSpeedModifiers());
                    this.isSecret = quaffingBrew.getHidden();
                    obfuscateIfSecret();
                    if (this.stack == ItemStack.EMPTY) continue; // If secret and hidden, avoid incrementing index
                    cache.put(index, new BrewActionInfo(ItemStackUtil.serializeStack(this.stack), this.description));
                    index++;
                }
                break;

            case "color":
                // Json has been reloaded and type has changed. Invalidate cache
                if (this.modifierType != 2) cache.clear();
                this.modifierType = 2;
                for (EnumDyeColor color : BrewRegistry.getColorBrews().keySet().stream()
                        .sorted(Comparator.comparingInt(EnumDyeColor::getMetadata))
                        .collect(Collectors.toList())) {

                    SetColorBrewAction colorBrew = BrewRegistry.getColorBrew(color);
                    if (colorBrew != null) {
                        this.stack = colorBrew.getKey().toStack();
                        this.isSecret = colorBrew.getHidden();
                        this.description = getDescription(color);
                        obfuscateIfSecret();
                        if (this.stack == ItemStack.EMPTY) continue; // If secret and hidden, avoid incrementing index
                        BrewActionInfo info = new BrewActionInfo(ItemStackUtil.serializeStack(this.stack), this.description);
                        cache.put(index, info);
                        index++;
                    }
                }
                break;

            default:
                // Json has been reloaded and type has changed. Invalidate cache
                if (this.modifierType != 0) cache.clear();
                this.modifierType = 0;
                for (EffectModifierBrewAction.Type type : BrewRegistry.getModifiers().keySet().stream()
                        .sorted(ModifierBrewActionProcessor::compareEffectModifierBrewActionTypes)
                        .collect(Collectors.toList())) {

                    EffectModifierBrewAction effectBrew = BrewRegistry.getModifier(type);
                    if (effectBrew != null) {
                        this.stack = effectBrew.getKey().toStack();
                        this.isSecret = effectBrew.getHidden();
                        this.description = getDescription(type);
                        obfuscateIfSecret();
                        if (this.stack == ItemStack.EMPTY) continue; // If secret and hidden, avoid incrementing index
                        BrewActionInfo info = new BrewActionInfo(ItemStackUtil.serializeStack(this.stack), this.description);
                        cache.put(index, info);
                        index++;
                    }
                }
                break;
        }
    }

    @Override
    public String process(String key) {
        int index = ProcessorUtils.splitKeyIndex(key);

        // Cache is not lazy, so it contains all relevant info.
        // If not in cache, then it should not be displayed
        BrewActionInfo info = null;
        if (cache.containsKey(index)) {
            info = cache.get(index);
            if (key.startsWith("stack")) {
                return info.serializedStack;
            } else if (key.startsWith("description")) {
                return info.description;
            }
        }

        return null;
    }

    protected String getDescription(EnumDyeColor color) {
        if (this.colorDescription == null) return "";
        return this.colorDescription
                .replace("{color}", I18n.format("item.witchery.witches_brews_book.modifiers.color." + color.getName()))
                + (this.isSecret ? this.secretText : "");
    }

    protected String getDescription(int quaffingLevel) {
        if (this.quaffingDescription == null) return "";
        return quaffingDescription.replace("{level}", String.valueOf(quaffingLevel))
                + (this.isSecret ? this.secretText : "");
    }

    protected String getDescription(EffectModifierBrewAction.Type type) {
        String result = "";
        switch (type) {
            case INVERT:
                result = this.effectInvertDescription;
                break;
            case NO_PARTICLES:
                result = this.effectNoParticlesDescription;
                break;
            case SKIP_BLOCK_EFFECTS:
                result = this.effectSkipBlocksDescription;
                break;
            case SKIP_ENTITY_EFFECTS:
                result = this.effectSkipEntitiesDescription;
                break;
            case DISABLE_POWER_LIMIT:
                result = this.effectDisableLimitDescription;
                break;
        }
        return result + (this.isSecret ? this.secretText : "");
    }

    public static void clearCache() {
        cache.clear();
    }

    public static int compareEffectModifierBrewActionTypes(EffectModifierBrewAction.Type o1, EffectModifierBrewAction.Type o2) {
        EffectModifierBrewAction a1 = BrewRegistry.getModifier(o1);
        EffectModifierBrewAction a2 = BrewRegistry.getModifier(o2);
        if (a1 == null && a2 != null) return 1;
        if (a1 != null && a2 == null) return -1;
        if (a1 == null) return 0;
        if (a1.getHidden() == a2.getHidden()) return o1.name().compareTo(o2.name());
        if (a1.getHidden()) return 1;
        return -1;
    }

}
