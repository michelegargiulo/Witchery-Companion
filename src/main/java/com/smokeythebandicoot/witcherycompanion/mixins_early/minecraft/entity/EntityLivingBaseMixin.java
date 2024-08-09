package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextComponentTranslation;
import net.msrandom.witchery.init.WitcheryPotionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Bugfix] Fix ResizingPotion not working on Players
 */
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {

    @Shadow(remap = true)
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow(remap = true)
    protected int ticksElytraFlying;

    /** This Mixin makes it so that players cannot use Elytra when resized */
    @WrapOperation(method = "updateElytra", remap = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;getFlag(I)Z", remap = true))
    private boolean disableElytraWhenResized(EntityLivingBase instance, int flag, Operation<Boolean> original) {
        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers &&
                this.isPotionActive(WitcheryPotionEffects.RESIZING)) {

            // To avoid spamming the Status effect, disable elytra when ticks is > 1
            if (this.ticksElytraFlying > 1 && ((Object)this instanceof EntityPlayer)) {
                EntityPlayer player = (EntityPlayer) (Object)this;
                player.sendStatusMessage(new TextComponentTranslation(
                        "witcherycompanion.message.potion.resizing.elytra_disallow",
                        new Object[0]), true);
                return false;
            }

        }
        return original.call(instance, flag);
    }


}
