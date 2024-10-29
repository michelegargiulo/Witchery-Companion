package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.worshipstatue.ITileEntityWorshipStatueAccessor;
import net.msrandom.witchery.block.entity.TileEntityWorshipStatue;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Accessor for worship level
 */
@Mixin(TileEntityWorshipStatue.class)
public abstract class TileEntityWorshipStatueMixin extends WitcheryTileEntity implements ITileEntityWorshipStatueAccessor {

    @Unique
    private int witchery_Patcher$worshipLevel = 0;

    private TileEntityWorshipStatueMixin(int worshipLevel) {
        this.witchery_Patcher$worshipLevel = worshipLevel;
    }

    @Inject(method = "updateWorshippersAndGetLevel", remap = false, at = @At("RETURN"))
    private void retrieveWorshipLevel(CallbackInfoReturnable<Integer> cir) {
        this.witchery_Patcher$worshipLevel = cir.getReturnValue();
    }

    @Override
    public int getWorshipLevel() {
        return this.witchery_Patcher$worshipLevel;
    }
}
