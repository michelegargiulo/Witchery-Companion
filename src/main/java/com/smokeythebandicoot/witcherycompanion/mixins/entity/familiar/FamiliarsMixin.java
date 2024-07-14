package com.smokeythebandicoot.witcherycompanion.mixins.entity.familiar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.FamiliarInstance;
import net.msrandom.witchery.entity.familiar.FamiliarType;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Familiars.class)
public abstract class FamiliarsMixin {

    @Inject(method = "summonFamiliar", remap = false, cancellable = true, at = @At("HEAD"))
    private static void summonDebug(EntityPlayer player, double x, double y, double z, CallbackInfoReturnable<Familiar<?>> cir) {

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        FamiliarInstance instance = playerEx.familiar;

        if (instance != null) {

            if (!instance.isSummoned()) {

                FamiliarType<?, ?> type = instance.getFamiliarType();
                NBTTagCompound data = instance.getData();
                if (data.hasUniqueId("UUID")) {
                    data.setUniqueId("UUID", UUID.randomUUID());
                }
                Entity entity = EntityList.createEntityFromNBT(instance.getData(), player.world);

                if (entity != null) {
                    entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                    Familiar<?> familiar = type.create(entity);
                    familiar.setColor(instance.getColor());
                    familiar.setFamiliarTamed(true);
                    familiar.setFamiliarOwnerId(player.getUniqueID());
                    familiar.setFamiliar(50.0F);
                    boolean hasSpawned = player.world.spawnEntity(familiar.getEntity());
                    player.world.setEntityState(familiar.getEntity(), (byte)7);
                    instance.setSummoned(hasSpawned);
                    familiar.getEntity().playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 0.4F / player.world.rand.nextFloat() * 0.4F + 0.8F);
                    cir.setReturnValue(familiar);
                    return;
                }
            }
        }

        cir.setReturnValue(null);
    }

    @WrapOperation(method = "handleLivingDeath", remap = false, at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/entity/familiar/Familiar;dismiss()V", remap = false))
    private static void handleLivingDeath(Familiar instance, Operation<Void> original) {
    }

    @WrapOperation(method = "handleLivingDeath", remap = false, at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/entity/familiar/Familiar;getOwner()Lnet/minecraft/entity/Entity;", ordinal = 0, remap = false))
    private static Entity handleLivingDeathInject(Familiar instance, Operation<Entity> original) {
        Entity entity = original.call(instance);
        if (entity instanceof EntityPlayer) {
            instance.dismiss((EntityPlayer) entity);
        } else {
            instance.dismiss();
        }
        return entity;
    }

    @Inject(method = "getBoundFamiliar", remap = false, cancellable = true, at = @At("HEAD"))
    private static void WPgetBoundFamiliar(EntityPlayer player, CallbackInfoReturnable<Familiar<?>> cir) {
        if (player == null) {
            cir.setReturnValue(null);
            return;
        }

        for (Entity entity : player.world.getEntities(Entity.class, entity -> entity instanceof Familiar<?>)) {
            Familiar<?> familiar = (Familiar<?>) entity;
            if (familiar.getOwnerId() != null && familiar.getOwnerId().equals(player.getUniqueID())) {
                cir.setReturnValue(familiar);
                return;
            }
        }

        cir.setReturnValue(null);

    }

}
