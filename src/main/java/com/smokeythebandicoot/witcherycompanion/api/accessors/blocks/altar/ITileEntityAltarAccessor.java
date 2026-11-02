package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar;

public interface ITileEntityAltarAccessor {

    void witcherycompanion$accessor$setCore(boolean isCore);

    int witcherycompanion$accessor$getEnhancementLevel();

    /** Range multiplier of the Altar. The range in blocks is this value times 16 (see TileEntityAltar.getRange()) */
    int witcherycompanion$accessor$getRangeScale();

    /** Max power multiplier of the Altar */
    int witcherycompanion$accessor$getPowerScale();

    /** Max power given by the power sources alone, before the multiplier (TileEntityAltar.getMaxPower() applies it) */
    float witcherycompanion$accessor$getBaseMaxPower();
}
