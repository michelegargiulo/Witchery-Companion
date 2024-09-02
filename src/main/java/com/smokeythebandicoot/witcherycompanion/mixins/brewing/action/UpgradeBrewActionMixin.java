package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action;

import com.smokeythebandicoot.witcherycompanion.api.brewing.IUpgradeBrewActionAccessor;
import kotlin.jvm.functions.Function1;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.brewing.action.BrewActionSerializer;
import net.msrandom.witchery.brewing.action.UpgradeBrewAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Feature] Expose the type of increase (power or duration)
 */
@Mixin(UpgradeBrewAction.class)
public abstract class UpgradeBrewActionMixin implements IUpgradeBrewActionAccessor {

    @Unique
    private boolean witchery_Patcher$power;

    /** This Mixins injects at return to read the power boolean value and store it in the class for accessor use */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void readIncrease(ItemKey key, BrewActionSerializer<?> serializer, boolean power, int increase, int limit, int cost, Function1<?, ?> upgradable, CallbackInfo ci) {
        this.witchery_Patcher$power = power;
    }

    @Override
    public boolean increasesPower() {
        return this.witchery_Patcher$power;
    }
}
