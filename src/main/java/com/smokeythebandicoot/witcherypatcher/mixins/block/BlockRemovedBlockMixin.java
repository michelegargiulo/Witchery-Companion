package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockRemovedBlock;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 [Bugfix] Brew of Tidal Hold suffocates entities that pass inside of the hole
 */
@Mixin(value = BlockRemovedBlock.class, remap = false)
public class BlockRemovedBlockMixin extends BlockContainer {

    @Unique
    private static Field witchery_Patcher$materialField = null;
    private static boolean witcheryPatcher$materialFieldPatchSuccess = true;
    private static Material targetMaterial = Material.ANVIL;

    private BlockRemovedBlockMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Inject(method = "<init>", at=@At("TAIL"), remap = false)
    private void WPinit(CallbackInfo ci) {
        if (witcheryPatcher$materialFieldPatchSuccess && witchery_Patcher$materialField == null) {
            try {
                Field f = Block.class.getDeclaredField("material");
                f.setAccessible(true);
                witchery_Patcher$materialField = f;
                witchery_Patcher$materialField.set(this, targetMaterial);
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                WitcheryPatcher.logger.warn("Error while patching BrewOfTidalHold: " + ex.toString());
                witcheryPatcher$materialFieldPatchSuccess = false;
            }
        } else if (witcheryPatcher$materialFieldPatchSuccess) {
            try {
                witchery_Patcher$materialField.set(this, targetMaterial);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //@ModifyVariable(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/block/material/Material;GLASS:Lnet/minecraft/block/material/Material;"))

    /*@WrapOperation(
            method = "<init>",
            at=@At(value = "FIELD",
                target = "Lnet/minecraft/block/material/Material;GLASS:Lnet/minecraft/block/material/Material;"
            ), remap = true)
    private static Material WPinit() {
        return Material.AIR;
    }*/

    /*@ModifyVariable(method = "<init>", at = @At("HEAD"))
    private Material WPinit() {
        return Material.AIR;
    }*/

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return WitcheryTileEntities.REMOVED.create();
    }


}
