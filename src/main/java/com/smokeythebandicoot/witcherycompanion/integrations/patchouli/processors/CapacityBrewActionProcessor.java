package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.CapacityBrewActionDTO;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.*;


public class CapacityBrewActionProcessor implements IComponentProcessor {

    private String removesCeilingText;
    private String removesCeilingTooltip;
    private String secretText;
    private String secretTooltip;

    private static final HashMap<Integer, CapacityBrewActionDTO> dtoCache = new HashMap<>();
    private static List<CapacityBrewAction> capacityBrews = null;

    @Override
    public void setup(IVariableProvider<String> provider) {
        // DTO is set dynamically for this component, as it renders all brews in a single page
        if (capacityBrews == null || capacityBrews.isEmpty()) {
            updateCapacityMap();
        }

        dtoCache.clear();

        this.removesCeilingText = ProcessorUtils.readVariable(provider, "removes_ceiling_text");
        this.removesCeilingTooltip = ProcessorUtils.readVariable(provider, "removes_ceiling_tooltip");
        this.secretText = ProcessorUtils.readVariable(provider, "secret_text");
        this.secretTooltip = ProcessorUtils.readVariable(provider, "secret_tooltip");
    }

    @Override
    public String process(String key) {
        // Serialized key
        ProcessorUtils.KeyInfo info = ProcessorUtils.splitKeyIndex(key);
        int index = info.index;

        if (index > -1 && index < capacityBrews.size()) {

            key = info.key;
            CapacityBrewActionDTO dto;

            // The DTO either comes from the cache, or must be computed and inserted into the cache
            if (dtoCache.containsKey(index)) {
                dto = dtoCache.get(index);
            } else {
                // Create new DTO from brew, serialize it and insert in cache and return
                dto = new CapacityBrewActionDTO(capacityBrews.get(index));
                dto.removesCeilingText = this.removesCeilingText;
                dto.removesCeilingTooltip = this.removesCeilingTooltip;
                dto.secretText = this.secretText;
                dto.secretTooltip = this.secretTooltip;
                dtoCache.put(index, dto);
            }

            // If the key is the serialized brew, then serialize and return.
            // For cases in which the same brew is used more than once, caching might be used
            // to improve performance and cache serialization results. But companion
            // only uses this template in a single page, where the same brew is never used twice
            if (key.equals("serialized")) {
                return ProcessorUtils.serializeDto(dto);
            }
            return dto.getForKey(key);
        }
        return null;
    }

    private static void updateCapacityMap() {
        capacityBrews = new ArrayList<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof CapacityBrewAction) {
                capacityBrews.add((CapacityBrewAction) action);
            }
        }
        capacityBrews.sort(Comparator.comparingInt(CapacityBrewAction::getCeiling));
    }


    /** Returns an ordered list of itemstacks to throw in the cauldron to have the required capacity */
    public static List<Ingredient> getItemsForCapacity(int requiredCapacity) {
        List<Ingredient> ingredients = new ArrayList<>();

        // Too early, return empty list
        if (capacityBrews == null || capacityBrews.isEmpty()) {
            return ingredients;
        }

        // Build the list
        int currentCapacity = 0;
        for (CapacityBrewAction capacityBrewInfo : capacityBrews) {
            if (currentCapacity >= requiredCapacity) {
                break;
            }
            currentCapacity += capacityBrewInfo.getIncrement();
            ingredients.add(Ingredient.fromStacks(capacityBrewInfo.getKey().toStack()));
        }
        return ingredients;
    }

}
