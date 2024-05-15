package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import kotlin.jvm.functions.Function0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockMandrakeCrop;
import net.msrandom.witchery.block.BlockWitchCrop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockMandrakeCrop.class)
public abstract class BlockMandrakeCropMixin extends BlockWitchCrop {

    private BlockMandrakeCropMixin(Function0<Item> crop) {
        super(crop);
    }

    @Inject(method = "harvestBlock", remap = false, cancellable = true,
            at = @At(value = "HEAD"))
    private void WPspawnMandrakeOnlyMature(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.mandrakeCrop_fixMandrakeSpawningNotMature) {
            if (!this.isMaxAge(state)) {
                super.harvestBlock(world, player, pos, state, te, stack);
                ci.cancel();
            }
        }
    }

}
