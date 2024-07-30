package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.AltarApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import net.msrandom.witchery.util.BlockUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Set;

/**
 Mixins:
 [Bugfix] Fixes PowerSources being lost upon world load/reload, requiring them to be interacted or
    broken and re-placed to be registered correctly again
 [Tweak] Great performance gain by caching Altar PowerSourceTable
 */
@Mixin(value = TileEntityAltar.class)
public abstract class TileEntityAltarMixin extends WitcheryTileEntity implements IPowerSource {

    @Shadow(remap = false)
    private long lastPowerUpdate;

    @Shadow(remap = false)
    private int enhancementLevel;

    @Shadow(remap = false)
    private Set<Block> extraNatureIDs;

    @Shadow(remap = false)
    private float maxPower;

    @Shadow(remap = false) @Final
    private static int SCAN_DISTANCE;

    @Shadow(remap = false)
    private boolean core;

    @Shadow(remap = false)
    public abstract void updateArtifacts();

    @Shadow(remap = false)
    protected abstract void updatePower(boolean throttle);

    @Shadow(remap = true)
    public abstract void update();

    @Shadow(remap = true)
    public abstract void invalidate();

    @Unique
    private static HashMap<Block, AltarApi.AltarPowerSource> witchery_Patcher$powerObjectTable = null;

    /** Triggers a TileEntity sync when power is consumed, as Witchery only updated it when the power increases.
     * This causes a desync in what appears in the Altar GUI and the actual power levels */
    @Inject(method = "consumePower", at = @At("RETURN"), remap = false)
    public void WPconsumePower(float power, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            BlockUtil.notifyBlockUpdate(this.world, this.getPos());
        }
    }



    /** This method fixes an inverse condition, as only "core" AltarTEs should update their power */
    @Inject(method = "updatePower()V", remap = false, cancellable = true, at = @At("HEAD"))
    public void updatePowerCheckValid(CallbackInfo ci) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            if (!this.core) return;
            PowerSources.instance().registerPowerSource(this);
            this.updateArtifacts();
            this.updatePower(true);
            ci.cancel();
        }
    }

    /** Rewrites the Power updating logic to factor in CraftTweaker integration and caching to improve performance by caching */
    @Inject(method = "updatePower(Z)V", remap = false, cancellable = true, at = @At("HEAD"))
    private void WPupdatePowerCached(boolean throttle, CallbackInfo ci) {
        if (BlockTweaks.altar_tweakCachePowerMap) {
            if (!this.world.isRemote && (!throttle || this.ticks - this.lastPowerUpdate <= 0L || this.ticks - this.lastPowerUpdate > 100L)) {
                this.lastPowerUpdate = this.ticks;
                witchery_Patcher$scanSurroundings();
            }
            ci.cancel();
        }
    }

    /** This Mixin sets the TileEntity as invalid if it is not a Core Altar  */
    @Inject(method = "readFromNBT", remap = true, at = @At("TAIL"))
    private void updateInvalidationOnReadFromNBT(NBTTagCompound nbtTag, CallbackInfo ci) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            if (!nbtTag.hasKey("Core")) return;
            boolean newValue = nbtTag.getBoolean("Core");
            if (this.core != newValue) {
                // Not Core, but packet sets this to Core: Update Power
                if (newValue) this.core = true;
                    // Core, but packet sets this to not Core: Invalidate
                else this.invalidate();
            }
        }
    }

    @Unique
    private void witchery_Patcher$scanSurroundings() {

        HashMap<IBlockState, Integer> sourceCount = new HashMap<>();

        for(int y = this.getPos().getY() - SCAN_DISTANCE; y <= this.getPos().getY() + SCAN_DISTANCE; ++y) {
            for(int z = this.getPos().getZ() + SCAN_DISTANCE; z >= this.getPos().getZ() - SCAN_DISTANCE; --z) {
                for(int x = this.getPos().getX() - SCAN_DISTANCE; x <= this.getPos().getX() + SCAN_DISTANCE; ++x) {

                    IBlockState state = this.world.getBlockState(new BlockPos(x, y, z));
                    AltarApi.AltarPowerSource source = AltarApi.getPowerSource(state);

                    if (source != null) {
                        sourceCount.merge(state, 1, (a, b) -> Math.min(a + b, source.getLimit()));
                    }

                }
            }
        }

        float newMax = 0.0F;

        for (IBlockState foundState : sourceCount.keySet()) {
            AltarApi.AltarPowerSource altarPowerSource = AltarApi.getPowerSource(foundState);
            newMax += Math.min(sourceCount.get(foundState), altarPowerSource.getLimit()) * altarPowerSource.getFactor();
        }

        if (newMax != this.maxPower) {
            this.maxPower = newMax;
            BlockUtil.notifyBlockUpdate(this.world, this.getPos());
        }
    }

}
