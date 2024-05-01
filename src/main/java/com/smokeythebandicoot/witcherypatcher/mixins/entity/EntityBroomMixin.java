package com.smokeythebandicoot.witcherypatcher.mixins.entity;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityBroom;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 [Bugfix] Fix EntityBroom freezing the game when breaking
 [Tweak] Tweak Broom Maximum Health
 */
@Mixin(value = EntityBroom.class, remap = false)
public abstract class EntityBroomMixin extends Entity {

    @Shadow
    public abstract void setForwardDirection(int par1);

    @Shadow
    public abstract int getForwardDirection();

    @Shadow
    public abstract void setTimeSinceHit(int par1);

    @Shadow
    public abstract void setDamageTaken(float par1);

    @Shadow
    public abstract float getDamageTaken();

    @Shadow
    public abstract EnumDyeColor getBrushColor();

    @Shadow @Final
    private static DataParameter<Float> DAMAGE;

    @Shadow @Final
    private static DataParameter<Integer> DIRECTION;

    @Shadow @Final
    private static DataParameter<Integer> HIT;

    @Shadow @Final
    private static DataParameter<Integer> BRUSH_COLOR;

    private EntityBroomMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(BRUSH_COLOR, -1);
        this.dataManager.register(HIT, 0);
        this.dataManager.register(DIRECTION, 1);
        this.dataManager.register(DAMAGE, 0.0F);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("BrushColor")) {
            this.dataManager.set(BRUSH_COLOR, Integer.valueOf(compound.getByte("BrushColor")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setByte("BrushColor", (byte)this.getBrushColor().getMetadata());
    }


    @Inject(method = "attackEntityFrom", remap = true, at = @At("HEAD"), cancellable = true)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.enchantedBroom_fixFreezeOnBreak) {
            if (this.isEntityInvulnerable(source)) {
                cir.setReturnValue(false);
            } else if (!this.world.isRemote && !this.isDead) {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                this.markVelocityChanged();
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) source.getTrueSource()).capabilities.isCreativeMode;

                if (flag || this.getDamageTaken() > ModConfig.PatchesConfiguration.EntityTweaks.enchantedBroom_tweakMaxHealth) {
                    if (!this.getPassengers().isEmpty()) {
                        this.startRiding(this);
                    }

                    if (!flag) {
                        ItemStack broomStack = new ItemStack(WitcheryIngredientItems.ENCHANTED_BROOM);
                        if (this.hasCustomName()) {
                            broomStack.setStackDisplayName(this.getCustomNameTag());
                        }

                        WitcheryIngredientItems.ENCHANTED_BROOM.setColor(broomStack, this.getBrushColor());
                        this.entityDropItem(broomStack, 0.0F);
                    }

                    // This is the call that is missing and makes the game freeze
                    this.removePassengers();

                    this.setDead();
                }

                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}
