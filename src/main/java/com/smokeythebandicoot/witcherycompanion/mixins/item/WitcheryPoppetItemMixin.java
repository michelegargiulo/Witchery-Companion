package com.smokeythebandicoot.witcherycompanion.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.WitcheryPoppetItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix crash when player that is cursed is protected by voodoo protection poppet
 */
@Mixin(WitcheryPoppetItems.class)
public abstract class WitcheryPoppetItemMixin {

    @Shadow(remap = false)
    public static ItemStack findBoundPoppetInWorld(WitcheryPoppetItems.ItemPoppet poppetType,
                                                   EntityPlayer player, int foundItemDamage,
                                                   boolean allIndices, boolean onlyBoosted) {
        return null;
    }

    @Final @Shadow(remap = false)
    public static WitcheryPoppetItems.ItemPoppet VOODOO_PROTECTION;

    @Final @Shadow(remap = false)
    public static WitcheryPoppetItems.ItemPoppet POPPET_PROTECTION;



    @Inject(method = "voodooProtectionActivated(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;ZZ)Z",
            remap = false, at = @At("HEAD"), cancellable = true)
    private static void fixVoodooProtectionActivated(EntityPlayer attackingEntity, ItemStack voodooStack,
                                                       EntityLivingBase targetEntity, boolean allowLightning,
                                                       boolean onlyBoosted, CallbackInfoReturnable<Boolean> cir) {

        if (ModConfig.PatchesConfiguration.ItemTweaks.voodooProtectionPoppet_fixCrashOnCurseProtect) {

            int amount = ModConfig.PatchesConfiguration.ItemTweaks.voodooProtectionPoppet_tweakDamageTaken;
            if (targetEntity instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) targetEntity;
                ItemStack defenseStack = findBoundPoppetInWorld(VOODOO_PROTECTION, targetPlayer, amount, false, onlyBoosted);

                if (defenseStack != null && !targetPlayer.world.isRemote) {
                    if (attackingEntity != null && !defenseStack.isEmpty()) {
                        defenseStack.damageItem(amount, attackingEntity);
                    }

                    if (attackingEntity != null && allowLightning) {
                        EntityLightningBolt lightning = new EntityLightningBolt(attackingEntity.world, attackingEntity.posX, attackingEntity.posY, attackingEntity.posZ, false);
                        attackingEntity.world.addWeatherEffect(lightning);
                    }

                    cir.setReturnValue(true);
                    return;
                }
            }

            cir.setReturnValue(false);
        }
    }

    /**
     * Returns true if the stack is EMPTY or NULL, to avoid NPE
     */
    @Inject(method = "poppetProtectionActivated", remap = false, at = @At("HEAD"), cancellable = true)
    private static void fixPoppetProtectionActivated(EntityPlayer attackingEntity, ItemStack voodooStack,
                                                     EntityLivingBase targetEntity, boolean allowLightning,
                                                     CallbackInfoReturnable<Boolean> cir) {

        if (ModConfig.PatchesConfiguration.ItemTweaks.poppetProtectionPoppet_fixCrashOnProtect) {

            int amount = ModConfig.PatchesConfiguration.ItemTweaks.poppetProtectionPoppet_tweakDamageTakenOnProtect;
            if (targetEntity instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) targetEntity;
                ItemStack defenseStack = findBoundPoppetInWorld(POPPET_PROTECTION, targetPlayer, amount, false, false);
                if (defenseStack != null && !attackingEntity.world.isRemote) {
                    if (!defenseStack.isEmpty()) {
                        defenseStack.damageItem(amount, attackingEntity);
                    }

                    if (allowLightning) {
                        EntityLightningBolt lightning = new EntityLightningBolt(attackingEntity.world, attackingEntity.posX, attackingEntity.posY, attackingEntity.posZ, false);
                        attackingEntity.world.addWeatherEffect(lightning);
                    }

                    cir.setReturnValue(true);
                    return;
                }
            }

            cir.setReturnValue(false);
        }

    }

}
