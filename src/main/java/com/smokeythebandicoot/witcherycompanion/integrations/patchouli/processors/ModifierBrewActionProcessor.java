package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.IPatchouliSerializable;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.*;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;


public class ModifierBrewActionProcessor implements IComponentProcessor {

    private String modifierType = "effect";
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
        int index = ProcessorUtils.splitKeyIndex(key).index;

        if (index > -1) {

            ModifierBrewActionInfo info;
            if ("color".equals(modifierType) && index < colorBrews.size()) {
                info = colorBrews.get(index);
            } else if ("quaffing".equals(modifierType) && index < colorBrews.size()) {
                info = quaffingBrews.get(index);
            } else if ((modifierType == null || modifierType.equals("effect")) && index < modifierBrews.size()) {
                info = modifierBrews.get(index);
            } else {
                return null;
            }

            return info.serialize();

        }

        WitcheryCompanion.logger.warn("Error while processing UpgradeBrewActionProcessor. Key outside of bounds. " +
                "Key: {}", key);
        return null;
    }


    private static void updateModifierMaps() {
        modifierBrews = new ArrayList<>();
        quaffingBrews = new ArrayList<>();
        colorBrews = new ArrayList<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof EffectModifierBrewAction) {
                modifierBrews.add(new ModifierBrewActionInfo((EffectModifierBrewAction)action));
            } else if (action instanceof QuaffingSpeedBrewAction) {
                quaffingBrews.add(new ModifierBrewActionInfo((QuaffingSpeedBrewAction)action));
            } else if (action instanceof SetColorBrewAction) {
                colorBrews.add(new ModifierBrewActionInfo((SetColorBrewAction)action));
            }
        }
    }


    public static class ModifierBrewActionInfo implements Comparable<ModifierBrewActionInfo>, IPatchouliSerializable, ISecretInfo {
        public ItemStack stack = new ItemStack(Items.AIR);

        public String description = "";
        public boolean secret = false;
        public ModifierBrewActionInfo() { }

        public ModifierBrewActionInfo(SetColorBrewAction action) {
            this.secret = action.getHidden();
            this.stack = action.getKey().toStack();
            this.description = action.getName();
        }

        public ModifierBrewActionInfo(EffectModifierBrewAction action) {
            this.stack = action.getKey().toStack();
            this.secret = action.getHidden();
            switch (action.getType()) {
                case NO_PARTICLES:
                    this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.no_particles");
                    break;
                case INVERT:
                    this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.invert");
                    break;
                case SKIP_BLOCK_EFFECTS:
                    this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.skip_block_effects");
                    break;
                case SKIP_ENTITY_EFFECTS:
                    this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.skip_entity_effects");
                    break;
                case DISABLE_POWER_LIMIT:
                    this.description = I18n.format("item.witchery.witches_brews_book.modifiers.general.disable_power_limit");
                    break;
            }
        }

        public ModifierBrewActionInfo(QuaffingSpeedBrewAction action) {
            this.stack = action.getKey().toStack();
            this.secret = action.getHidden();
            this.description = I18n.format("item.witchery.witches_brews_book.modifiers.quaffing.faster_quaffing");
        }

        @Override
        public int compareTo(ModifierBrewActionInfo o) {
            if (o == null) return -1;
            return Boolean.compare(this.secret, o.secret);
        }

        @Override
        public String serialize() {
            StringBuilder sb = new StringBuilder();
            sb.append(ItemStackUtil.serializeStack(stack))
                    .append("#$")
                    .append(description)
                    .append("#$")
                    .append(secret);
            return sb.toString();
        }

        @Override
        public void deserialize(String str) {
            String[] splits = str.split("#$");
            if (splits.length != 3) return;
            try {
                this.stack = ItemStackUtil.loadStackFromString(splits[0]);
                this.description = splits[1];
                this.secret = Boolean.parseBoolean(splits[2]);
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
