package com.smokeythebandicoot.witcherycompanion.mixins.brewing;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.msrandom.witchery.brewing.EffectLevelCounter;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.UpgradableModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModifiersEffect.class)
public abstract class ModifiersEffectMixin {

    @Shadow(remap = false) @Final
    public boolean ritualised;

    @Shadow(remap = false)
    public UpgradableModifier strength;

    @Shadow(remap = false)
    public int strengthPenalty;

    @Shadow(remap = false) @Final
    private static int[] covenToMaxStrength;

    @Shadow(remap = false) @Final
    public int covenSize;

    @Shadow(remap = false)
    public boolean inverted;

    @Shadow(remap = false) @Final
    public double powerScalingFactor;

    @Shadow(remap = false) @Final
    public boolean isGlancing;

    @Shadow(remap = false) @Final
    public Vec3d impactLocation;

    @Shadow(remap = false) @Final
    public EntityPlayer caster;

    @Shadow(remap = false) @Final
    public double durationScalingFactor;

    @Shadow(remap = false) @Final
    public EffectLevelCounter effectLevel;

    @Shadow(remap = false)
    public boolean noParticles;

    @Shadow(remap = false)
    public boolean disableBlockTarget;

    @Shadow(remap = false)
    public boolean disableEntityTarget;

    @Shadow(remap = false)
    public boolean strengthCeilingDisabled;

    @Shadow(remap = false)
    public boolean powerCeilingDisabled;

    @Shadow(remap = false)
    public boolean protectedFromNegativePotions;

    /**
     * @author
     * @reason
     */
    @Inject(method = "getStrength", remap = false, cancellable = true, at = @At("HEAD"))
    public void getStrength(CallbackInfoReturnable<Integer> cir) {
        int result = -1;
        if (this.ritualised) {
            result = Math.min(
                    Math.max(this.strength.getValue() - this.strengthPenalty, 0),
                    covenToMaxStrength[
                            Math.min(this.covenSize, covenToMaxStrength.length - 1)]);
        } else {
            Utils.logChat(this.strength.getValue() + " - " + this.strengthPenalty);
            result = Math.max(this.strength.getValue() - this.strengthPenalty, 0);
        }
        Utils.logChat(result);
        cir.setReturnValue(result);
    }

    @Inject(method = "reset", remap = false, cancellable = true, at = @At("HEAD"))
    public void unreset(CallbackInfo ci) {
        this.inverted = false;
        //ci.cancel();
        // this.noParticles = false;
    }

    @Unique
    public ModifiersEffect witchery_Patcher$getCopy() {
        ModifiersEffect clone = new ModifiersEffect(
                this.powerScalingFactor,
                this.durationScalingFactor,
                this.isGlancing,
                this.impactLocation,
                this.ritualised,
                this.covenSize,
                this.caster
        );
        // clone.effectLevel = this.effectLevel;
        clone.strength = new UpgradableModifier(clone);
        clone.strength.setValue(this.strength.getValue());
        clone.strength.setTotal(this.strength.getTotal());

        clone.duration = new UpgradableModifier(clone);
        clone.duration.setValue(this.strength.getValue());
        clone.duration.setTotal(this.strength.getTotal());

        clone.strengthPenalty = this.strengthPenalty ;
        clone.noParticles = this.noParticles;
        clone.inverted = this.inverted ;
        clone.disableBlockTarget = this.disableBlockTarget;
        clone.disableEntityTarget = this.disableEntityTarget ;
        clone.strengthCeilingDisabled = this.strengthCeilingDisabled;
        clone.powerCeilingDisabled = this.powerCeilingDisabled ;
        clone.protectedFromNegativePotions = this.protectedFromNegativePotions;

        return clone;
    }

}
