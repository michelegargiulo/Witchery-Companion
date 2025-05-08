package com.smokeythebandicoot.witcherycompanion.patches.entity.wingedmonkey.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFlyerAttackOnCollide extends EntityAIBase {

    World world;
    protected EntityCreature attacker;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    protected int attackTick;
    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    boolean longMemory;
    /** The PathEntity of our entity. */
    Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    protected final int attackInterval = 20;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAIFlyerAttackOnCollide(EntityCreature creature, double speedIn, boolean useLongMemory) {
        this.attacker = creature;
        this.world = creature.world;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        else {
            if (canPenalize) {
                if (--this.delayCounter <= 0) {
                    this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                    this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
                    return this.path != null;
                }
                else {
                    return true;
                }
            }
            this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);

            if (this.path != null) {
                return true;
            }
            else {
                return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        else if (!this.longMemory) {
            return !this.attacker.getNavigator().noPath();
        }
        else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase))) {
            return false;
        }
        else {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
        }
    }


    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
        this.delayCounter = 0;
    }

    public void resetTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative())) {
            this.attacker.setAttackTarget(null);
        }

        this.attacker.getNavigator().clearPath();
    }

    public void updateTask() {
        double d0;
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && --this.delayCounter <= 0) {
            this.delayCounter = this.failedPathFindingPenalty + 4 + this.attacker.world.rand.nextInt(7);
            d0 = entitylivingbase.posX - this.attacker.posX;
            double d1 = entitylivingbase.posY - this.attacker.posY;
            double d2 = entitylivingbase.posZ - this.attacker.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (this.isCourseTraversable(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, d3 = MathHelper.sqrt(d3))) {
                this.attacker.motionX += d0 / d3 * 0.15;
                this.attacker.motionY += d1 / d3 * 0.15;
                this.attacker.motionZ += d2 / d3 * 0.15;
                this.failedPathFindingPenalty = 0;
            } else {
                this.failedPathFindingPenalty += 10;
            }
            this.attacker.renderYawOffset = this.attacker.rotationYaw = -((float)Math.atan2(this.attacker.motionX, this.attacker.motionZ)) * 180.0f / (float)Math.PI;
        }
        this.attackTick = Math.max(this.attackTick - 1, 0);
        d0 = this.attacker.width * 2.0f * this.attacker.width * 2.0f + entitylivingbase.width;
        if (this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            if (!this.attacker.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
                this.attacker.swingArm(EnumHand.MAIN_HAND);
            }
            this.attacker.attackEntityAsMob(entitylivingbase);
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget) {
        return this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width;
    }

    private boolean isCourseTraversable(double x, double y, double z, double speed) {
        double d4 = (x - this.attacker.posX) / speed;
        double d5 = (y - this.attacker.posY) / speed;
        double d6 = (z - this.attacker.posZ) / speed;
        AxisAlignedBB original = this.attacker.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                original.minX,
                original.minY,
                original.minZ,
                original.maxX,
                original.maxY,
                original.maxZ
        );
        int i = 1;
        while ((double)i < speed) {
            axisalignedbb.offset(d4, d5, d6);
            if (!this.attacker.world.getCollisionBoxes(this.attacker, axisalignedbb).isEmpty()) {
                return false;
            }
            ++i;
        }
        return true;
    }

}