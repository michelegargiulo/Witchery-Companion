package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.LootTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.passive.EntityCatFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 Mixins:
 [Bugfix] Fix Familiar having null owner after world reload
 [Tweak] Introduce own loot table
 */
@Mixin(value = EntityCatFamiliar.class)
public abstract class EntityCatFamiliarMixin extends EntityOcelot implements Familiar<EntityOcelot> {

    private EntityCatFamiliarMixin(World worldIn) {
        super(worldIn);
    }

    protected ResourceLocation getLootTable() {
        if (LootTweaks.familiarCat_tweakOwnLootTable) {
            return LootTables.FAMILIAR_CAT;
        }
        return LootTableList.ENTITIES_OCELOT;
    }

    /** This Mixin overrides the getOwner(Entity) function inherited by IEntityOwnable */
    @Inject(method = "getOwner()Lnet/minecraft/entity/Entity;", remap = true, cancellable = true, at = @At("HEAD"))
    public void getOwnerEntity(CallbackInfoReturnable<EntityLivingBase> cir) {
        if (EntityTweaks.familiarCat_fixOwnerDisconnect) {
            UUID id = this.getOwnerId();
            if (id == null) {
                cir.setReturnValue(null);
                return;
            }
            cir.setReturnValue(this.world.getPlayerEntityByUUID(id));
        }
    }

    /** This Mixin overrides the getOwner(Entity) function inherited by EntityTameable */
    @Inject(method = "getOwner()Lnet/minecraft/entity/EntityLivingBase;", remap = true, cancellable = true, at = @At("HEAD"))
    public void getOwnerEntityLivingBase(CallbackInfoReturnable<EntityLivingBase> cir) {
        if (EntityTweaks.familiarCat_fixOwnerDisconnect) {
            UUID id = this.getOwnerId();
            if (id == null) {
                cir.setReturnValue(null);
                return;
            }
            cir.setReturnValue(this.world.getPlayerEntityByUUID(id));
        }
    }

}
