package com.smokeythebandicoot.witcherycompanion.mixins.item.brews;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.ErosionBrewEffect;
import net.msrandom.witchery.entity.EntityWitchProjectile;
import net.msrandom.witchery.init.data.brewing.WitcheryBrewEffects;
import net.msrandom.witchery.item.brews.ItemErosionBrew;
import net.msrandom.witchery.item.brews.ItemKettleBrew;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Tweak] Apply the same effects as the Brew of Erosion crafted in cauldron
 */
@Mixin(ItemErosionBrew.class)
public abstract class ItemErosionBrewMixin extends ItemKettleBrew {

    @Inject(method = "impact", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPemulateErosionBrew(World world, EntityWitchProjectile projectile, EntityLivingBase caster, RayTraceResult hit, boolean enhanced, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.ItemTweaks.itemErosionBrew_tweakEmulateBrewEffects) {

            // If one of the params is null, return early and cancel the method to avoid crashes. Also, returns early on miss
            if (world == null || projectile == null || hit == null || hit.typeOfHit == RayTraceResult.Type.MISS) {
                cir.setReturnValue(false);
                return;
            }

            // Grab common data. Stack can be null as it is an unused parameter for Erosion Brew, so doesn't matter
            BlockPos pos = hit.getBlockPos();
            EnumFacing facing = hit.sideHit;
            ErosionBrewEffect effect = new ErosionBrewEffect(WitcheryBrewEffects.EROSION, false);
            EntityPlayer player = caster instanceof EntityPlayer ? (EntityPlayer)caster : null;
            ModifiersEffect modifiers = new ModifiersEffect(1.0f, 1.0f, false, null, false, 0, player);

            // In case the Brew hit a block, emulate the brew action on the block
            if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                effect.applyToBlock(world, pos, facing, 3, modifiers, null);

            // In case the Brew hit an entity, emulate the brew action on the entity
            } else if (hit.typeOfHit == RayTraceResult.Type.ENTITY) {
                EntityLivingBase targetEntity = hit.entityHit instanceof EntityLivingBase ? (EntityLivingBase)hit.entityHit : null;
                if (targetEntity == null) {
                    cir.setReturnValue(false);
                    return;
                } else {
                    effect.applyToEntity(world, targetEntity, modifiers, null);
                }
            }

            // Cancel the event to avoid running the original item's code
            cir.setReturnValue(true);
        }
    }

}
