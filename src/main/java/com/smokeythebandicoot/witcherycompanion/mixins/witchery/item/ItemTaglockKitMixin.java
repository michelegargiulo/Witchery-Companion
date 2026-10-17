package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import com.smokeythebandicoot.witcherycompanion.patches.crystalball.DivinationManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.item.BloodStorage;
import net.msrandom.witchery.item.ItemTaglockKit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Tweak Max Use Duration
 * [Tweak] Spectator Divination: Crystal Ball divination puts the player in Spectator mode instead of moving the camera
 */
@Mixin(ItemTaglockKit.class)
public abstract class ItemTaglockKitMixin extends Item implements BloodStorage {

    @Inject(method = "getMaxItemUseDuration", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakMaxUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ItemTweaks.tweakMaxItemUseDuration);
    }

    /**
     * Spectator Divination: on the first tick of the use, start the divination. While a divination is running,
     * the original logic (which sends the client-side camera packets) is skipped.
     */
    @Inject(method = "onUsingTick", remap = false, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$startSpectatorDivination(ItemStack stack, EntityLivingBase user, int count, CallbackInfo ci) {
        if (!BlockTweaks.crystalBall_tweakSpectatorDivination || !(user instanceof EntityPlayerMP)) return;
        int elapsed = this.getMaxItemUseDuration(stack) - count;
        if (DivinationManager.onUsingTick((EntityPlayerMP) user, stack, elapsed)) ci.cancel();
    }

    /** Spectator Divination: releasing the kit ends the divination. Not gated by config, no-op without a divination */
    @Inject(method = "onPlayerStoppedUsing", remap = false, at = @At("HEAD"))
    private void witcherycompanion$endSpectatorDivination(ItemStack stack, World world, EntityLivingBase entity, int timeLeft, CallbackInfo ci) {
        if (entity instanceof EntityPlayerMP) DivinationManager.onUseEnded((EntityPlayerMP) entity);
    }

    /** Spectator Divination: reaching the max use duration ends the divination */
    @Inject(method = "onItemUseFinish", remap = false, at = @At("HEAD"))
    private void witcherycompanion$finishSpectatorDivination(ItemStack stack, World world, EntityLivingBase entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof EntityPlayerMP) DivinationManager.onUseEnded((EntityPlayerMP) entity);
    }

    /**
     * Spectator Divination: the Crystal Ball cooldown is checked before the Altar power is consumed (so no power is
     * wasted), and starts when power is consumed
     */
    @WrapOperation(method = "onItemUseFirst", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lnet/msrandom/witchery/block/BlockCrystalBall;tryConsumePower(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean witcherycompanion$divinationCooldown(World world, EntityPlayer player, BlockPos pos, Operation<Boolean> original) {
        if (!BlockTweaks.crystalBall_tweakSpectatorDivination || world.isRemote) {
            return original.call(world, player, pos);
        }
        if (!DivinationManager.beginDivinationAttempt(world, player, pos)) return false;
        boolean consumed = original.call(world, player, pos);
        if (consumed) DivinationManager.markCrystalBallUsed(world, pos);
        return consumed;
    }

}
