package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.brewing.IUpgradeBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.IPatchouliSerializable;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.UpgradeBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class UpgradeBrewActionProcessor implements IComponentProcessor {

    private boolean power;
    private String description;
    private static List<UpgradeBrewActionInfo> powerBrews = null;
    private static List<UpgradeBrewActionInfo> durationBrews = null;


    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        if (powerBrews == null || powerBrews.isEmpty() || durationBrews == null || durationBrews.isEmpty()) {
            updateUpgradeMaps();
        }

        if (iVariableProvider.has("upgrade_type")) {
            this.power = iVariableProvider.get("upgrade_type").equals("power");
        }

        if (iVariableProvider.has("increment_text")) {
            this.description = iVariableProvider.get("increment_text");
        }
    }

    @Override
    public String process(String key) {

        if (key.equals("increment_text"))
            return this.description;

        // Convert the set into a sorted list accessible by index
        int index = ProcessorUtils.splitKeyIndex(key);

        if (index > -1) {

            UpgradeBrewActionInfo info;
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
            if (!shouldShow(info))
                return null;

            return info.serialize();

        }

        WitcheryCompanion.logger.warn("Error while processing UpgradeBrewActionProcessor. Key outside of bounds. " +
                "Key: {}, Power Brew Size: {}, Duration Brew Size: {}", key,
                powerBrews == null ? "null" : powerBrews.size(),
                durationBrews == null ? "null" : durationBrews.size());
        return null;
    }

    private static boolean shouldShow(UpgradeBrewActionInfo info) {
        // Recipe is not secret, always show
        if (!info.secret) return true;

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS && hasUnlockedProgress(info);
    }

    private static boolean hasUnlockedProgress(UpgradeBrewActionInfo info) {
        // Get secret key and return true if the corresponding element has been found
        String key = ProgressUtils.getBrewActionSecret(info.stack);
        return ClientProxy.getLocalWitcheryProgress().hasProgress(key);
    }


    private static void updateUpgradeMaps() {
        SortedSet<UpgradeBrewActionInfo> sortedPower = new TreeSet<>();
        SortedSet<UpgradeBrewActionInfo> sortedDuration = new TreeSet<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof UpgradeBrewAction) {
                UpgradeBrewActionInfo info = new UpgradeBrewActionInfo((UpgradeBrewAction) action);
                if (info.increasesPower) {
                    sortedPower.add(info);
                } else {
                    sortedDuration.add(info);
                }
            }
        }
        powerBrews = new ArrayList<>(sortedPower);
        durationBrews = new ArrayList<>(sortedDuration);
    }


    public static class UpgradeBrewActionInfo implements Comparable<UpgradeBrewActionInfo>, IPatchouliSerializable, ISecretInfo {

        public ItemStack stack = new ItemStack(Items.AIR);
        public int increment = 0;
        public int ceiling = 0;
        public boolean increasesPower = false;
        public boolean secret = false;

        public UpgradeBrewActionInfo() { }

        public UpgradeBrewActionInfo(UpgradeBrewAction action) {
            this.stack = action.getKey().toStack();
            this.increment = action.getIncrease();
            this.secret = action.getHidden();
            this.ceiling = action.getLimit();
            if ((Object)action instanceof IUpgradeBrewActionAccessor) {
                IUpgradeBrewActionAccessor accessor = (IUpgradeBrewActionAccessor) (Object)action;
                this.increasesPower = accessor.increasesPower();
            } else {
                this.increasesPower = false;
            }

        }

        public UpgradeBrewActionInfo(ItemStack stack, int increment, boolean increasesPower, int ceiling, boolean secret) {
            this.stack = stack;
            this.increment = increment;
            this.ceiling = ceiling;
            this.increasesPower = increasesPower;
            this.secret = secret;
        }


        @Override
        public int compareTo(UpgradeBrewActionInfo o) {
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
            if (str == null)
                return;
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
