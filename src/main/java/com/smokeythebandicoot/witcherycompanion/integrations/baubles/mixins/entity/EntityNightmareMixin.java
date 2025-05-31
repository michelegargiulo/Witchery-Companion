package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.entity;

import baubles.api.BaublesApi;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityNightmare;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin:
 * [Integration] Checks for Fanciful Thinking Charm when attacking
 */
@Mixin(EntityNightmare.class)
public abstract class EntityNightmareMixin extends EntityMob {


    @Unique
    private EntityPlayer witchery_Patcher$player = null;


    public EntityNightmareMixin(World worldIn) {
        super(worldIn);
    }

    /** Since in the WrapOperation we do not have access to the player, we retrieve it here for later use **/
    @Inject(method = "attackEntityAsMob", remap = false, at = @At("HEAD"))
    private void catchPlayer(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof EntityPlayer) {
            this.witchery_Patcher$player = (EntityPlayer) entity;
        }
    }

    /** When the Nightmare is attacking a player, check if they have a Fanciful Thinking Charm equipped in Bauble. If
     * they have, do as if the returned Item was the FTC, otherwise the method proceeds as normal (checks inventory) **/
    @WrapOperation(method = "attackEntityAsMob", remap = false, at = @At(value = "INVOKE", remap = true, ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item checkFancifulBauble(ItemStack instance, Operation<Item> original) {
        if (witchery_Patcher$player != null) {
            if (BaublesApi.isBaubleEquipped(witchery_Patcher$player, WitcheryIngredientItems.DISRUPTED_DREAMS_CHARM) > -1) {
                return original.call(new ItemStack(WitcheryIngredientItems.DISRUPTED_DREAMS_CHARM));
            }
            witchery_Patcher$player = null;
        }
        return original.call(instance);
    }
}
