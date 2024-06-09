package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.block.entity.TileEntityPoppetShelf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPoppetShelf.class)
public abstract class TileEntityPoppetShelfMixin {

    @Inject(method = "forceChunk", remap = false, at = @At(value = "HEAD"), cancellable = true)
    private void cancelForceChunk(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.poppetShelf_tweakDisableChunkloading) {
            ci.cancel();
        }
    }

}
