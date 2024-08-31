package com.smokeythebandicoot.witcherycompanion.api.player;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.msrandom.witchery.entity.familiar.FamiliarType;

public class FamiliarInfo {

    private Entity familiarEntity;

    private FamiliarType<?, ?> familiarType;

    private String name;

    private EnumDyeColor color;

    private boolean isSummoned;

    public Entity getFamiliarEntity() {
        return familiarEntity;
    }

    public FamiliarType<?, ?> getFamiliarType() {
        return familiarType;
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

    public FamiliarInfo(Entity familiarEntity, FamiliarType<?, ?> type, String name, EnumDyeColor color, boolean isSummoned) {
        this.familiarEntity = familiarEntity;
        this.familiarType = type;
        this.name = name;
        this.color = color;
        this.isSummoned = isSummoned;
    }
}
