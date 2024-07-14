package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.passive.EntityToad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 Mixins:
 [Bugfix] Fix Familiar having null owner after world reload
 */
@Mixin(EntityToad.class)
public abstract class EntityToadMixin extends EntityTameable implements Familiar<EntityToad> {

    private EntityToadMixin(World worldIn) {
        super(worldIn);
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
