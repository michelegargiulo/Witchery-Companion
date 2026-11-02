package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/** A circle drawn for a rite, as seen by ZenScript */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.RiteCircle")
@ZenRegister
public class RiteCircle {

    private final String glyph;
    private final int size;

    public RiteCircle(String glyph, int size) {
        this.glyph = glyph;
        this.size = size;
    }

    @ZenGetter("glyph")
    @ZenDoc(value = "Registry name of the glyph block, for example witchery:ritual_circle_glyph")
    public String getGlyph() {
        return glyph;
    }

    @ZenGetter("size")
    @ZenDoc(value = "Size of the circle: 1, 2 or 3")
    public int getSize() {
        return size;
    }

}
