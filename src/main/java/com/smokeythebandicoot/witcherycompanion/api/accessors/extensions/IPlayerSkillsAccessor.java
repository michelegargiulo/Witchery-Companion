package com.smokeythebandicoot.witcherycompanion.api.accessors.extensions;

/** Access to the skills that W:R keeps in the data of the player, and that it only lets increase (see PlayerExtendedDataMixin) */
public interface IPlayerSkillsAccessor {

    /** The bottling skill, as it is stored */
    int witcherycompanion$accessor$getBottlingSkillRaw();

    void witcherycompanion$accessor$setBottlingSkill(int skill);

    /** The throwing skill, as it is stored (W:R's own getter returns the bottling skill, unless the throwing skill fix is on) */
    int witcherycompanion$accessor$getThrowingSkillRaw();

    void witcherycompanion$accessor$setThrowingSkill(int skill);

}
