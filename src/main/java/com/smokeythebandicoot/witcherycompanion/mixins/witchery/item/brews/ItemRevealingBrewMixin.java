package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item.brews;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityPoltergeist;
import net.msrandom.witchery.entity.EntitySpectre;
import net.msrandom.witchery.entity.EntityWitchProjectile;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcherySounds;
import net.msrandom.witchery.init.data.WitcheryAlternateForms;
import net.msrandom.witchery.item.brews.ItemRevealingBrew;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRevealingBrew.class)
public abstract class ItemRevealingBrewMixin {

    @Unique
    private EntityLivingBase witcherycompanion$caster = null;

    /** Captures the caster entity for later use **/
    @Inject(method = "impact", remap = false, at = @At("HEAD"), cancellable = false)
    private void captureBrewThrower(World world, EntityWitchProjectile projectile, EntityLivingBase caster, RayTraceResult hit, boolean enhanced, CallbackInfoReturnable<Boolean> cir) {
        this.witcherycompanion$caster = caster;
    }

    /** This Mixin completely replaces the affectEntity function inside the ItemRevealingBrew.Companion. This is
     * because we need both the caster and the affected entity, and both are obtainable from the main class and not in
     * the Companion object. Caster object is released at the end, regardless **/
    @WrapOperation(method = "impact", remap = false, at = @At(value = "INVOKE", remap = false,
        target = "Lnet/msrandom/witchery/item/brews/ItemRevealingBrew$Companion;affectEntity(Lnet/minecraft/entity/EntityLivingBase;)V"))
    private void spectreRevealUnlockProgress(ItemRevealingBrew.Companion instance, EntityLivingBase entity, Operation<Void> original) {

        boolean hasRemovedInvisibility = false;

        if (entity.isPotionActive(MobEffects.INVISIBILITY)) {
            entity.removePotionEffect(MobEffects.INVISIBILITY);
            hasRemovedInvisibility = true;
        }

        if (entity.isInvisible()) {
            entity.setInvisible(false);
            hasRemovedInvisibility = true;
        }

        if (hasRemovedInvisibility) {
            if (entity instanceof EntityPoltergeist && witcherycompanion$caster instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) witcherycompanion$caster;
                ProgressUtils.unlockProgress(player, ProgressUtils.getCreatureSecret(EntityPoltergeist.class, "reveal"),
                        WitcheryProgressEvent.EProgressTriggerActivity.ENTITY_REVEAL.activityTrigger);
            }
        }

        if (entity instanceof EntityPlayer) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension((EntityPlayer)entity);
            CreatureForm playerForm = playerEx.getCurrentForm();
            if (playerForm != null && playerForm.equals(WitcheryAlternateForms.PLAYER)) {
                World world = entity.world;
                world.playSound(null, entity.getPosition(), WitcherySounds.ENTITY_VAMPIRE_POOF, SoundCategory.PLAYERS, 0.5F, 0.4F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
                WitcheryUtils.addNewParticles(world, EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY, entity.posZ, 0.0, 20, 0.5, 2.0);
                playerEx.setCurrentForm(null);
                // If the Vampire reveal should be progress, unlockProgress call should be here
                // It is not transformed into secret progress because it would be unobtainable in single player
            }
        }

        if (entity instanceof EntitySpectre && ((EntitySpectre)entity).isObscured()) {
            ((EntitySpectre)entity).setObscured(false);
            if (witcherycompanion$caster instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) witcherycompanion$caster;
                ProgressUtils.unlockProgress(player, ProgressUtils.getCreatureSecret(EntitySpectre.class, "reveal"),
                        WitcheryProgressEvent.EProgressTriggerActivity.ENTITY_REVEAL.activityTrigger);
            }
        }

        // Cannot release caster variable as this function gets called multiple times by the brew.
        // Clearing it here won't allow progress to be unlocked if the first call to this function
        // is not performed upon an obscured spectre / poltergeist
    }

}
