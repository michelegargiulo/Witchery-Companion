package com.smokeythebandicoot.witcherypatcher.mixins.block.entity;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockCoffin;
import net.msrandom.witchery.block.entity.TileEntityCoffin;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix edge case where the coffin might crash the game when placed
 */
@Mixin(value = TileEntityCoffin.class)
public abstract class TileEntityCoffinMixin extends TileEntity {

    @Shadow(remap = false)
    private EnumDyeColor color;

    @Inject(method = "getColor", remap = false, at = @At("HEAD"), cancellable = true)
    public final void getColor(CallbackInfoReturnable<EnumDyeColor> cir) {

        if (ModConfig.PatchesConfiguration.BlockTweaks.coffin_fixEdgeCrash) {
            if (this.hasWorld()) {
                if (this.blockType == null)
                    cir.setReturnValue(EnumDyeColor.BLACK);
                else
                    cir.setReturnValue(((BlockCoffin) this.blockType).getColor());
            } else cir.setReturnValue(this.color);
        }

    }

    @Inject(method = "update", remap = true, cancellable = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/tileentity/TileEntityType;getAt(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;"))
    public void WPfixPistonCrash(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.coffin_fixPistonMoveCrash) {
            BlockPos otherPieceLocation = this.pos.offset(EnumFacing.byHorizontalIndex(this.getBlockMetadata() & 3));
            if (!(WitcheryTileEntities.COFFIN.getAt(this.world, otherPieceLocation) instanceof TileEntityCoffin)) {
                ci.cancel();
            }
        }
    }
}

