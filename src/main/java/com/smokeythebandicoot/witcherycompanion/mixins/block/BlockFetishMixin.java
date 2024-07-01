package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockFetish;
import net.msrandom.witchery.block.entity.TileEntityFetish;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritEffect;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix Fetishes not dropping when broken
 */
@Mixin(BlockFetish.class)
public abstract class BlockFetishMixin extends BlockContainer {

    @Unique
    private TileEntityFetish witchery_Patcher$tileEntityPreserve = null;

    @Unique
    private ItemStack witchery_Patcher$dropType = null;

    private BlockFetishMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin preserves the TileEntity that would otherwise be set to null onBlockHarvested, causing drops to not
     be generated as they depend on the TileEntity's NBT data */
    @Inject(method = "onBlockHarvested", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/block/BlockContainer;onBlockHarvested(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/player/EntityPlayer;)V"))
    public void WPpreserveTE(World world, BlockPos pos, IBlockState state, EntityPlayer player, CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.BlockTweaks.blockFetish_fixNoDropsOnHarvest) return;
        witchery_Patcher$tileEntityPreserve = WitcheryTileEntities.FETISH.getAt(world, pos);
        witchery_Patcher$dropType = new ItemStack(Item.getItemFromBlock(world.getBlockState(pos).getBlock()));
    }

    /** This Mixin injects into the getDrops function and actually drops the items thanks to the preserved TileEntity */
    @Inject(method = "getDrops", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPrestoreTE(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.BlockTweaks.blockFetish_fixNoDropsOnHarvest) return;
        TileEntityFetish tile = witchery_Patcher$tileEntityPreserve;

        if (tile != null && witchery_Patcher$dropType != null) {

            // Retrieve tag and drop items
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("SpiritEffect", InfusedSpiritEffect.REGISTRY.getKey(tile.getEffect()).toString());
            witchery_Patcher$dropType.setTagCompound(tag);
            drops.add(witchery_Patcher$dropType);

            // Discard preserved values
            witchery_Patcher$tileEntityPreserve = null;
            witchery_Patcher$dropType = null;
        }

        ci.cancel();
    }

}
