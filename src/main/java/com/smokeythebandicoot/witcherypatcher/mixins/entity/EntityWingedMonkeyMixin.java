package com.smokeythebandicoot.witcherypatcher.mixins.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.EntityWingedMonkey;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.item.ItemTaglockKit;
import net.msrandom.witchery.util.TeleportationUtil;
import net.msrandom.witchery.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWingedMonkey.class)
public abstract class EntityWingedMonkeyMixin extends EntityFlyingTameable {

    @Shadow(remap = false)
    public abstract void setFeatherColor(int color);

    @Shadow(remap = false)
    public abstract void setTameSkin(int par1);

    private EntityWingedMonkeyMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "processInteract", remap = false,
        at = @At("HEAD"), cancellable = true)
    private void WPprocessInteract(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (this.isTamed()) {
            if (this.isOwner(player) && !this.isBreedingItem(itemstack)) {
                if (!itemstack.isEmpty() && itemstack.getItem() == Items.DYE) {
                    if (!this.world.isRemote) {
                        int i = EnumDyeColor.byDyeDamage(itemstack.getItemDamage()).getMetadata();
                        this.setFeatherColor(i);
                        if (!player.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }
                    }
                } else {
                    if (!itemstack.isEmpty() && (itemstack.getItem() == Items.NAME_TAG || itemstack.getItem() == WitcheryGeneralItems.POLYNESIA_CHARM || itemstack.getItem() == WitcheryGeneralItems.DEVILS_TONGUE_CHARM)) {
                        cir.setReturnValue(false);
                        return;
                    }

                    if (itemstack.getItem() != WitcheryIngredientItems.PLAYER_BOUND_WAYSTONE && itemstack.getItem() != WitcheryIngredientItems.BOUND_WAYSTONE) {
                        if (!itemstack.isEmpty() && ItemTaglockKit.isTaglockPresent(itemstack, 0)) {
                            this.waypoint = itemstack.copy();
                            this.homeX = this.posX;
                            this.homeY = this.posY;
                            this.homeZ = this.posZ;
                            this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
                            this.world.setEntityState(this, (byte)6);
                            if (!player.capabilities.isCreativeMode) {
                                itemstack.shrink(1);
                            }
                        } else if (this.getDistanceSq(player) < 9.0 && !this.world.isRemote) {
                            if (!this.isSitting()) {
                                this.getNavigator().clearPath();
                            }

                            this.setSitting(!this.isSitting());
                            this.aiSit.setSitting(this.isSitting());
                        }
                    } else {
                        this.waypoint = itemstack.copy();
                        this.homeX = 0.0;
                        this.homeY = 0.0;
                        this.homeZ = 0.0;
                        Waypoint wp = this.getWaypoint();
                        this.homeX = wp.x;
                        this.homeY = wp.y;
                        this.homeZ = wp.z;
                        TeleportationUtil.bindToLocation(this.getPosition(), this.world.provider.getDimension(), this.waypoint);
                        if (!this.world.isRemote && this.isSitting()) {
                            this.aiSit.setSitting(false);
                        }

                        player.startRiding(this);
                        this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
                        this.world.setEntityState(this, (byte)6);
                    }
                }

                cir.setReturnValue(true);

            } else if (this.isOwner(player) && this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                if (!this.world.isRemote) {
                    this.heal(10.0F);
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(super.processInteract(player, hand));
            }
        } else if (!itemstack.isEmpty() && (itemstack.getItem() == Items.PORKCHOP || itemstack.getItem() == Items.BEEF) && player.getDistanceSq(this) < 9.0) {
            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            if (!this.world.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTameSkin(1 + this.world.rand.nextInt(3));
                    this.setTamedBy(player);
                    this.enablePersistence();
                    this.playTameEffect(true);
                    this.getNavigator().clearPath();
                    this.setSitting(true);
                    this.world.setEntityState(this, (byte)7);
                } else {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(!this.isBreedingItem(itemstack) && super.processInteract(player, hand));
        }
    }
}
