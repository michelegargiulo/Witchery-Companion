package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.AltarApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar.ITileEntityAltarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.msrandom.witchery.block.BlockPlacedItem;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.block.entity.TileEntityPlacedItem;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.WitcheryTileEntities;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.util.BlockUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/**
 Mixins:
 [Bugfix] Fixes PowerSources being lost upon world load/reload, requiring them to be interacted or
    broken and re-placed to be registered correctly again
 [Tweak] Great performance gain by caching Altar PowerSourceTable
 [Integration] Thaumcraft candles are better than Witchery Candelabra
 */
@Mixin(value = TileEntityAltar.class)
public abstract class TileEntityAltarMixin extends WitcheryTileEntity implements IPowerSource, ITileEntityAltarAccessor {

    @Shadow(remap = false)
    private long lastPowerUpdate;

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

    @Shadow(remap = false)
    private int rechargeScale;

    @Shadow(remap = false)
    private int powerScale;

    @Shadow(remap = false)
    private int rangeScale;

    @Shadow(remap = false)
    private int enhancementLevel;

    @Shadow(remap = false)
    private float power;

    @Shadow(remap = false)
    public abstract void updatePower();


    /** ========== POWER SOURCE PERSISTENCY ========== **/

    /** Triggers a TileEntity sync when power is consumed, as Witchery only updated it when the power increases.
     * This causes a desync in what appears in the Altar GUI and the actual power levels */
    @Inject(method = "consumePower", at = @At("RETURN"), remap = false)
    private void WPconsumePower(float power, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            BlockUtil.notifyBlockUpdate(this.world, this.getPos());
        }
    }

    /** This method fixes an inverse condition, as only "core" AltarTEs should update their power */
    @Inject(method = "updatePower()V", remap = false, cancellable = true, at = @At("HEAD"))
    private void updatePowerCheckValid(CallbackInfo ci) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            if (!this.core) return;
            PowerSources.instance().registerPowerSource(this);
            this.updateArtifacts();
            this.updatePower(true);
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

    @Override
    public void witcherycompanion$accessor$setCore(boolean isCore) {
        this.core = isCore;
    }

    @Override
    public int witcherycompanion$accessor$getEnhancementLevel() {
        return this.enhancementLevel;
    }


    /** ========== ALTAR POWER SOURCES ========== **/

    /** This Mixin forces the Altar send block updates to notify things like TOP **/
    @Inject(method = "update", remap = true, at = @At("HEAD"), cancellable = true)
    private void updateSurroundings(CallbackInfo ci) {
        super.update();
        if (!this.world.isRemote) {
            float maxPowerScaled = this.maxPower * (float) this.powerScale;
            if (this.core && this.ticks % 20 == 0L) {
                updatePower();
                if (this.power < maxPowerScaled) {
                    this.power = (float) ((int) Math.min(this.power + 10.0f * (float) this.rechargeScale, maxPowerScaled));
                } else if (this.power > maxPowerScaled) {
                    this.power = maxPowerScaled;
                }
                BlockUtil.notifyBlockUpdate(this.world, this.getPos());
            }
        }
        ci.cancel();

    }

    /** Rewrites the Power updating logic to factor in CraftTweaker integration and caching to improve performance by caching */
    @Inject(method = "updatePower(Z)V", remap = false, cancellable = true, at = @At("HEAD"))
    private void WPupdatePowerCached(boolean throttle, CallbackInfo ci) {
        if (BlockTweaks.altar_tweakEnableCrafttweakerCompat) {
            if (!this.world.isRemote && (!throttle || this.ticks - this.lastPowerUpdate <= 0L || this.ticks - this.lastPowerUpdate > 75L)) {
                this.lastPowerUpdate = this.ticks;
                witcherycompanion$scanSurroundings();
            }
            ci.cancel();
        }
    }

    @Unique
    private void witcherycompanion$scanSurroundings() {

        HashMap<IBlockState, Integer> sourceCount = new HashMap<>();

        for(int y = this.getPos().getY() - SCAN_DISTANCE; y <= this.getPos().getY() + SCAN_DISTANCE; ++y) {
            for(int z = this.getPos().getZ() + SCAN_DISTANCE; z >= this.getPos().getZ() - SCAN_DISTANCE; --z) {
                for(int x = this.getPos().getX() - SCAN_DISTANCE; x <= this.getPos().getX() + SCAN_DISTANCE; ++x) {

                    IBlockState state = this.world.getBlockState(new BlockPos(x, y, z));
                    AltarApi.AltarPowerSource source = AltarApi.getPowerSource(state);

                    if (source != null) {
                        sourceCount.merge(state, 1, (a, b) -> Math.min(a + b, source.getLimit(this.enhancementLevel)));
                    }

                }
            }
        }

        float newMax = 0.0F;

        for (IBlockState foundState : sourceCount.keySet()) {
            AltarApi.AltarPowerSource altarPowerSource = AltarApi.getPowerSource(foundState);
            newMax += Math.min(sourceCount.get(foundState), altarPowerSource.getLimit(this.enhancementLevel)) * altarPowerSource.getFactor();
        }

        if (newMax != this.maxPower) {
            this.maxPower = newMax;
            BlockUtil.notifyBlockUpdate(this.world, this.getPos());
        }
    }


    /** ========== ALTAR BOOSTERS ========== **/

    //TODO: Migrate entirely towards API, including Placed Items
    /** This Mixin injects the Altar Boosters Api inside Artifacts logic **/
    @Inject(method = "updateArtifacts", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectNewArtifacts(CallbackInfo ci) {

        if (!BlockTweaks.altar_tweakEnableCrafttweakerCompat) {
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> toVisit = new ArrayList<>();
        toVisit.add(getPos());

        AltarApi.AltarBoosterFunc skullBooster = null;
        AltarApi.AltarBoosterFunc candleBooster = null;
        AltarApi.AltarBoosterFunc chaliceBooster = null;

        BlockPos bestSkullPos = null;
        BlockPos bestCandlePos = null;
        BlockPos bestChalicePos = null;

        boolean knifeFound = false;
        boolean wandFound = false;
        boolean pentacleFound = false;
        boolean infinityFound = false;

        AltarApi.AltarBoosterInfo info = new AltarApi.AltarBoosterInfo();

        // Iterate over blocks to visit
        while (!toVisit.isEmpty()) {
            BlockPos coord = toVisit.get(0);
            toVisit.remove(0);
            for (BlockPos newCoord : new BlockPos[] {coord.north(), coord.south(), coord.east(), coord.west()}) {
                if (this.world.getBlockState(newCoord).getBlock() == WitcheryBlocks.ALTAR && !visited.contains(newCoord) && !toVisit.contains(newCoord)) {
                    toVisit.add(newCoord);
                }
            }
            visited.add(coord);

            // Retrieve information
            BlockPos offset = coord.up();
            IBlockState state = this.world.getBlockState(offset);
            Block block = state.getBlock();

            // Inject API
            // SKULL
            if (AltarApi.isAltarBooster(state, AltarApi.EAltarBoosterType.SKULL)) {
                AltarApi.AltarBoosterFunc booster = AltarApi.getBooster(state, AltarApi.EAltarBoosterType.SKULL);
                if (skullBooster == null || booster.priority > skullBooster.priority) {
                    skullBooster = booster;
                    bestSkullPos = offset;
                }
            }

            // CANDELABRA
            else if (AltarApi.isAltarBooster(state, AltarApi.EAltarBoosterType.CANDLE)) {
                AltarApi.AltarBoosterFunc booster = AltarApi.getBooster(state, AltarApi.EAltarBoosterType.CANDLE);
                if (candleBooster == null || booster.priority > candleBooster.priority) {
                    candleBooster = booster;
                    bestCandlePos = offset;
                }
            }

            // PLACED ITEMS
            else if (block instanceof BlockPlacedItem) {
                TileEntityPlacedItem item = WitcheryTileEntities.PLACED_ITEM.getAt(world, offset);
                if (item != null) {
                    ItemStack placedStack = item.getStack();
                    if (!knifeFound && placedStack.getItem() == WitcheryGeneralItems.getArthana()) {
                        knifeFound = true;
                        ++info.newRangeScale;
                    } else if (!wandFound && placedStack.getItem() == WitcheryGeneralItems.MYSTIC_BRANCH) {
                        wandFound = true;
                        ++info.newEnhancementLevel;
                    } else {
                        if (pentacleFound || placedStack.getItem() != WitcheryIngredientItems.KOBOLDITE_PENTACLE) {
                            continue;
                        }
                        pentacleFound = true;
                    }
                }
            }

            // CHALICE
            else if (AltarApi.isAltarBooster(state, AltarApi.EAltarBoosterType.CHALICE)) {
                AltarApi.AltarBoosterFunc booster = AltarApi.getBooster(state, AltarApi.EAltarBoosterType.CHALICE);
                if (chaliceBooster == null || booster.priority > chaliceBooster.priority) {
                    chaliceBooster = booster;
                    bestChalicePos = offset;
                }
            }

            // INFINITY EGG
            else if (!infinityFound && block == WitcheryBlocks.INFINITY_EGG) {
                infinityFound = true;
            }
        }

        // Apply boosters
        witcherycompanion$applyBooster(skullBooster, bestSkullPos, info);
        witcherycompanion$applyBooster(candleBooster, bestCandlePos, info);
        witcherycompanion$applyBooster(chaliceBooster, bestChalicePos, info);


        // Pentacle (applied after everything except Infinity)
        if (pentacleFound) {
            info.newRechargeScale *= 2;
        }

        // Infinity Egg (applied after everything)
        if (infinityFound) {
            info.newRechargeScale *= 10;
            info.newPowerScale *= 10;
        }

        if ( // Only send Block Update if something has changed
                info.newRechargeScale != this.rechargeScale ||
                info.newPowerScale != this.powerScale ||
                info.newRangeScale != this.rangeScale ||
                info.newEnhancementLevel != this.enhancementLevel
        ) {
            this.rechargeScale = info.newRechargeScale;
            this.powerScale = info.newPowerScale;
            this.rangeScale = info.newRangeScale;
            this.enhancementLevel = info.newEnhancementLevel;
            if (!this.world.isRemote) {
                BlockUtil.notifyBlockUpdate(world, getPos());
            }
        }

        ci.cancel();

    }

    @Unique
    private void witcherycompanion$applyBooster(AltarApi.AltarBoosterFunc booster, BlockPos pos, AltarApi.AltarBoosterInfo info) {
        if (pos != null && booster != null) {
            IBlockState state = this.world.getBlockState(pos);
            TileEntity tile = this.world.getTileEntity(pos);
            booster.consumer.apply(state, tile, info);
        }
    }

}
