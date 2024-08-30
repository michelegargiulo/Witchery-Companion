package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.player.FamiliarInfo;
import com.smokeythebandicoot.witcherycompanion.api.player.PlayerExtendedDataApi;
import com.smokeythebandicoot.witcherycompanion.api.symboleffect.ISymbolEffectAccessor;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.common.InfusionPower;
import net.msrandom.witchery.entity.familiar.FamiliarType;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.resources.CreatureFormStatManager;
import net.msrandom.witchery.rite.curse.Curse;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.transformation.CreatureTraitType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.ExtendedPlayer")
@ZenRegister
public class ExtendedPlayerHandler {

    @ZenMethod
    @ZenDoc(value="Returns true if the player can manifest")
    public static boolean canManifest(IPlayer player) {
        return PlayerExtendedDataApi.canManifest(CraftTweakerMC.getPlayer(player));
    }

    @ZenMethod
    @ZenDoc(value="Returns the coven size of the player")
    public static int getCovenSize(IPlayer player) {
        return PlayerExtendedDataApi.getCovenSize(CraftTweakerMC.getPlayer(player));
    }

    @ZenMethod
    @ZenDoc(value="Returns current player bottling skill")
    public static int getBottlingSkill(IPlayer player) {
        return PlayerExtendedDataApi.getBottlingSkill(CraftTweakerMC.getPlayer(player));
    }

    @ZenMethod
    @ZenDoc(value="Returns an array of learnt Symbol Effects")
    public static SymbolEffectWrapper[] getLearntSymbols(IPlayer player) {
        return PlayerExtendedDataApi.getLearntSymbols(CraftTweakerMC.getPlayer(player))
                .stream()
                .map(SymbolEffectWrapper::new)
                .toArray(SymbolEffectWrapper[]::new);
    }

    @ZenMethod
    @ZenDoc(value="Returns the list of currently applied curses")
    public static CurseWrapper[] getAppliedCurses(IPlayer iPlayer) {
        return PlayerExtendedDataApi.getAppliedCurses(CraftTweakerMC.getPlayer(iPlayer))
                .stream()
                .map(CurseWrapper::new)
                .toArray(CurseWrapper[]::new);
    }

    @ZenMethod
    @ZenDoc(value="Returns the info about the player's familiar")
    public static FamiliarInfoWrapper getFamiliarInfo(IPlayer iPlayer) {
        return new FamiliarInfoWrapper(
                PlayerExtendedDataApi.getFamiliarInfo(CraftTweakerMC.getPlayer(iPlayer))
        );
    }

    @ZenMethod
    @ZenDoc(value="Returns the current infusion power. Infusion Power contains Infusion Type " +
            "[DEFUSED, INFERNAL, OTHERWHERE], currentPower and maxPower")
    public static InfusionPowerWrapper getCurrentInfusion(IPlayer iPlayer) {
        return new InfusionPowerWrapper(
                PlayerExtendedDataApi.getCurrentInfusion(CraftTweakerMC.getPlayer(iPlayer))
        );
    }

    @ZenMethod
    @ZenDoc(value="Returns the current form of the player")
    public static CreatureFormWrapper getCurrentForm(IPlayer iPlayer) {
        return new CreatureFormWrapper(
                PlayerExtendedDataApi.getCurrentForm(CraftTweakerMC.getPlayer(iPlayer))
        );
    }

    @ZenMethod
    @ZenDoc(value="Returns the effective form of the player")
    public static CreatureFormWrapper getEffectiveForm(IPlayer iPlayer) {
        return new CreatureFormWrapper(
                PlayerExtendedDataApi.getEffectiveForm(CraftTweakerMC.getPlayer(iPlayer))
        );
    }

    @ZenMethod
    @ZenDoc(value="Returns the current size set by Potion of Resizing")
    public static float getResizingPotionScale(IPlayer iPlayer) {
        return PlayerExtendedDataApi.getResizingPotionScale(CraftTweakerMC.getPlayer(iPlayer));
    }


    /** Helper class to flatten and expose Symbol Effect properties */
    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.playerex.SymbolEffect")
    public static class SymbolEffectWrapper {

        private final SymbolEffect symbolEffect;

        public SymbolEffectWrapper(SymbolEffect symbolEffect) {
            this.symbolEffect = symbolEffect;
        }

        @ZenMethod
        public int getChargeCost(IPlayer iPlayer, int level) {
            EntityPlayer player = CraftTweakerMC.getPlayer(iPlayer);
            return symbolEffect.getChargeCost(player, level);
        }

        @ZenMethod
        public boolean isCurse() {
            return symbolEffect.isCurse();
        }

        @ZenMethod
        public boolean fallsToEarth() {
            return symbolEffect.fallsToEarth();
        }

        @ZenMethod
        public boolean hasKnowledge() {
            return ((ISymbolEffectAccessor)symbolEffect).hasKnowledge();
        }

        @ZenMethod
        public long getCooldownTicks(IPlayer iPlayer) {
            EntityPlayer player = CraftTweakerMC.getPlayer(iPlayer);
            return symbolEffect.cooldownRemaining(player.getEntityData());
        }

        @ZenMethod
        public boolean isVisible() {
            return symbolEffect.isVisible();
        }

        @ZenMethod
        public String getName() {
            ResourceLocation resourceLocation = SymbolEffect.REGISTRY.getKey(symbolEffect);
            return resourceLocation == null ? "<null>" : resourceLocation.toString();
        }

        @ZenMethod
        public String getDescription() {
            return symbolEffect.getDescription();
        }

    }

    /** Helper class to flatten and expose Curse properties */
    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.playerex.Curse")
    public static class CurseWrapper {

        private final Curse curse;

        public CurseWrapper(Curse curse) {
            this.curse = curse;
        }

        @ZenMethod
        public String getName() {
            ResourceLocation resourceLocation = Curse.REGISTRY.getKey(curse);
            return resourceLocation == null ? "<null>" : resourceLocation.toString();
        }

    }

    /** Helper class to flatten and expose Familiar properties */
    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.playerex.Familiar")
    public static class FamiliarInfoWrapper {

        private final FamiliarInfo familiarInfo;

        public FamiliarInfoWrapper(FamiliarInfo info) {
            this.familiarInfo = new FamiliarInfo(
                    info.getFamiliarEntity(),
                    info.getFamiliarType(),
                    info.getName(),
                    info.getColor(),
                    info.isSummoned()
            );
        }

        @ZenMethod
        public IEntity getEntity() {
            return CraftTweakerMC.getIEntity(familiarInfo.getFamiliarEntity());
        }

        @ZenMethod
        public String getFamiliarType() {
            Class<? extends Entity> familiarClass = FamiliarType.REGISTRY.getKey(familiarInfo.getFamiliarType());
            if (familiarClass != null) {
                ResourceLocation entityRegistryName = EntityList.getKey(familiarClass);
                return entityRegistryName == null ? "<unknown entity>" : entityRegistryName.toString();
            }
            return "<unknown familiar type>";
        }

        @ZenMethod
        public String getName() {
            return familiarInfo.getName();
        }

        @ZenMethod
        public int getColor() {
            return familiarInfo.getColor().getColorValue();
        }

        @ZenMethod
        public boolean isSummoned() {
            return familiarInfo.isSummoned();
        }
    }

    /** Helper class to flatten and expose Infusion Power properties */
    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.playerex.InfusionPower")
    public static class InfusionPowerWrapper {

        private final InfusionPower power;

        public InfusionPowerWrapper(InfusionPower power) {
            this.power = power;
        }

        @ZenMethod
        public float getCurrentPower() {
            return this.power.getCurrentPower();
        }

        @ZenMethod
        public float getMaxPower() {
            return this.power.getMaxPower();
        }

        @ZenMethod
        public String getName() {
            return power.getInfusionType().getTranslationKey();
        }
    }

    /** Helper class to flatten and expose Creature Form properties */
    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.playerex.CreatureForm")
    public static class CreatureFormWrapper {

        private final CreatureForm form;

        private final CreatureForm.Stats stats;

        public CreatureFormWrapper(CreatureForm form) {
            this.form = form;
            this.stats = form == null ? null : CreatureFormStatManager.INSTANCE.getStats(form);
        }

        @ZenMethod
        public String getName() {
            ResourceLocation resourceLocation = CreatureForm.REGISTRY.getKey(form);
            return resourceLocation == null ? null : resourceLocation.toString();
        }

        @ZenMethod
        public Float getWidth() {
            return stats == null ? null : stats.getWidth();
        }

        @ZenMethod
        public Float getHeight() {
            return stats == null ? null : stats.getHeight();
        }

        @ZenMethod
        public Float getEyeHeight() {
            return stats == null ? null : stats.getEyeHeight();
        }

        @ZenMethod
        public Float getStepHeight() {
            return stats == null ? null : stats.getStepHeight();
        }

        @ZenMethod
        public Boolean canFly() {
            return stats == null ? null : stats.canFly();
        }

        @ZenMethod
        public Boolean canHowl() {
            return stats == null ? null : stats.canHowl();
        }

        @ZenMethod
        public IIngredient getHoldableItem() {
            return stats == null ? null : CraftTweakerMC.getIIngredient(stats.getHoldableItem());
        }

        @ZenMethod
        public Boolean wearsArmor() {
            return stats == null ? null : stats.wearsArmor();
        }

        @ZenMethod
        public Double getReflectionDamage() {
            return stats == null ? null : stats.getReflectionDamage();
        }

        @ZenMethod
        public String getCreatureTraitName() {
            if (stats == null) return null;
            ResourceLocation resourceLocation = CreatureTraitType.REGISTRY.getKey(stats.getCreatureTraitType());
            return resourceLocation == null ? null : resourceLocation.toString();
        }

        @ZenMethod
        public int getMaxLevel() {
            if (stats == null || stats.getCreatureTraitType() == null) {
                return -1;
            }
            return stats.getCreatureTraitType().getMaxLevel();
        }

    }

}
