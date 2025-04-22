package com.smokeythebandicoot.witcherycompanion.mixins.witchery.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.vanillaaccessors.player.IEntityPlayerAccessor;
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
import net.msrandom.witchery.util.EntitySizeInfo;
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
 [Bugfix] Fix spectator player falling in the void after world reload/dimension change
 [Tweak] Current Health after a transformation will be set as the same percentage of HP as before the transformation
 [Integration] Morph integration
 [Feature] Unlock progress when werewolf infects player with Curse of the Wolf
 */
@Mixin(ShapeShift.class)
public abstract class ShapeShiftMixin {

    @Shadow(remap = false)
    public abstract CreatureForm.Stats getFormStats(PlayerExtendedData playerEx);

    @Shadow(remap = false)
    public abstract void initCurrentShift(EntityLivingBase entity, EntityPlayer player, boolean applyDamage);

    @Unique
    private Float witcherycompanion$prevHpPercentOnTransform = null;

    @Unique
    private EntityPlayer witcherycompanion$prevPlayerOnTransform$init2 = null;


    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At("HEAD"), cancellable = true)
    public void initCurrentShift(EntityPlayer player, CallbackInfo ci) {
        if (!player.world.isRemote) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
            player.eyeHeight = sizeInfo.eyeHeight;
            this.initCurrentShift(player, player, true);

            /** [Integration] Handle size desync if morph is installed **/
            if (Loader.isModLoaded(Mods.MORPH) && ModConfig.IntegrationConfigurations.MorphIntegration.fixSizeDesyncOnDimChange) {
                MorphIntegration.INSTANCE.handleMorphOnShapeShift(player);
            }

            CreatureForm.Stats stats = this.getFormStats(playerEx);

            /** [Bugfix] Avoid updating flying capability if player is spectator or creative **/
            if (!(player.isCreative() || player.isSpectator())) {
                player.capabilities.allowFlying = stats.canFly();
                if (!player.capabilities.allowFlying && player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                } else if (player.capabilities.allowFlying) {
                    player.capabilities.isFlying = true;
                }

                /** [Bugfix] Fix floating entities **/
                if (CommonTweaks.shapeShift_fixFloatingEntities) {
                    if (player instanceof EntityPlayerMP) {
                        EntityPlayerMP playerMP = ((EntityPlayerMP)player);
                        if (playerMP.connection != null)
                            playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
                    }
                } else {
                    player.sendPlayerAbilities();
                }
            }

            WitcheryNetworkChannel.sendToAll(new PacketSyncEntitySize(player));
        }

        ci.cancel();
    }

    @WrapOperation(method = "updatePlayerState", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    private void avoidUpdatingPlayerAbilitiesUpdate(EntityPlayer instance, Operation<Void> original) {
        if (CommonTweaks.shapeShift_fixFloatingEntities) {
            if (instance instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = ((EntityPlayerMP)instance);
                playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
            }
            return;
        }
        original.call(instance);
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
        witcherycompanion$prevHpPercentOnTransform = player.getHealth() / player.getMaxHealth();
        witcherycompanion$prevPlayerOnTransform$init2 = player;
    }

    /** This Mixin restores the player and hp percentage the preserve patch */
    @Unique
    private void witchery_Patcher$restoreData() {
        if (witcherycompanion$prevHpPercentOnTransform != null && witcherycompanion$prevPlayerOnTransform$init2 != null) {
            witcherycompanion$prevPlayerOnTransform$init2.setHealth(witcherycompanion$prevPlayerOnTransform$init2.getMaxHealth() * witcherycompanion$prevHpPercentOnTransform);
            witcherycompanion$prevPlayerOnTransform$init2 = null;
            witcherycompanion$prevHpPercentOnTransform = null;
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
            accessor.witcherycompanion$accessor$setCurrentFormWidthScale(stats.getWidth() / 0.6f);
            accessor.witcherycompanion$accessor$setCurrentFormHeightScale(stats.getHeight() / 1.8f);
            accessor.witcherycompanion$accessor$setCurrentFormEyeHeightScale(stats.getEyeHeight() / stats.getHeight());
            accessor.witcherycompanion$accessor$setCurrentFormStepHeightScale(stats.getStepHeight() / 0.6f);
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
