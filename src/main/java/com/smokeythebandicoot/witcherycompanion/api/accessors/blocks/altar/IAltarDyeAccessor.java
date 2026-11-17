package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar;

/** Accessor for the dye of the cloth of an Altar block (implemented by TileEntityAltar, each Altar part has its own) */
public interface IAltarDyeAccessor {

    /** Dye of the cloth, as {@code EnumDyeColor#getMetadata()}, or -1 if the Altar is not dyed */
    int witcherycompanion$accessor$getDyeColor();

    /**
     * Sets the dye of the cloth (-1 to remove it). Marks the tile entity dirty and syncs it to the clients.
     * Returns false if the Altar already had this dye
     */
    boolean witcherycompanion$accessor$setDyeColor(int dyeMeta);

}
