package com.smokeythebandicoot.witcherycompanion.api.player;

public interface IEntityPlayerAccessor {

    float accessor_getCurrentResizingScale();

    void accessor_setCurrentResizingScale(float scale);

    float accessor_getCurrentFormWidthScale();

    void accessor_setCurrentFormWidthScale(float scale);

    float accessor_getCurrentFormHeightScale();

    void accessor_setCurrentFormHeightScale(float scale);

    float accessor_getCurrentFormEyeHeightScale();

    void accessor_setCurrentFormEyeHeightScale(float scale);

    float accessor_getCurrentFormStepHeightScale();

    void accessor_setCurrentFormStepHeightScale(float scale);

}
