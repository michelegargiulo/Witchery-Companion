package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.msrandom.witchery.block.entity.TileEntityBeartrap;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] When beartrap (wolftrap) summons a Werewolf, it sends a message to warn the player
 */
@Mixin(TileEntityBeartrap.class)
public abstract class TileEntityBeartrapMixin extends WitcheryTileEntity {

    @Inject(method = "update", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/WitcheryUtils;summonEntity(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityType;Lnet/minecraft/util/math/BlockPos;IILkotlin/jvm/functions/Function1;)Lnet/minecraft/entity/Entity;"))
    private void sendMessageForWolfSpawn(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.wolfTrap_warnPlayers) {
            BlockPos pos = this.getPos();
            for (EntityPlayer player : this.world.getPlayers(EntityPlayer.class, player -> player.getDistanceSq(pos) < 256.0)) {
                player.sendMessage(new TextComponentTranslation("witcherycompanion.message.wolftrap.approaching"));
            }
        }
    }

}
