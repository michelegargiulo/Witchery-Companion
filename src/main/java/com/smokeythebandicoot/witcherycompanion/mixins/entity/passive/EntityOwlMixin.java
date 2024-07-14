package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Mixins:
 [Bugfix] Fix Owl not sitting
 [Bugfix] Fix Familiar having null owner after world reload
 [Tweak] Disable giving items to the Owl (bugged in Witchery)
 */
@Mixin(value = EntityOwl.class)
public abstract class EntityOwlMixin extends EntityFlyingTameable implements Familiar<EntityOwl> {

    @Shadow(remap = false)
    protected abstract void setVariant(int i);

    @Shadow(remap = true)
    public abstract boolean isBreedingItem(ItemStack itemstack);

    private EntityOwlMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "processInteract", remap = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAISit;setSitting(Z)V", remap = true))
    public void WPfixSitting(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.owl_fixSitting) {
            this.setVariant(this.isSitting() ? 1 : 0);
            this.setSitting(!this.isSitting());
        }
    }


    @WrapOperation(method = "processInteract", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHeldItem(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;", remap = true))
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

    @Inject(method = "getOwner()Lnet/minecraft/entity/Entity;", remap = true, cancellable = true, at = @At("HEAD"))
    public void getOwnerEntity(CallbackInfoReturnable<EntityLivingBase> cir) {
        UUID id = this.getOwnerId();
        if (id == null) {
            cir.setReturnValue(null);
            return;
        }
        cir.setReturnValue(this.world.getPlayerEntityByUUID(id));
    }

    @Inject(method = "getOwner()Lnet/minecraft/entity/EntityLivingBase;", remap = true, cancellable = true, at = @At("HEAD"))
    public void getOwnerEntityLivingBase(CallbackInfoReturnable<EntityLivingBase> cir) {
        UUID id = this.getOwnerId();
        if (id == null) {
            cir.setReturnValue(null);
            return;
        }
        cir.setReturnValue(this.world.getPlayerEntityByUUID(id));
    }


}
