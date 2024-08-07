package com.smokeythebandicoot.witcherycompanion.api.player;

import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.common.CovenWitchData;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.Set;

public class PlayerExtendedDataApi {

    /**
     * Familiar data (entity, type, isSummon, etc)
     * Learned symbols (torment, mors mordre, etc)
     * Bottling skill
     * etc...
     */

    /** Returns the coven size of the player */
    public int getCovenSize(EntityPlayer player) {
        PlayerExtendedData extendedData = WitcheryUtils.getExtension(player);
        Set<CovenWitchData> coven = extendedData.coven;
        return coven == null ? 0 : coven.size();
    }

    /*public EntityLivingBase getFamiliar(EntityPlayer player) {
        PlayerExtendedData extendedData = WitcheryUtils.getExtension(player);
        FamiliarInstance familiar = extendedData.familiar;

    }*/



    /** Returns the current size set by Potion of Resizing */
    public float getResizingPotionScale(EntityPlayer player) {
        IEntityPlayerAccessor playerResize = (IEntityPlayerAccessor) player;
        return playerResize.accessor_getCurrentResizingScale();
    }

}
