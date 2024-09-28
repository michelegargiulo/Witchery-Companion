package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action;

import com.smokeythebandicoot.witcherycompanion.api.brewing.IIncrementBrewActionAccessor;
import kotlin.jvm.functions.Function1;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.brewing.action.BrewActionSerializer;
import net.msrandom.witchery.brewing.action.IncrementBrewAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IncrementBrewAction.class)
public abstract class IncrementBrewActionMixin implements IIncrementBrewActionAccessor {

    @Unique
    private boolean witchery_Patcher$extent;

    /** This Mixins injects at return to read the extent boolean value and store it in the class for accessor use */
    @Inject(method = "<init>", remap = false, at = @At(value = "RETURN"))
    private void readExtent(ItemKey key, BrewActionSerializer<?> serializer, boolean extent, int limit, int cost, Function1<?, ?> integer, CallbackInfo ci) {
        this.witchery_Patcher$extent = extent;
    }

    @Override
    public boolean increasesExtent() {
        return this.witchery_Patcher$extent;
    }

}
