package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.mojang.authlib.GameProfile;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.msrandom.witchery.block.entity.TileEntityCursedBlock;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.EntityUtil;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 Mixins:
 [Bugfix] Fix crash when adding or updating a curse to a trigger block
 */
@Mixin(value = TileEntityCursedBlock.class)
public abstract class TileEntityCursedBlockMixin extends WitcheryTileEntity {

    @Shadow(remap = false)
    public BrewActionList actionList;

    @Shadow(remap = false)
    public int count;

    @Shadow(remap = false)
    public int duration;

    @Shadow(remap = false)
    public int expansion;

    @Shadow(remap = false)
    public UUID thrower;

    @Unique
    protected int cooldown = 10;

    @Unique
    protected long lastActivation = -1;

    @Inject(method = "writeToNBT", remap = true,
            cancellable = true, at = @At("HEAD"))
    private void WPfixNullActionList(NBTTagCompound nbtRoot, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.cursedBlock_fixNullActionListCrash){
            if (actionList == null) {
                super.writeToNBT(nbtRoot);
                cir.setReturnValue(nbtRoot);
                return;
            }
            nbtRoot.setLong("LastActivation", this.lastActivation);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readLastActivationFromNBT(NBTTagCompound nbtRoot, CallbackInfo ci) {
        this.lastActivation = nbtRoot.getLong("LastActivation");
    }

    @Inject(method = "updateCurse", remap = false,
            cancellable = true, at = @At("HEAD"))
    private void WPfixUpdateCurse(ModifiersImpact impactModifiers, BrewActionList actionList, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.cursedBlock_fixNullActionListCrash) {
            if (this.actionList != null && this.actionList.equals(actionList)) {
                ++this.count;
            } else {
                this.actionList = actionList;
                this.count = 1;
                this.duration = impactModifiers.lifetime.get() >= 0 ? 5 + impactModifiers.lifetime.get() * impactModifiers.lifetime.get() * 5 : 100;
                this.expansion = Math.min(4 + impactModifiers.extent.get(), 10);
                if (impactModifiers.thrower != null) {
                    this.thrower = impactModifiers.thrower.getUniqueID();
                }
            }
            ci.cancel();
        }
    }

    /** This Mixin fixes a NPE on null action list and introduces a configurable cooldown between activations.
     * Also, avoids to consume the potion if the cooldown is active or the affected entity is not an instance
     * of EntityLivingBase (some triggers might be activated by items, arrows, etc) */
    @Inject(method = "applyToEntityAndDestroy", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixNullPlayerOrActionlist(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        long currentTickCount = entity.world.getTotalWorldTime();
        if (entity instanceof EntityLivingBase && (lastActivation + cooldown < currentTickCount)) {

            EntityLivingBase living = (EntityLivingBase)entity;
            EntityPlayer player = WitcheryUtils.getPlayer(this.world, this.thrower);
            player = EntityUtil.playerOrFake(this.world, player);

            // This player object goes into ModifiersEffect caster, that is used for many brews
            // Having player still == null *should* be supported by all brews, that in turn pass this
            // caster to other objects (like world.createExplosion(EntityPlayer player, ...)
            // All usages should be @Nullable, so no problems should arise.
            // A null check is not implemented here, as the effect should effectively be executed
            // even when the caster is null. It is responsibility of each BrewEffect to check for null

            // If action list is null, return early
            if (this.actionList == null) {
                cir.setReturnValue(this.count <= 0);
                return;
            }

            this.actionList.applyToEntity(entity.world, living, new ModifiersEffect(1.0, 1.0, false, living.getPositionVector(), false, 0, player));
            living.world.playSound(null, living.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 0.4F / (living.world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllTracking(new PacketParticles(living.posX, living.posY, living.posZ, 1.0F, 1.0F, 16777215), living);
            lastActivation = currentTickCount;
            this.count--;
        }

        cir.setReturnValue(this.count <= 0);
    }

}
