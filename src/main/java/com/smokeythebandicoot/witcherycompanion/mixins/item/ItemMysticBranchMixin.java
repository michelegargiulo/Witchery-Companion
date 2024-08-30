package com.smokeythebandicoot.witcherycompanion.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.SymbolEffectProcessor;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.item.ItemMysticBranch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

/**
 * Mixins:
 * [Feature] unlock secret SymbolEffects
 */
@Mixin(ItemMysticBranch.class)
public abstract class ItemMysticBranchMixin extends Item {

    @WrapOperation(method = "onPlayerStoppedUsing", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/infusion/symbol/SymbolEffect;perform(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;I)V"))
    private void unlockSecret(SymbolEffect instance, World world, EntityPlayer entityPlayer, int i, Operation<Void> original) {
        // At this point in the function, the effect and the player have already passed null-checks
        SymbolEffectProcessor.SymbolEffectInfo info = new SymbolEffectProcessor.SymbolEffectInfo(instance);
        IWitcheryProgress progress = entityPlayer.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);

        // Retrieve progress
        if (progress == null) {
            WitcheryCompanion.logger.warn("Could not unlock secret SymbolEffect: progress is null");
            return;
        }

        // Unlock progress
        progress.unlockProgress(ProgressUtils.getSymbolEffectSecret(info.effectId));
        ProgressSync.serverRequest(entityPlayer);
    }

}
