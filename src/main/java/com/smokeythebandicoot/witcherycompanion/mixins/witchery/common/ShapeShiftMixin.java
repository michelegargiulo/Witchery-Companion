package com.smokeythebandicoot.witcherycompanion.mixins.witchery.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.player.IEntityPlayerAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.CommonTweaks;
import com.smokeythebandicoot.witcherycompanion.integrations.morph.MorphIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.common.ShapeShift;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.network.PacketSyncEntitySize;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix floating entities when traveling through dimensions due to an incorrect usage of 'sendPlayerAbilities'
    that spawns tracked entities from origin dimension to target dimension
 [Tweak] Current Health after a transformation will be set as the same percentage of HP as before the transformation
 [Feature] Unlock progress when werewolf infects player with Curse of the Wolf
 */
@Mixin(ShapeShift.class)
public abstract class ShapeShiftMixin {

    @Shadow(remap = false)
    public abstract CreatureForm.Stats getFormStats(PlayerExtendedData playerEx);

    @Unique
    private boolean witchery_Patcher$prevFlightCapability = false;

    @Unique
    private Float witchery_Patcher$prevHpPercentOnTransform = null;

    @Unique
    private EntityPlayer witchery_Patcher$prevPlayerOnTransform = null;


    /** Wraps around the sendPlayerAbilities() call and cancels it. The shape-shifting only changes the ability to fly,
     * so instead of calling EntityPlayerMP.sendPlayerAbilities() that causes the bug, just sync the player's capability
     * to fly. */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At(
            value = "INVOKE", remap = false, target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    public void avoidUpdatingPlayerAbilitiesInit(EntityPlayer instance, Operation<Void> original) {
        // If config option is true, only sync ability to fly, do not update visibility
        if (CommonTweaks.shapeShift_fixFloatingEntities) {
            if (instance instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = ((EntityPlayerMP)instance);
                if (playerMP.connection != null)
                    playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
            }
            return;
        }
        original.call(instance);
    }

    @WrapOperation(method = "updatePlayerState", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    public void avoidUpdatingPlayerAbilitiesUpdate(EntityPlayer instance, Operation<Void> original) {
        if (CommonTweaks.shapeShift_fixFloatingEntities) {
            if (instance instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = ((EntityPlayerMP)instance);
                playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
            }
            return;
        }
        original.call(instance);
    }

    /** This mixin will sync the entity size when the player changes dimension, taking morph into account */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At(value = "INVOKE",
            remap = false, shift = At.Shift.AFTER, target = "Lnet/msrandom/witchery/common/ShapeShift;initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V"))
    public void handleMorphOnDimensionChange(EntityPlayer player, CallbackInfo ci) {
        if (Loader.isModLoaded(Mods.MORPH) && ModConfig.IntegrationConfigurations.MorphIntegration.fixSizeDesyncOnDimChange) {
            MorphIntegration.INSTANCE.handleMorphOnShapeShift(player);
        }
    }

    /** This Mixin saves a reference to the Shifting player and its health percentage immediately before the
     * applyModifier() call */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 2, shift = At.Shift.BEFORE,
            target = "Lnet/msrandom/witchery/common/ShapeShift;applyModifier(Lnet/minecraft/entity/ai/attributes/IAttribute;Lnet/minecraft/entity/ai/attributes/AttributeModifier;DLnet/minecraft/entity/ai/attributes/AbstractAttributeMap;)V"))
    private void preserveHpPercentageOnTransform(EntityLivingBase entity, EntityPlayer player, boolean applyDamage, CallbackInfo ci) {
        if (CommonTweaks.shapeShift_tweakPreserveHpPercentOnTransform) {
            witchery_Patcher$preserveData(player);
        }
    }

    /** This Mixin uses the preserved player and health percentage and restores it immediately after applyModifier() call */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 2,
            target = "Lnet/msrandom/witchery/common/ShapeShift;applyModifier(Lnet/minecraft/entity/ai/attributes/IAttribute;Lnet/minecraft/entity/ai/attributes/AttributeModifier;DLnet/minecraft/entity/ai/attributes/AbstractAttributeMap;)V"))
    private void restoreHpPercentageOnTransform(ShapeShift instance, IAttribute attribute, AttributeModifier modifier, double modification, AbstractAttributeMap playerAttributes, Operation<Void> original) {
        original.call(instance, attribute, modifier, modification, playerAttributes);
        if (CommonTweaks.shapeShift_tweakPreserveHpPercentOnTransform) {
            witchery_Patcher$restoreData();
        }
    }

    /** This Mixin saves a reference to the Shifting player and its health percentage immediately before the
     * applyModifier() call */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 2, shift = At.Shift.BEFORE,
            target = "Lnet/msrandom/witchery/common/ShapeShift;removeModifier(Lnet/minecraft/entity/ai/attributes/IAttribute;Lnet/minecraft/entity/ai/attributes/AttributeModifier;Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;)V"))
    private void preserveHpPercentageOnDetransform(EntityLivingBase entity, EntityPlayer player, boolean applyDamage, CallbackInfo ci) {
        if (CommonTweaks.shapeShift_tweakPreserveHpPercentOnDetransform) {
            witchery_Patcher$preserveData(player);
        }
    }

    /** This Mixin uses the preserved player and health percentage and restores it immediately after applyModifier() call */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 2,
            target = "Lnet/msrandom/witchery/common/ShapeShift;removeModifier(Lnet/minecraft/entity/ai/attributes/IAttribute;Lnet/minecraft/entity/ai/attributes/AttributeModifier;Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;)V"))
    private void restoreHpPercentageOnDetransform(ShapeShift instance, IAttribute attribute, AttributeModifier modifier, AbstractAttributeMap playerAttributes, Operation<Void> original) {
        original.call(instance, attribute, modifier, playerAttributes);
        if (CommonTweaks.shapeShift_tweakPreserveHpPercentOnDetransform) {
            witchery_Patcher$restoreData();
        }
    }

    /** This Mixin preserves the player and hp percentage the preserve patch */
    @Unique
    private void witchery_Patcher$preserveData(EntityPlayer player) {
        witchery_Patcher$prevHpPercentOnTransform = player.getHealth() / player.getMaxHealth();
        witchery_Patcher$prevPlayerOnTransform = player;
    }

    /** This Mixin restores the player and hp percentage the preserve patch */
    @Unique
    private void witchery_Patcher$restoreData() {
        if (witchery_Patcher$prevHpPercentOnTransform != null && witchery_Patcher$prevPlayerOnTransform != null) {
            witchery_Patcher$prevPlayerOnTransform.setHealth(witchery_Patcher$prevPlayerOnTransform.getMaxHealth() * witchery_Patcher$prevHpPercentOnTransform);
            witchery_Patcher$prevPlayerOnTransform = null;
            witchery_Patcher$prevHpPercentOnTransform = null;
        }
    }

    /** This Mixin enables compat between current form size and Resizing Potion size. Avoid call ResizingUtils.setSize,
     * as the new method simply sets some injected variables inside the EntityPlayer class */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/util/ResizingUtils;setSize(Lnet/minecraft/entity/Entity;FF)V", remap = false))
    private void setCurrentFormStats(Entity instance, float width, float height, Operation<Void> original) {
        if (!ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers) {
            original.call(instance, width, height);
        }
    }

    /** This Mixin adds compat between ResizingPotion and ShapeShifting by setting some internal
     * variables injected inside EntityPlayer class */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/util/ResizingUtils;setSize(Lnet/minecraft/entity/Entity;FF)V", remap = false))
    private void setCurrentFormStats(EntityLivingBase entity, EntityPlayer player, boolean applyDamage, CallbackInfo ci) {
        if (player instanceof IEntityPlayerAccessor) {
            IEntityPlayerAccessor accessor = (IEntityPlayerAccessor) player;
            CreatureForm.Stats stats = this.getFormStats(WitcheryUtils.getExtension(player));
            // Since we are computing just a scaling factor, we compare the creature target dimensions w.r.t.
            // the default player's sizes. Even if other mods chance them, it shouldn't affect this computation
            accessor.accessor_setCurrentFormWidthScale(stats.getWidth() / 0.6f);
            accessor.accessor_setCurrentFormHeightScale(stats.getHeight() / 1.8f);
            accessor.accessor_setCurrentFormEyeHeightScale(stats.getEyeHeight() / stats.getHeight());
            accessor.accessor_setCurrentFormStepHeightScale(stats.getStepHeight() / 0.6f);
            // Send the new size to all players
            WitcheryNetworkChannel.sendToAll(new PacketSyncEntitySize(player));
        }
    }

    /** This Mixin runs almost at the end of the method, where Witchery sends the chat message to notify the player
     * of the new Werewolf injection. This method of acquiring the Curse of the Wolf is not well documented, so assumed
     * to be secret. **/
    @WrapOperation(method = "processWolfInfection(Lnet/minecraft/entity/EntityLivingBase;Lnet/msrandom/witchery/entity/EntityWerewolf;F)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
    private void unlockSecretOnWerewolfInfection(EntityPlayer instance, ITextComponent iTextComponent, Operation<Void> original) {
        original.call(instance, iTextComponent);
        ProgressUtils.unlockProgress(instance, WitcheryCompanion.prefix("werewolf_to_player_infection"),
                WitcheryProgressEvent.EProgressTriggerActivity.WEREWOLF_INFECTION.activityTrigger);
    }

}
