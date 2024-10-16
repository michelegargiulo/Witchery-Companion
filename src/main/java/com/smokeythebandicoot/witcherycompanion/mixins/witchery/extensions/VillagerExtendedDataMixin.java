package com.smokeythebandicoot.witcherycompanion.mixins.witchery.extensions;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.msrandom.witchery.extensions.VillagerExtendedData;
import net.msrandom.witchery.extensions.WitcheryExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix crash when villagers try to set property Occupied to a block of Air instead of a Bed
 */
@Mixin(VillagerExtendedData.class)
public abstract class VillagerExtendedDataMixin extends WitcheryExtendedData<EntityVillager> {

    @Shadow(remap = false)
    public abstract GlobalPos getHome();

    /** This mixin replaces the setBedOccupied */
    @Inject(method = "setBedOccupied", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkBothBedBlocks(boolean value, CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.CommonTweaks.villagerExtendedData_fixCrashOnSleeping) return;

        GlobalPos home = this.getHome();
        if (home == null) {
            ci.cancel();
            return;
        }

        IBlockState state = this.entity.world.getBlockState(home.getPos());
        if (WitcheryUtils.isOf(state, Blocks.BED)) {
            BlockPos pos = home.getPos().offset(state.getValue(BlockBed.FACING).getOpposite());

            // Additional check: also the other block of the BED must be a BED block, otherwise will
            // crash in some edge cases
            if (WitcheryUtils.isOf(this.entity.world.getBlockState(pos), Blocks.BED)) {
                this.entity.world.setBlockState(home.getPos(), state.withProperty(BlockBed.OCCUPIED, value), 4);
                this.entity.world.setBlockState(pos, this.entity.world.getBlockState(pos).withProperty(BlockBed.OCCUPIED, value), 4);
            }
        }

        ci.cancel();

    }

}
