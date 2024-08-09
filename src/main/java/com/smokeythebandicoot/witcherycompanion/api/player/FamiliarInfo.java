package com.smokeythebandicoot.witcherycompanion.api.player;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;

public class FamiliarInfo {

    private Entity familiarEntity;

    private String name;

    private EnumDyeColor color;

    private boolean isSummoned;

    public Entity getFamiliarEntity() {
        return familiarEntity;
    }

    public String getName() {
        return name;
    }

    public EnumDyeColor getColor() {
        return color;
    }

    public boolean isSummoned() {
        return isSummoned;
    }

    public FamiliarInfo(Entity familiarEntity, String name, EnumDyeColor color, boolean isSummoned) {
        this.familiarEntity = familiarEntity;
        this.name = name;
        this.color = color;
        this.isSummoned = isSummoned;
    }
}
