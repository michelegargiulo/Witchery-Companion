package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Material trait of the Koboldite head. It is a trait, not a modifier, so it cannot be added to other materials
 * (except with Embossment). Marker trait: a pickaxe with it is treated by Witchery as a Koboldite Pickaxe
 * (see TinkersEquivalents). Its armor counterpart is in the ConArm integration
 **/
public class TraitGoblinsFavor extends AbstractTrait {

    public static final String id = WitcheryCompanion.prefix("goblins_favor");

    public TraitGoblinsFavor() {
        super(id, 0x61aadf);
    }
}
