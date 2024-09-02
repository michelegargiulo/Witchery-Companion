package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.brewing.IUpgradeBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.IPatchouliSerializable;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ProgressionProcessor;
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


public class UpgradeBrewActionProcessor extends ProgressionProcessor implements IComponentProcessor {

    private boolean power;
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
    }

    @Override
    public String process(String key) {

        // Convert the set into a sorted list accessible by index
        int index = ProcessorUtils.getIndexFromKey(key, "upgrade_brew_item");

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

        public ItemStack stack;
        public int increment;
        public boolean increasesPower;
        public boolean secret;

        public UpgradeBrewActionInfo() { }

        public UpgradeBrewActionInfo(UpgradeBrewAction action) {
            this.stack = action.getKey().toStack();
            this.increment = action.getIncrease();
            this.secret = action.getHidden();
            if ((Object)action instanceof IUpgradeBrewActionAccessor) {
                IUpgradeBrewActionAccessor accessor = (IUpgradeBrewActionAccessor) (Object)action;
                this.increasesPower = accessor.increasesPower();
            } else {
                this.increasesPower = false;
            }

        }

        public UpgradeBrewActionInfo(ItemStack stack, int increment, boolean increasesPower, boolean secret) {
            this.stack = stack;
            this.increment = increment;
            this.increasesPower = increasesPower;
            this.secret = secret;
        }


        @Override
        public int compareTo(UpgradeBrewActionInfo o) {
            if (o == null) return 1;
            if (increment != o.increment)
                return Integer.compare(increment, o.increment);
            return stack.getItem().getRegistryName().compareTo(o.stack.getItem().getRegistryName());
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
                    .append(secret);
            return sb.toString();
        }

        @Override
        public void deserialize(String str) {
            String[] splits = str.split(",");
            if (splits.length != 4) return;
            try {
                this.stack = ItemStackUtil.loadStackFromString(splits[0]);
                this.increment = Integer.parseInt(splits[1]);
                this.increasesPower = Boolean.parseBoolean(splits[2]);
                this.secret = Boolean.parseBoolean(splits[3]);
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
