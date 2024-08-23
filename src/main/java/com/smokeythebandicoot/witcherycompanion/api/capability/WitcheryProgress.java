package com.smokeythebandicoot.witcherycompanion.api.capability;

import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;

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
        if (progress == null)
            return;
        unlockedProgress.add(progress);
    }

    @Override
    public boolean hasProgress(@Nonnull String progress) {
        if (progress == null)
            return false;
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
