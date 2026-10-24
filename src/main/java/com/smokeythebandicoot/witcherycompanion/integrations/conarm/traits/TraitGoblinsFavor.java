package com.smokeythebandicoot.witcherycompanion.integrations.conarm.traits;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import c4.conarm.lib.traits.AbstractArmorTrait;

/**
 * Armor counterpart of the Koboldite head trait. Marker Trait: Witchery checks if the player is wearing armor with
 * this trait, to enhance the worship of Hobgoblins (see TileEntityWorshipStatueMixin)
 **/
public class TraitGoblinsFavor extends AbstractArmorTrait {

    public static final String id = WitcheryCompanion.prefix("goblins_favor");

    public TraitGoblinsFavor() {
        super(id, 0x61aadf);
    }
}
