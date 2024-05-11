package com.smokeythebandicoot.witcherypatcher.mixins.world.loot.functions;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootContext;
import net.msrandom.witchery.world.loot.functions.LevelledRandomEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = LevelledRandomEnchantment.class, remap = false)
public class LevelledRandomEnchantmentMixin {

    @Inject(method = "apply", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPreturnDummyEnchantedBookIfNullRandom(ItemStack stack, Random random, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.levelledRandomEnchant_fixCrashNullRandom) {
            if (random == null) {
                stack.setStackDisplayName(new TextComponentTranslation("witcherypatches.items.random_enchanted_book.name").getFormattedText());
                //dummyBook.addEnchantment(Enchantments.AQUA_AFFINITY, 1);
                cir.setReturnValue(stack);
            }
        }
    }

}
