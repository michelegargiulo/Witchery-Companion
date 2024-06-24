package com.smokeythebandicoot.witcherycompanion.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.morph.MorphIntegration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.common.ShapeShift;
import net.msrandom.witchery.transformation.CreatureForm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix floating entities when traveling through dimensions due to an incorrect usage of 'sendPlayerAbilities'
    that spawns tracked entities from origin dimension to target dimension
 */
@Mixin(ShapeShift.class)
public class ShapeShiftMixin {

    @Unique
    private boolean witchery_Patcher$prevFlightCapability = false;

    /** Wraps around the sendPlayerAbilities() call and cancels it. The shape-shifting only changes the ability to fly,
     * so instead of calling EntityPlayerMP.sendPlayerAbilities() that causes the bug, just sync the player's capability
     * to fly. */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At(
            value = "INVOKE", remap = false, target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    public void avoidUpdatingPlayerAbilitiesInit(EntityPlayer instance, Operation<Void> original) {
        // If config option is true, only sync ability to fly, do not update visibility
        if (ModConfig.PatchesConfiguration.CommonTweaks.shapeShift_fixFloatingEntities) {
            if (instance instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = ((EntityPlayerMP)instance);
                playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
            }
            return;
        }
        original.call(instance);
    }

    @WrapOperation(method = "updatePlayerState", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    public void avoidUpdatingPlayerAbilitiesUpdate(EntityPlayer instance, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.shapeShift_fixFloatingEntities) {
            if (instance instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = ((EntityPlayerMP)instance);
                playerMP.connection.sendPacket(new SPacketPlayerAbilities(playerMP.capabilities));
            }
            return;
        }
        original.call(instance);
    }

    /** This mixin will sync  */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/common/ShapeShift;initCurrentShift(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/player/EntityPlayer;Z)V"))
    public void handleMorphOnDimensionChange(EntityPlayer player, CallbackInfo ci) {
        if (Loader.isModLoaded("morph") && ModConfig.IntegrationConfigurations.MorphIntegration.fixSizeDesyncOnDimChange) {
            MorphIntegration.INSTANCE.handleMorphOnShapeShift(player);
        }
    }

    /** This Mixin preserves the capability to fly to be restored later */
    @Inject(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/transformation/CreatureForm$Stats;canFly()Z", remap = false))
    public void preserveFlyingCapability(EntityPlayer player, CallbackInfo ci) {
        witchery_Patcher$prevFlightCapability = player.capabilities.allowFlying;
    }

    /** Witchery sets the capability to fly depending ONLY on if the player's current form allows flight (eg. Vampire
     * in bat form). Other mods might exist that allow flight, so let's be less bully and listen to what other mods
     * have to say */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/transformation/CreatureForm$Stats;canFly()Z", remap = false))
    public boolean removeFlyingStatHegemony(CreatureForm.Stats instance, Operation<Boolean> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.flight_preserveFlightCapability) {
            return witchery_Patcher$prevFlightCapability || original.call(instance);
        }
        return original.call(instance);
    }

}
