package com.smokeythebandicoot.witcherycompanion.api.progress;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class WitcheryProgress implements IWitcheryProgress {

    private final Set<String> unlockedProgress = new HashSet<>();

    @Nonnull
    @Override
    public Set<String> getUnlockedProgress() {
        return new HashSet<>(unlockedProgress);
    }

    @Override
    public void setUnlockedProgress(@Nonnull Set<String> progress) {
        unlockedProgress.clear();
        unlockedProgress.addAll(progress);
    }

    @Override
    public void unlockProgress(@Nonnull String progress) {
        unlockedProgress.add(progress);
    }

    @Override
    public boolean hasProgress(@Nonnull String progress) {
        return unlockedProgress.contains(progress);
    }

    @Override
    public void lockProgress(@Nonnull String progress) {
        unlockedProgress.remove(progress);
    }

    @Override
    public void resetProgress() {
        unlockedProgress.clear();
    }

}
