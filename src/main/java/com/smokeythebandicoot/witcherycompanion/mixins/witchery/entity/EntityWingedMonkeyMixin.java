package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.LootTweaks;
import com.smokeythebandicoot.witcherycompanion.patches.entity.wingedmonkey.ai.EntityAIFlyerAttackOnCollide;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.EntityWingedMonkey;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.item.BloodStorage;
import net.msrandom.witchery.item.ItemTaglockKit;
import net.msrandom.witchery.util.TeleportationUtil;
import net.msrandom.witchery.util.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

/**
 * Mixins:
 * [Tweak] Introduce own loot table
 * [Bugfix] Fix Crash when right-clicking with most items
 * [Bugfix] Fix sitting behavior not working
 * [Bugfix] Fix Not attacking owner's target
 * [Bugfix] Fix Carried entities y-offset
 */
@Mixin(EntityWingedMonkey.class)
public abstract class EntityWingedMonkeyMixin extends EntityFlyingTameable {

    @Shadow(remap = false)
    public abstract void setFeatherColor(int color);

    @Shadow(remap = false)
    public abstract void setTameSkin(int color);

    private EntityWingedMonkeyMixin(World world) {
        super(world);
    }


    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    protected void disableDropFewItems(boolean param, int fortune, CallbackInfo ci) {
        if (LootTweaks.wingedMonkey_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return LootTweaks.wingedMonkey_tweakLootTable ? LootTables.WINGED_MONKEY : null;
    }

    /** Cancel the original processInteract function in favor of the new one from Companion **/
    @Inject(method = "processInteract", remap = true, at = @At("HEAD"), cancellable = true)
    private void fixProcessInteract(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (EntityTweaks.wingedMonkey_fixAI) {
            cir.setReturnValue(witcherycompanion$fixedProcessInteract(player, hand));
        }
    }

    /** The new processInteract function. Internal logic is changed w.r.t. the original one, and calls
     * to this.aiSit.setSitting are replaced with fixSetSitting() call. Also fixes a ItemStack != null
     * with proper !ItemStack.isEmpty **/
    @Unique
    private boolean witcherycompanion$fixedProcessInteract(EntityPlayer player, EnumHand hand) {;
        ItemStack itemstack = player.inventory.getCurrentItem();
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
                        return false;
                    }
                    // The logic of this if is changed
                    if (itemstack.getItem() == WitcheryIngredientItems.PLAYER_BOUND_WAYSTONE || itemstack.getItem() == WitcheryIngredientItems.BOUND_WAYSTONE) {
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
                            this.witcherycompanion$fixSetSitting(false);
                        }
                        player.startRiding(this);
                        this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
                        this.world.setEntityState(this, (byte)6);
                    }
                    else if (itemstack.getItem() instanceof BloodStorage && ItemTaglockKit.isTaglockPresent(itemstack, 0)) {
                        this.waypoint = itemstack.copy();
                        this.homeX = this.posX;
                        this.homeY = this.posY;
                        this.homeZ = this.posZ;
                        this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
                        this.world.setEntityState(this, (byte)6);
                        if (!player.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }
                    }
                    else if (this.getDistanceSq(player) < 9.0 && !this.world.isRemote) {
                        if (!this.isSitting()) {
                            this.getNavigator().clearPath();
                        }

                        this.witcherycompanion$fixSetSitting(!this.isSitting());
                    }
                }
                return true;
            }
            else if (this.isOwner(player) && this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                if (!this.world.isRemote) {
                    this.heal(10.0F);
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                }

                return true;
            } else {
                return super.processInteract(player, hand);
            }
        }
        else if (!itemstack.isEmpty() && (itemstack.getItem() == Items.PORKCHOP || itemstack.getItem() == Items.BEEF) && player.getDistanceSq(this) < 9.0) {
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
                    this.witcherycompanion$fixSetSitting(true);
                    this.world.setEntityState(this, (byte)7);
                } else {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        } else {
            return !this.isBreedingItem(itemstack) && super.processInteract(player, hand);
        }
    }

    /** Fixes a desync between the aiSit.isSitting and this.isSitting **/
    @Unique
    private void witcherycompanion$fixSetSitting(boolean sit) {
        this.aiSit.setSitting(sit);
        this.setSitting(sit);
    }

    /** This mixin makes it so that the Monkey is actually on top instead of intersecting with the carried entity **/
    @Inject(method = "getMountedYOffset", remap = true, at = @At("HEAD"), cancellable = true)
    private void getMountedYOffset(CallbackInfoReturnable<Double> cir) {
        if (EntityTweaks.wingedMonkey_tweakEntityCarryMountOffset) {
            cir.setReturnValue(!this.getPassengers().isEmpty() ? (double) (-this.getPassengers().get(0).height) : 0.0);
        }
    }

    /** Replaces the EntityAttackMelee() with EntityAIFlyerAttackOnCollide, ported to 1.12 from the original Witchery **/
    @WrapOperation(method = "<init>", remap = false, at = @At(value = "INVOKE", remap = true, ordinal = 2,
            target = "Lnet/minecraft/entity/ai/EntityAITasks;addTask(ILnet/minecraft/entity/ai/EntityAIBase;)V"))
    private void fixDefendOwnerAI(EntityAITasks instance, int i, EntityAIBase entityAIBase, Operation<Void> original) {
        if (EntityTweaks.wingedMonkey_fixAI) {
            instance.addTask(i, new EntityAIFlyerAttackOnCollide(this, 1.0, true));
        }
        else {
            original.call(instance, i, entityAIBase);
        }
    }

    /** This Mixin solves a crash when we override the createNavigator function, as Witchery performs a class cast
     * on the result of getNavitagor() to PathNavigateGround, while we override with PathNavigateFlying **/
    @WrapOperation(method = "<init>", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/msrandom/witchery/entity/EntityWingedMonkey;getNavigator()Lnet/minecraft/pathfinding/PathNavigate;"))
    private PathNavigate injectIntoInit(EntityWingedMonkey instance, Operation<PathNavigate> original) {
        if (EntityTweaks.wingedMonkey_fixAI) {
            return new PathNavigateGround((EntityWingedMonkey) (Object) this, this.world);
        }
        return original.call(instance);
    }

    @Override
    @Nonnull
    protected PathNavigate createNavigator(@Nonnull World worldIn) {
        if (EntityTweaks.wingedMonkey_fixAI) {
            PathNavigateFlying pathnavigateflying = new PathNavigateFlying((EntityWingedMonkey) (Object) this, worldIn);
            pathnavigateflying.setCanOpenDoors(false);
            pathnavigateflying.setCanFloat(true);
            pathnavigateflying.setCanEnterDoors(true);
            return pathnavigateflying;
        }
        return super.createNavigator(worldIn);
    }
}
