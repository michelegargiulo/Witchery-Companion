package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityBroom;
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
@Mixin(value = EntityBroom.class)
public abstract class EntityBroomMixin extends Entity {

    @Final
    @Shadow(remap = false)
    private static DataParameter<Integer> HIT;          // Used in Init

    @Final
    @Shadow(remap = false)
    private static DataParameter<Float> DAMAGE;         // Used in Init

    @Final
    @Shadow(remap = false)
    private static DataParameter<Integer> DIRECTION;    // Used in Init

    @Final
    @Shadow(remap = false)
    private static DataParameter<Integer> BRUSH_COLOR;  // Used in Init

    //
    @Shadow(remap = false)
    public abstract EnumDyeColor getBrushColor();       // Used by writeEntityToNBT

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


    @Inject(method = "attackEntityFrom", remap = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/msrandom/witchery/entity/EntityBroom;setDead()V"))
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.enchantedBroom_fixFreezeOnBreak) {
            this.removePassengers();
        }
    }
}
