package com.smokeythebandicoot.witcherycompanion.mixins.entity.monster;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.entity.monster.EntityLilith;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 Mixins:
 [Tweak] Disable Lilith Enchanting items
 */
@Mixin(EntityLilith.class)
public abstract class EntityLilithMixin {

    @WrapOperation(method = "processInteract", remap = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;buildEnchantmentList(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;"))
    public List WPdisableLilithEnchanting(Random random, ItemStack itemStack, int i, boolean b, Operation<List> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.lilith_tweakDisableEnchanting) {
            return new ArrayList<EnchantmentData>();
        }
        return original.call(random, itemStack, i, b);
    }

}
