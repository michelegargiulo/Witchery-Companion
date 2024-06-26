package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.msrandom.witchery.block.entity.TileEntityFetish;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Fetish Tile Entities losing most of their NBT data on world reload
 */
@Mixin(TileEntityFetish.class)
public abstract class TileEntityFetishMixin extends WitcheryTileEntity {

    @Shadow
    public abstract NBTTagCompound writeToNBT(NBTTagCompound nbtRoot);

    /** This method injects into the getUpdatePacket method to make use of getUpdateTag() method.
     * Even if not required, it is better to avoid code duplication */
    @Inject(method = "getUpdatePacket", remap = true, cancellable = true, at = @At("HEAD"))
    public void getUpdatePacket(CallbackInfoReturnable<SPacketUpdateTileEntity> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.blockFetish_fixMissingDataOnWorldReload) {
            NBTTagCompound nbtTag = getUpdateTag();
            cir.setReturnValue(new SPacketUpdateTileEntity(this.getPos(), 1, nbtTag));
        }
    }

    /** Injected method that is missing from the TileEntity class. This is responsible for sending
     * TileEntity data to clients on initial chunk load or when many blocks update at once (see javadocs for
     * getUpdateTag() of TileEntity class. Since it is missing, super() is called and returns an empty NBT,
     * get decorated with default Forge-added data (see SPacketChunkData constructor) and sent to clients */
    public NBTTagCompound getUpdateTag() {
        if (ModConfig.PatchesConfiguration.BlockTweaks.blockFetish_fixMissingDataOnWorldReload) {
            NBTTagCompound nbtTag = new NBTTagCompound();
            this.writeToNBT(nbtTag);
            return nbtTag;
        }
        return super.getUpdateTag();
    }

    /** Fixes wrong feathing of NBT due to wrong tag type. Correct type is 8. Witchery uses 10 */
    @WrapOperation(method = "readSubDataFromNBT", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/nbt/NBTTagCompound;getTagList(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;"))
    public NBTTagList fixReadSubdataFromNBT(NBTTagCompound instance, String nbtKey, int nbtType, Operation<NBTTagList> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.blockFetish_fixMissingDataOnWorldReload) {
            return original.call(instance, nbtKey, 8);
        }
        return original.call(instance, nbtKey, 10);
    }

}
