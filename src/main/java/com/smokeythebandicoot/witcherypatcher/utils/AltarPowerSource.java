package com.smokeythebandicoot.witcherypatcher.utils;

public class AltarPowerSource {

    private final int factor;
    private final int limit;

    public AltarPowerSource(int factor, int limit) {
        this.factor = factor;
        this.limit = limit;
    }

    public int getFactor() {
        return factor;
    }

    public int getLimit() {
        return limit;
    }
}
