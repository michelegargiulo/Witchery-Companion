package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.mirror.ITileEntityMirrorAccessor;
import net.msrandom.witchery.block.entity.TileEntityMirror;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityMirror.class)
public abstract class TileEntityMirrorMixin extends WitcheryTileEntity implements ITileEntityMirrorAccessor {

    @Shadow(remap = false)
    private long cooldown;

    @Shadow(remap = false)
    public boolean demonKilled;

    @Override
    public long getCooldown() {
        return Math.max(this.cooldown - ticks, 0);
    }

    @Override
    public boolean isHollow() {
        return this.demonKilled;
    }

}
