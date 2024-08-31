package com.smokeythebandicoot.witcherycompanion.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.barkbelt.BarkBeltApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.WorldGenTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.config.WitcheryConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fixes Crash on Village Worldgen, when Witchery attempts to generate items inside chests or item frames and
   tries to add duplicate loot pools (pools with the same name)
 [Tweak] Disable PvP and PvE for Poppets
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    @Inject(method = "loadLoot", remap = false, cancellable = true, at = @At("HEAD"))
    private static void fixAddedLoot(LootTableLoadEvent event, CallbackInfo ci) {

        if (!WorldGenTweaks.frameWithBook_fixCrashOnVillageGen) return;

        if (event.getName().toString().equals("witchery:chests/bookshop")) {

            for (Item item : WitcheryConfigOptions.townBooks) {
                if (item != null) {
                    String entryName = item.getRegistryName().toString();
                    LootEntry[] entries = new LootEntry[]{
                            new LootEntryItem(item, 1, 0, new LootFunction[0], new LootCondition[0], entryName)
                    };
                    LootTable table = event.getTable();
                    if (table.getPool(entryName) == null)
                        table.addPool(new LootPool(entries, new LootCondition[0], new RandomValueRange(1.0F, 1.0F), new RandomValueRange(0.0F, 0.0F), entryName));
                }
            }

        }

        ci.cancel();

    }

    /** This Mixin checks if Poppet Effects should be disabled (for PvP or PvE) and returns a null target entity
     if it should be disabled. Can't return early due the CommonEvents.onLivingHurt function being a huge chunk of code
     and other code would not be executed. Function skips revelant part if targetEntity is null */
    @WrapOperation(method = "onLivingHurt", remap = false, at = @At(value = "INVOKE",  remap = false,
            target = "Lnet/msrandom/witchery/item/ItemTaglockKit;getBoundEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/entity/EntityLivingBase;"))
    private static EntityLivingBase checkDisablePoppetPvX(World world, ItemStack stack, int index, Operation<EntityLivingBase> original) {
        EntityLivingBase targetEntity = original.call(world, stack, index);
        if (ItemTweaks.poppetItem_tweakDisablePvE || ItemTweaks.poppetItem_tweakDisablePvP) {
            if ((ItemTweaks.poppetItem_tweakDisablePvP && targetEntity instanceof EntityPlayer) ||
                    ItemTweaks.poppetItem_tweakDisablePvE && !(targetEntity instanceof EntityPlayer)) {
                return null;
            }
        }
        return targetEntity;
    }

    /** Since Witchery has hardcoded check to if the block below is GRASS or MYCELIUM, we will use this
     * as the "true" return value for the method. Anything else is considered "false" */
    @WrapOperation(method = "onLivingUpdate", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;"))
    private static Block barkBeltCrafttweakerIntegration(IBlockState instance, Operation<Block> original) {
        if (!ModConfig.PatchesConfiguration.ItemTweaks.barkBelt_tweakCraftTweakerIntegration) {
            return original.call(instance);
        }
        if (BarkBeltApi.canRechargeBarkBelt(instance)) {
            return Blocks.GRASS;
        }
        return Blocks.AIR;
    }
}
