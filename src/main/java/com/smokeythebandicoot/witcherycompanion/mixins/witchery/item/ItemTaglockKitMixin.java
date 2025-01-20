package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.DiviningUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.msrandom.witchery.item.BloodStorage;
import net.msrandom.witchery.item.ItemTaglockKit;
import net.msrandom.witchery.network.WitcheryNetworkPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Crystal Ball Rework
 * [Tweak] Tweak Max Use Duration
 */
@Mixin(ItemTaglockKit.class)
public abstract class ItemTaglockKitMixin extends Item implements BloodStorage {

    @Unique
    private EntityLivingBase witchery_Patcher$entityToSpectate = null;

    @Inject(method = "getMaxItemUseDuration", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakMaxUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ItemTweaks.taglockKit_tweakMaxItemUseDuration);
    }


    @WrapOperation(method = "onUsingTick", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/item/ItemTaglockKit;getBoundEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/entity/EntityLivingBase;"))
    private EntityLivingBase captureEntityToSpectate(World entity, ItemStack worldServer, int server, Operation<EntityLivingBase> original) {
        witchery_Patcher$entityToSpectate = original.call(entity, worldServer, server);
        return witchery_Patcher$entityToSpectate;
    }


    @WrapOperation(method = "onUsingTick", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/msrandom/witchery/network/WitcheryNetworkChannel;sendTo(Lnet/msrandom/witchery/network/WitcheryNetworkPacket;Lnet/minecraft/entity/player/EntityPlayerMP;)V"))
    private void spectateInsteadOfRedirectingCamera(WitcheryNetworkPacket packet, EntityPlayerMP player, Operation<Void> original) {

        if (BlockTweaks.crystalBall_tweakSpectatorRework && witchery_Patcher$entityToSpectate != null) {

            if (!DiviningUtils.isDivining(player)) {
                DiviningUtils.startDivination(player, witchery_Patcher$entityToSpectate);
            }
            // Player is already divining: check if entity is still alive, otherwise terminate divination
            else {
                Entity divinedEntity = DiviningUtils.getDivinedEntity(player);
                if (divinedEntity == null || divinedEntity.isDead) {
                    DiviningUtils.terminateDivination(player);
                }
            }

            // TODO:
            // Save extended data:
            // Is Crystal Ball spectating?
            // Spectating entity UID
            // Create sleeping body
            // Check if sleeping body is alive, if dead return
            // If entity is null (killed or left the game) return
            // On world load, if target entity is null, return
            // On return, kill sleeping body

            //player.setGameType(GameType.SPECTATOR);
            //player.setSpectatingEntity(witchery_Patcher$entityToSpectate);

        }
    }

    @Inject(method = "onItemUseFinish", remap = false, cancellable = true, at = @At("HEAD"))
    private void stopSpectatingOnItemUseFinish(ItemStack stack, World world, EntityLivingBase player, CallbackInfoReturnable<ItemStack> cir) {
        if (!BlockTweaks.crystalBall_tweakSpectatorRework) {
            return;
        }

        if (player instanceof EntityPlayerMP) {
            DiviningUtils.terminateDivination((EntityPlayerMP)player);
        }
        cir.setReturnValue(stack);
    }

    @Inject(method = "onPlayerStoppedUsing", remap = false, cancellable = true, at = @At("HEAD"))
    private void stopSpectatingOnStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft, CallbackInfo ci) {
        if (!BlockTweaks.crystalBall_tweakSpectatorRework) {
            return;
        }

        if (player instanceof EntityPlayerMP) {
            DiviningUtils.terminateDivination((EntityPlayerMP)player);
        }
        ci.cancel();
    }

}
