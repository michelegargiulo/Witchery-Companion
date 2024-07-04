package com.smokeythebandicoot.witcherycompanion.mixins.entity.familiar;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.FamiliarInstance;
import net.msrandom.witchery.entity.familiar.FamiliarType;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.network.WitcheryNetworkPacket;
import net.msrandom.witchery.network.medallion.PacketSummonFamiliar;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Familiar.class)
public interface FamiliarMixin extends IEntityOwnable {

    @Shadow(remap = false)
    Entity getEntity();

    @Shadow(remap = false)
    EnumDyeColor getColor();

    @Shadow(remap = false)
    boolean isBoundTo(EntityPlayer boundEntity);

    @Shadow(remap = false)
    void dismiss();

    @Shadow(remap = false)
    byte getParticleEffectId();

    @Shadow(remap = false)
    void validate();

    @Shadow(remap = false)
    String[] getNames();

    @Shadow(remap = false)
    FamiliarType getFamiliarType();

    @Shadow(remap = false)
    void setFamiliar(float maxHealth);

    @Shadow(remap = false)
    boolean isValid();

    @Shadow(remap = false)
    Entity getOriginalEntity();

    @Shadow(remap = false)
    void setOriginalEntity(Entity $noName_0);

    @Inject(method = "bindTo", remap = false, cancellable = true, at = @At("HEAD"))
    default void bindTo(EntityPlayer player, CallbackInfo ci) {
        this.validate();
        Familiar familiar = Familiars.getBoundFamiliar(player);
        if (familiar != null) {
            familiar.clearFamiliar();
        }

        String randomName;
        label36: {
            String[] names = this.getNames();
            if (names != null) {
                randomName = names[player.getRNG().nextInt(names.length)];
                if (randomName != null) {
                    break label36;
                }
            }

            randomName = "Familiar";
        }

        String name = randomName;
        if (!this.getEntity().hasCustomName()) {
            CharSequence var8 = name;
            if (var8.length() > 0) {
                this.getEntity().setCustomNameTag(name);
            }
        }

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        FamiliarType type = this.getFamiliarType();
        Class key = FamiliarType.REGISTRY.getKey(type);
        if (type != null && key != null) {
            NBTTagCompound tag = this.getEntity().writeToNBT(new NBTTagCompound());
            tag.setString("id", String.valueOf(EntityList.getKey(this.getEntity())));
            playerEx.familiar = new FamiliarInstance(type, key, tag, this.getColor(), true);
        } else {
            playerEx.familiar = null;
        }

        playerEx.markChanged();
        this.setFamiliar(50.0F);

        ci.cancel();
    }

    @Inject(method = "dismiss(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, cancellable = true, at = @At("HEAD"))
    default void injectDebug(EntityPlayer player, CallbackInfo ci) {
        Utils.logChat("Dismiss(Player)");
        this.dismiss();
        ci.cancel();
    }

    @Inject(method = "dismiss()V", remap = false, cancellable = true, at = @At("HEAD"))
    default void injectDebug(CallbackInfo ci) {
        Utils.logChat("Dismiss()");
        Entity owner = this.getOwner();
        //TODO 1: First fix
        if (owner == null && this.getOwnerId() != null) {
            owner = Minecraft.getMinecraft().world.getPlayerEntityByUUID(this.getOwnerId());
        }

        if (owner != null) {
            if (owner instanceof EntityPlayer && this.isBoundTo((EntityPlayer) owner)) {

                // Retrieve playerOwner and cache familiar Entity
                EntityPlayer playerOwner = (EntityPlayer) owner;
                Entity familiarEntity = this.getEntity();

                // OG Code
                familiarEntity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 0.4F / this.getEntity().world.rand.nextFloat() * 0.4F + 0.8F);
                familiarEntity.setDead();
                PlayerExtendedData playerEx = WitcheryUtils.getExtension((EntityPlayer) owner);
                FamiliarInstance instance = playerEx.familiar;
                if (instance != null) {
                    instance.setSummoned(false);
                    instance.setColor(this.getColor());
                    instance.getData().setString("id", String.valueOf(EntityList.getKey(this.getEntity())));
                    familiarEntity.writeToNBT(instance.getData());
                }

                // Move dismiss() functions here
                if (this.isBoundTo(playerOwner)) {
                    playerOwner.world.setEntityState(familiarEntity, this.getParticleEffectId());
                    WitcheryUtils.getExtension(playerOwner).markChanged();
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "validate", remap = false, cancellable = true, at = @At("HEAD"))
    default void validate(CallbackInfo ci) {
        if (!this.isValid()) {
            Entity originalEntity = this.getOriginalEntity();
            if (originalEntity != null) {
                originalEntity.setDead();
                FamiliarType familiarType = this.getFamiliarType();
                if (familiarType != null) {
                    familiarType.update(originalEntity, (Familiar) this);
                }
            }

            this.getEntity().world.spawnEntity(this.getEntity());
            this.setOriginalEntity(null);
        }
        ci.cancel();

    }

}
