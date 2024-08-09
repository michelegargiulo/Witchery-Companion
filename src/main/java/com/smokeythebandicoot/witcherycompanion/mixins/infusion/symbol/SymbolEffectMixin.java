package com.smokeythebandicoot.witcherycompanion.mixins.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.api.symboleffect.ISymbolEffectAccessor;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 Mixins:
 [Feature] Expose Symbol Effects to API
 */
@Mixin(SymbolEffect.class)
public abstract class SymbolEffectMixin implements ISymbolEffectAccessor {

    @Shadow(remap = false) @Final
    private boolean hasKnowledge;

    @Override
    public boolean accessor_getHasKnowledge() {
        return this.hasKnowledge;
    }

}
