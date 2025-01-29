package com.smokeythebandicoot.witcherycompanion.mixins.witchery.extensions;

import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.extensions.WitcheryExtendedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Implement Throwing Skill
 */
@Mixin(PlayerExtendedData.class)
public abstract class PlayerExtendedDataMixin extends WitcheryExtendedData<EntityPlayer> implements IPlayerExtendedDataAccessor {

    @Shadow(remap = false)
    private int throwingSkill;

    @Unique
    private DivinationData witchery_Patcher$divinationData;

    /** This Mixin actually implements reading the Throwing Skill from player NBT **/
    @Inject(method = "read", remap = false, at = @At("HEAD"))
    private void readThrowingSkill(NBTTagCompound tag, CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.common_fixThrowingSkill) return;
        //this.throwingSkill = tag.getInteger("ThrowingSkill");
        this.throwingSkill = 0;
    }

    /** Getting the throwing skill actually returns it, instead of the bottling skill **/
    @Inject(method = "getThrowingSkill", remap = false, cancellable = true, at = @At("HEAD"))
    public void getThrowingSkill(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.common_fixThrowingSkill) return;
        cir.setReturnValue(this.throwingSkill);
    }

    /** Increments the throwing skill actually and returns the new value **/
    @Inject(method = "increaseThrowingSkill", remap = false, cancellable = true, at = @At("HEAD"))
    public void increaseThrowingSkill(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.common_fixThrowingSkill) return;
        this.throwingSkill = Math.min(this.throwingSkill + 1, 100);
        cir.setReturnValue(throwingSkill);
    }


    /*@Inject(method = "write", remap = false, at = @At("HEAD"))
    private void writeDivinationData(NBTTagCompound tag, CallbackInfo ci) {
        DivinationData.writeToNBT(witchery_Patcher$divinationData, tag);
    }


    @Inject(method = "read", remap = false, at = @At("HEAD"))
    private void readDivinationData(NBTTagCompound tag, CallbackInfo ci) {
        witchery_Patcher$divinationData = DivinationData.readFromNBT(tag);
    }*/

    @Override
    public void setDivinationData(DivinationData data) {
        this.witchery_Patcher$divinationData = data;
    }

    @Override
    public DivinationData getDivinationData() {
        return this.witchery_Patcher$divinationData;
    }
}
