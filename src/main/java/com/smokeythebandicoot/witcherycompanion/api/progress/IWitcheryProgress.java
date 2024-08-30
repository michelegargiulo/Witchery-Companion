package com.smokeythebandicoot.witcherycompanion.api.progress;

import javax.annotation.Nonnull;
import java.util.Set;

public interface IWitcheryProgress {

    @Nonnull Set<String> getUnlockedProgress();

    void setUnlockedProgress(Set<String> progress);

    void unlockProgress(String progress);

    boolean hasProgress(String progress);

    void lockProgress(String progress);

    void resetProgress();

}
