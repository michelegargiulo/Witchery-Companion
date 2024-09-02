package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.brewing.IUpgradeBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.IPatchouliSerializable;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ProgressionProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.*;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class ModifierBrewActionProcessor extends ProgressionProcessor implements IComponentProcessor {

    private String modifierType;
    private static List<ModifierBrewActionInfo> modifierBrews = null;
    private static List<ModifierBrewActionInfo> quaffingBrews = null;
    private static List<ModifierBrewActionInfo> colorBrews = null;

    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        if (modifierBrews == null || modifierBrews.isEmpty() ||
                quaffingBrews == null || quaffingBrews.isEmpty() ||
                colorBrews == null || colorBrews.isEmpty()) {
            updateModifierMaps();
        }

        if (iVariableProvider.has("modifier_type")) {
            this.modifierType = iVariableProvider.get("modifier_type");
        }
    }

    @Override
    public String process(String key) {

        // Convert the set into a sorted list accessible by index
        int index = ProcessorUtils.getIndexFromKey(key, "modifier_brew_item");

        if (index > -1) {

            ModifierBrewActionInfo info;
            if (this.power) {
                if (index < powerBrews.size())
                    info = powerBrews.get(index);
                else return null;
            }
            else {
                if (index < durationBrews.size())
                    info = durationBrews.get(index);
                else return null;
            }

            // Checks if it is secret and if player has knowledge about it
            if (shouldHide(info))
                return null;

            return info.serialize();

        }

        WitcheryCompanion.logger.warn("Error while processing UpgradeBrewActionProcessor. Key outside of bounds. " +
                "Key: {}, Power Brew Size: {}, Duration Brew Size: {}", key,
                powerBrews == null ? "null" : powerBrews.size(),
                durationBrews == null ? "null" : durationBrews.size());
        return null;
    }


    private static void updateModifierMaps() {
        modifierBrews = new ArrayList<>();
        quaffingBrews = new ArrayList<>();
        colorBrews = new ArrayList<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            ModifierBrewActionInfo info = new ModifierBrewActionInfo(action);
            if (action instanceof EffectModifierBrewAction) {
                modifierBrews.add(info);
            } else if (action instanceof QuaffingSpeedBrewAction) {
                quaffingBrews.add(info);
            } else if (action instanceof SetColorBrewAction) {
                colorBrews.add(info);
            }
        }
    }


    public static class ModifierBrewActionInfo implements Comparable<ModifierBrewActionInfo>, IPatchouliSerializable, ISecretInfo {

        public ItemStack stack;
        public String description;
        public boolean secret;

        public ModifierBrewActionInfo() { }

        public ModifierBrewActionInfo(SetColorBrewAction action) {
            this.stack = action.getKey().toStack();
            this.description = I18n.format("")
        }

        public ModifierBrewActionInfo(BrewAction action) {
            this.stack = action.getKey().toStack();
           if (action instanceof EffectModifierBrewAction) {
               EffectModifierBrewAction effectModifierBrewAction = (EffectModifierBrewAction) action;
               switch (effectModifierBrewAction.getType()) {
                   case NO_PARTICLES:
                       this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.no_particles");
                   case INVERT:
                       this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.invert");
                   case SKIP_BLOCK_EFFECTS:
                       this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.skip_block_effects");
                   case SKIP_ENTITY_EFFECTS:
                       this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.skip_entity_effects");
                   case DISABLE_POWER_LIMIT:
                       this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.disable_power_limit");
               }
           } else if (action instanceof QuaffingSpeedBrewAction) {
               this.description = I18n.format("item.witchery.witches_brews_book.modifiers.quaffing.faster_quaffing");
           } else if (action instanceof )

        }

        public ModifierBrewActionInfo(ItemStack stack, int increment, boolean increasesPower, int ceiling, boolean secret) {
            this.stack = stack;
            this.increment = increment;
            this.ceiling = ceiling;
            this.increasesPower = increasesPower;
            this.secret = secret;
        }


        @Override
        public int compareTo(ModifierBrewActionInfo o) {
            if (o == null) return 1;
            if (increment != o.increment)
                return Integer.compare(increment, o.increment);
            return Integer.compare(ceiling, o.ceiling);
        }

        // Format:
        // <stack>,<increment>,<removesCeiling>,<secret>
        @Override
        public String serialize() {
            StringBuilder sb = new StringBuilder();
            sb.append(ItemStackUtil.serializeStack(stack))
                    .append(",")
                    .append(increment)
                    .append(",")
                    .append(increasesPower)
                    .append(",")
                    .append(ceiling)
                    .append(",")
                    .append(secret);
            return sb.toString();
        }

        @Override
        public void deserialize(String str) {
            String[] splits = str.split(",");
            if (splits.length != 5) return;
            try {
                this.stack = ItemStackUtil.loadStackFromString(splits[0]);
                this.increment = Integer.parseInt(splits[1]);
                this.increasesPower = Boolean.parseBoolean(splits[2]);
                this.ceiling = Integer.parseInt(splits[3]);
                this.secret = Boolean.parseBoolean(splits[4]);
            } catch (Exception ex) {
                WitcheryCompanion.logger.warn("Could not deserialize UpgradeBrewActionInfo from string: {}", str, ex);
            }

        }

        @Override
        public String getSecretKey() {
            return ProgressUtils.getBrewActionSecret(this.stack);
        }

        @Override
        public boolean isSecret() {
            return this.secret;
        }

    }
}
