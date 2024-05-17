package com.smokeythebandicoot.witcherypatcher.mixins.world.loot.functions;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.msrandom.witchery.world.loot.functions.LevelledRandomEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 Mixins:
 [Bugfix] Fix Loot Function being called with a Null random, by returning the same input item with a fake enchantment
    (used for Just Enough Resources loot table display in JEI interface)
 */
@Mixin(value = LevelledRandomEnchantment.class)
public abstract class LevelledRandomEnchantmentMixin {

    @Inject(method = "apply", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPreturnDummyEnchantedBookIfNullRandom(ItemStack stack, Random random, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.levelledRandomEnchant_fixCrashNullRandom) {
            if (random == null) {
                // Should not be problematic to set null as an enchantment. Registry returns -1 as ID
                stack.addEnchantment(null, 1);
                cir.setReturnValue(stack);
            }
        }
    }

}
