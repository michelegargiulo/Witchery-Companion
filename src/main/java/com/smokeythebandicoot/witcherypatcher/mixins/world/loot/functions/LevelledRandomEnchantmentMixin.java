package com.smokeythebandicoot.witcherypatcher.mixins.world.loot.functions;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.msrandom.witchery.world.loot.functions.LevelledRandomEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = LevelledRandomEnchantment.class)
public class LevelledRandomEnchantmentMixin {

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
