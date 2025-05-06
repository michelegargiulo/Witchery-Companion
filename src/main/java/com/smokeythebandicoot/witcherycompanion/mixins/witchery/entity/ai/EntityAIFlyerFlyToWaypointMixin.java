package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.ai;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.msrandom.witchery.block.BlockVoidBramble;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.EntityWitchProjectile;
import net.msrandom.witchery.entity.ai.EntityAIFlyerFlyToWaypoint;
import net.msrandom.witchery.entity.item.EntityBrew;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.util.TeleportationUtil;
import net.msrandom.witchery.util.Waypoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


/**
 * Mixins:
 * [Bugfix] Fix Winged Monkey Fly to Waypoint AI
 */
@Mixin(EntityAIFlyerFlyToWaypoint.class)
public abstract class EntityAIFlyerFlyToWaypointMixin extends EntityAIBase {

    @Shadow(remap = false) @Final
    private EntityFlyingTameable flyer;

    @Shadow(remap = false) @Final
    private EntityAIFlyerFlyToWaypoint.CarryRequirement carryRequirement;

    @Shadow(remap = false)
    int courseTimer;


    @Inject(method = "updateTask", remap = true, at = @At("HEAD"), cancellable = true)
    private void fixAiMovement(CallbackInfo ci) {

        // If sitting, do nothing
        if (this.flyer.isSitting()) {
            ci.cancel();
            return;
        }

        Waypoint waypoint = this.flyer.getWaypoint();
        if (this.carryRequirement == EntityAIFlyerFlyToWaypoint.CarryRequirement.ENTITY_LIVING) {
            if (this.flyer.getDistanceSq(waypoint.x, waypoint.y, waypoint.z) <= 1.0) {

                List<EntityLivingBase> entities = this.flyer.world.getEntitiesWithinAABB(EntityLivingBase.class,
                        this.flyer.getEntityBoundingBox().expand(1.0, 1.0, 1.0));

                if (entities.size() > 1) {
                    if (!this.flyer.world.isRemote) {
                        for (EntityLivingBase entity : entities) {
                            if (entity != this.flyer) {
                                entity.startRiding(this.flyer);
                            }
                        }
                    }

                    this.flyer.waypoint = ItemStack.EMPTY;
                    waypoint = this.flyer.getWaypoint();
                }

            }
        }

        else if (!this.flyer.getHeldItemMainhand().isEmpty() && this.flyer.getDistanceSq(waypoint.x, waypoint.y, waypoint.z) <= 1.0) {
            if (!this.flyer.world.isRemote) {
                ItemStack stack = this.flyer.getHeldItemMainhand();
                this.flyer.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);

                // Brew
                if (TeleportationUtil.isBrew(stack)) {
                    this.flyer.world.playSound(null, this.flyer.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT,
                            this.flyer.getSoundCategory(), 0.5F, 0.4F / (this.flyer.world.rand.nextFloat() * 0.4F + 0.8F));
                    EntityWitchProjectile projectile = new EntityWitchProjectile(this.flyer.world, this.flyer, stack);
                    projectile.shoot(this.flyer, this.flyer.rotationPitch, this.flyer.rotationYaw, -20.0F, 0.5F, 1.0F);
                    projectile.motionX = 0.0;
                    projectile.motionZ = 0.0;
                    this.flyer.world.spawnEntity(projectile);
                }

                // Any item that is not a Lingering or Splash Brew Bottle
                else if (stack.getItem() != WitcheryGeneralItems.SPLASH_BREW_BOTTLE && stack.getItem() != WitcheryGeneralItems.LINGERING_BREW_BOTTLE) {
                    if (stack.getItem() != Items.SPLASH_POTION && stack.getItem() != Items.LINGERING_POTION) {
                        EntityItem item = new EntityItem(this.flyer.world, this.flyer.posX, this.flyer.posY, this.flyer.posZ, stack);
                        if (stack.getItem() == Item.getItemFromBlock(WitcheryBlocks.MINDRAKE)) {
                            item.lifespan = 60;
                        }

                        this.flyer.world.spawnEntity(item);
                    }
                    else {
                        this.flyer.world.playSound(null, this.flyer.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, this.flyer.getSoundCategory(), 0.5F, 0.4F / (this.flyer.world.rand.nextFloat() * 0.4F + 0.8F));
                        EntityPotion projectile = new EntityPotion(this.flyer.world, this.flyer, stack);
                        projectile.shoot(this.flyer, this.flyer.rotationPitch, this.flyer.rotationYaw, -20.0F, 0.5F, 1.0F);
                        projectile.motionX = 0.0;
                        projectile.motionZ = 0.0;
                        this.flyer.world.spawnEntity(projectile);
                    }
                }

                // Lingering or Splash Brew Bottle
                else {
                    this.flyer.world.playSound(null, this.flyer.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, this.flyer.getSoundCategory(), 0.5F, 0.4F / (this.flyer.world.rand.nextFloat() * 0.4F + 0.8F));
                    EntityBrew projectile = new EntityBrew(this.flyer.world, this.flyer, stack, false);
                    projectile.shoot(this.flyer, this.flyer.rotationPitch, this.flyer.rotationYaw, -20.0F, 0.75F, 1.0F);
                    projectile.motionX = 0.0;
                    projectile.motionZ = 0.0;
                    this.flyer.world.spawnEntity(projectile);
                }
            }

            this.flyer.waypoint = ItemStack.EMPTY;
            waypoint = this.flyer.getWaypoint();
        }

        // Compute distance to waypoint
        double dX = waypoint.x - this.flyer.posX;
        double dY = waypoint.y - this.flyer.posY;
        double dZ = waypoint.z - this.flyer.posZ;

        double trajectory = dX * dX + dY * dY + dZ * dZ;
        trajectory = MathHelper.sqrt(trajectory);
        if (trajectory >= 128.0 && this.carryRequirement == EntityAIFlyerFlyToWaypoint.CarryRequirement.HELD_ITEM) {
            BlockVoidBramble.teleportRandomly(this.flyer.world, new BlockPos(waypoint.x, waypoint.y, waypoint.z), this.flyer, 16);
        }

        // New part, missing from OG Witchery
        // Mappings gotten from https://github.com/LegacyModdingMC/LegacyMappings/blob/1.7.10/mappings/net/minecraft
        if (--this.courseTimer < 0) {
            this.courseTimer = 0;
        }
        if (this.courseTimer == 0) {
            if (!this.witcherycompanion$isCourseTraversable(waypoint.x, waypoint.y, waypoint.z, trajectory)) {
                double newX = this.flyer.posX + (this.flyer.world.rand.nextDouble() * 4.0 - 2.0) * 6.0;
                double newY = this.flyer.posY + (this.flyer.world.rand.nextDouble() * 2.0 - 1.0) * 4.0;
                double newZ = this.flyer.posZ + (this.flyer.world.rand.nextDouble() * 4.0 - 2.0) * 6.0;
                if (this.flyer.world.rand.nextInt(2) != 0) {
                    dX = newX - this.flyer.posX;
                    dZ = newZ - this.flyer.posZ;
                }
                dY = this.flyer.getDistanceSq(waypoint.x, waypoint.y, waypoint.z) <= 1.0 ? (this.flyer.posY > waypoint.y && newY > 0.0 ? -newY : newY) - this.flyer.posY : newY - this.flyer.posY;
                trajectory = dX * dX + dY * dY + dZ * dZ;
                trajectory = MathHelper.sqrt(trajectory);
            }
            double ACCELERATION = 0.2;
            this.flyer.motionX += dX / trajectory * ACCELERATION;
            this.flyer.motionZ += dZ / trajectory * ACCELERATION;
            this.flyer.motionY = this.flyer.motionY + (dY / trajectory * ACCELERATION + (this.flyer.posY < Math.min(waypoint.y + (double)(this.carryRequirement == EntityAIFlyerFlyToWaypoint.CarryRequirement.HELD_ITEM ? 32 : 32), 255.0) ? 0.1 : 0.0));
            this.courseTimer = 10;
        }

        this.flyer.getMoveHelper().setMoveTo(waypoint.x, Math.min(waypoint.y + 32.0, 255.0), waypoint.z, trajectory * 0.1);
        ci.cancel();
    }

    @Unique
    private boolean witcherycompanion$isCourseTraversable(double x, double y, double z, double distance) {
        double offsetX = (x - this.flyer.posX) / distance;
        double offsetY = (y - this.flyer.posY) / distance;
        double offsetZ = (z - this.flyer.posZ) / distance;

        // Copy AABB
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                this.flyer.getEntityBoundingBox().minX,
                this.flyer.getEntityBoundingBox().minY,
                this.flyer.getEntityBoundingBox().minZ,
                this.flyer.getEntityBoundingBox().maxX,
                this.flyer.getEntityBoundingBox().maxY,
                this.flyer.getEntityBoundingBox().maxZ
        );
        int i = 1;
        while ((double)i < distance) {
            axisalignedbb.offset(offsetX, offsetY, offsetZ);
            if (!this.flyer.world.getCollisionBoxes(this.flyer, axisalignedbb).isEmpty()) {
                return false;
            }
            ++i;
        }
        return true;
    }

}
