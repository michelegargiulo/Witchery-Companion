package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.InfusionTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityDemon;
import net.msrandom.witchery.entity.EntityEnt;
import net.msrandom.witchery.entity.EntityImp;
import net.msrandom.witchery.entity.EntitySpellEffect;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.infusion.symbol.AvadaKedavraSymbolEffect;
import net.msrandom.witchery.infusion.symbol.ProjectileSymbolEffect;
import net.msrandom.witchery.potion.PotionEnslaved;
import net.msrandom.witchery.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Tweak to set custom damage to entity types
 */
@Mixin(AvadaKedavraSymbolEffect.class)
public abstract class AvadaKedavraSymbolEffectMixin extends ProjectileSymbolEffect {


    @Inject(method = "onCollision", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakDamagesOnCollision(World world, EntityLivingBase caster, RayTraceResult hit, EntitySpellEffect spell, CallbackInfo ci) {

        // Spell only works on entities
        if (hit.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity target = hit.entityHit;

            // If players, special case: get server of caster and check if PvP is enabled
            if (target instanceof EntityPlayer) {

                if (caster instanceof EntityPlayer) {
                    MinecraftServer server = caster.getServer();

                    if (server == null || !server.isPVPEnabled()) {
                        return;
                    }
                }

                // Caster is not player, so skip PvP check
                witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_tweakPlayerDamage);
            }

            // Else, if hit entity is not player, tweak damages
            else if (target instanceof EntityLiving) {

                if (InfusionTweaks.avadaKedavra_tweakAlwaysInstakillWhenInCreative) {

                }

                if (target.isNonBoss()) {
                    if (target instanceof EntityGolem) {
                        witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_tweakGolemDamage);
                    }

                    else if (target instanceof EntityWitch || target instanceof EntityCovenWitch) {
                        witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_tweakWitchDamage);
                    }

                    else if (target instanceof EntityDemon || target instanceof EntityImp) {
                        witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_tweakDemonDamage);
                    }

                    else {
                        witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_tweakNormalDamage);
                    }
                }

                else {
                    witchery_Patcher$performDamage(target, caster, spell, InfusionTweaks.avadaKedavra_ossDamage);
                }

                if (caster instanceof EntityPlayer && ((EntityPlayer)caster).capabilities.isCreativeMode) {
                    EntityUtil.instantDeath((EntityLivingBase)target, caster, spell);
                } else if ((PotionEnslaved.canCreatureBeEnslaved((EntityLivingBase)target) || target instanceof EntityWitch || target instanceof EntityEnt || target instanceof EntityGolem) && ((EntityLiving)target).getMaxHealth() <= 200.0F) {
                    target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(spell, caster), 200.0F);
                } else {
                    target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(spell, caster), 25.0F);
                }
            }
        }
    }

    @Unique
    private void witchery_Patcher$performDamage(Entity target, EntityLivingBase caster, Entity spell, float amount) {
        if (amount == 0 || !(target instanceof EntityLivingBase))
            return;
        EntityLivingBase targetEntity = (EntityLivingBase) target;
        if (amount < 0) {
            EntityUtil.instantDeath(targetEntity, caster, spell);
        } else {
            targetEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(spell, caster), amount);
        }
    }

}
