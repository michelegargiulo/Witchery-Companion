package com.smokeythebandicoot.witcherycompanion.api.player;

import com.smokeythebandicoot.witcherycompanion.api.symboleffect.ISymbolEffectAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.common.CovenWitchData;
import net.msrandom.witchery.common.InfusionPower;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.FamiliarInstance;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.extensions.LivingExtendedData;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.init.WitcheryDataExtensions;
import net.msrandom.witchery.rite.curse.Curse;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.*;

public class PlayerExtendedDataApi {

    /** Returns true if the player can manifest */
    public static boolean canManifest(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        return playerEx.getSpiritData().getManifestationDuration() > 0;
    }

    /** Returns the coven size of the player */
    public static int getCovenSize(EntityPlayer player) {
        PlayerExtendedData extendedData = WitcheryUtils.getExtension(player);
        Set<CovenWitchData> coven = extendedData.coven;
        return coven == null ? 0 : coven.size();
    }

    /** Returns current player bottling skill */
    public static int getBottlingSkill(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        return playerEx.getBottlingSkill();
    }

    /** Returns the list of learnt Symbol Effects */
    public static List<SymbolEffect> getLearntSymbols(EntityPlayer player) {

        List<SymbolEffect> knownSymbolEffects = new ArrayList<>();
        NBTTagCompound compound = player.getEntityData();
        if (compound.hasKey("WitcherySpellBook")) {

            Iterator<Map.Entry<ResourceLocation, SymbolEffect>> iterator = SymbolEffect.REGISTRY.iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, SymbolEffect> entry = iterator.next();
                SymbolEffect effect = entry.getValue();
                ISymbolEffectAccessor accessor = (ISymbolEffectAccessor) effect;
                if (accessor.accessor_getHasKnowledge()) {
                    if (effect.hasValidKnowledge(player, compound)) {
                        knownSymbolEffects.add(effect);
                    }
                }

            }

        }
        return knownSymbolEffects;
    }

    /** Returns the list of currently applied curses */
    public static List<Curse> getAppliedCurses(EntityPlayer player) {
        LivingExtendedData livingEx = WitcheryDataExtensions.LIVING.get(player);
        return new ArrayList<>(livingEx.getCurses().keySet());
    }

    /** Returns the info about the player's familiar */
    public static FamiliarInfo getFamiliarInfo(EntityPlayer player) {
        PlayerExtendedData extendedData = WitcheryUtils.getExtension(player);
        FamiliarInstance familiar = extendedData.familiar;

        if (familiar == null) {
            return new FamiliarInfo(null, "", null, false);
        }

        Familiar<?> boundFamiliar = Familiars.getBoundFamiliar(player);
        Entity familiarEntity = null;
        if (boundFamiliar != null) {
            familiarEntity = boundFamiliar.getEntity();
        }

        return new FamiliarInfo(
          familiarEntity,                       // Can be null
          Familiars.getFamiliarName(player),    // Can be null
          familiar.getColor(),                  // Can be null
          familiar.isSummoned()                 // true if summoned, false if otherwise or familiar is null
        );
    }

    /** Returns the current infusion power. Infusion Power contains Infusion Type
     * [DEFUSED, INFERNAL, OTHERWHERE], currentPower and maxPower */
    public static InfusionPower getCurrentInfusion(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        return playerEx.infusionPower;
    }

    /** Returns the current form of the player */
    public static CreatureForm getCurrentForm(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        return playerEx.getCurrentForm();
    }

    /** Returns the effective form of the player */
    public static CreatureForm getEffectiveForm(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        return playerEx.getEffectiveForm();
    }

    /** Returns the current size set by Potion of Resizing */
    public static float getResizingPotionScale(EntityPlayer player) {
        IEntityPlayerAccessor playerResize = (IEntityPlayerAccessor) player;
        return playerResize.accessor_getCurrentResizingScale();
    }

}
