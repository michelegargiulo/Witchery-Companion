package com.smokeythebandicoot.witcherypatcher.mixins.entity.passive;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Fix Owl not sitting
 * [Tweak] Disable giving items to the Owl (bugged in Witchery)
 */
@Mixin(value = EntityOwl.class, remap = false)
public abstract class EntityOwlMixin extends EntityFlyingTameable {

    @Shadow
    protected abstract void setVariant(int i);

    @Shadow
    public abstract boolean isBreedingItem(ItemStack itemstack);

    private EntityOwlMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "processInteract", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAISit;setSitting(Z)V"))
    public void WPfixSitting(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.owl_fixSitting) {
            this.setVariant(this.isSitting() ? 1 : 0);
            this.setSitting(!this.isSitting());
        }
    }


    @WrapOperation(method = "processInteract", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHeldItem(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack WPsimulateEmptyHand(EntityPlayer instance, EnumHand enumHand, Operation<ItemStack> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.owl_tweakDisableTakeItems) {

            // Get the original stack. If not special item, return empty stack to simulate right-click
            // with an empty hand, so that nothing happens
            ItemStack stack = original.call(instance, enumHand);
            if ( // Item is not breeding item, nor name tag, nor one of the charms
                    !isBreedingItem(stack) &&
                    stack.getItem() != Items.NAME_TAG &&
                    stack.getItem() != WitcheryGeneralItems.POLYNESIA_CHARM &&
                    stack.getItem() != WitcheryGeneralItems.DEVILS_TONGUE_CHARM) {
                return ItemStack.EMPTY;
            } else {
                return stack;
            }
        }
        // Call original, do not modify the behaviour
        return original.call(instance, enumHand);
    }
}
