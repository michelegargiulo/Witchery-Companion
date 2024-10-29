package com.smokeythebandicoot.witcherycompanion.mixins.witchery.init.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.items.ItemTornPage;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.registry.RegistryWrapper;
import net.msrandom.witchery.registry.WitcheryNamespacedInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


/**
 * Mixins:
 * [Feature] Replace Witchery TornPage item with a new one that can be right-clicked to unlock progress
 */
@Mixin(WitcheryIngredientItems.class)
public abstract class WitcheryIngredientItemsMixin extends WitcheryNamespacedInitializer<Item> {

    @Shadow(remap = false)
    private static <T extends Item> T register(String name, T ingredient) {
        return null;
    }

    private WitcheryIngredientItemsMixin(RegistryWrapper<ResourceLocation, Item> wrapper, String namespace, String configName) {
        super(wrapper, namespace, configName);
    }

    /** This Mixin replaces the register("torn_page") item with a register("torn_page", new ItemTornPage()) call,
     * to replace Witchery TornPage item with a new one that can be right-clicked to unlock progress **/
    @WrapOperation(method = "<clinit>", remap = false, at = @At(value = "INVOKE", ordinal = 32,
            target = "Lnet/msrandom/witchery/init/items/WitcheryIngredientItems;register(Ljava/lang/String;)Lnet/minecraft/item/Item;"))
    private static Item replaceTornPageClass(String name, Operation<Item> original) {
        if (ModConfig.IntegrationConfigurations.PatchouliIntegration.common_replaceImmortalsBook &&
                Loader.isModLoaded(Mods.PATCHOULI))
            return register(name, new ItemTornPage());
        else
            return original.call(name);
    }
}
