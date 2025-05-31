package com.smokeythebandicoot.witcherycompanion.mixins.witchery.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.BarkBeltApi;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.WorldGenTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.TransformationTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.config.WitcheryConfigOptions;
import net.msrandom.witchery.entity.EntityPoltergeist;
import net.msrandom.witchery.entity.EntitySpectre;
import net.msrandom.witchery.entity.EntitySummonedUndead;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.init.WitcheryPotionEffects;
import net.msrandom.witchery.init.data.WitcheryAlternateForms;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.potion.IHandlePlayerDrops;
import net.msrandom.witchery.transformation.CreatureForm;
import net.msrandom.witchery.transformation.WerewolfCreatureTrait;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

/**
 Mixins:
 [Bugfix] Fixes Crash on Village Worldgen, when Witchery attempts to generate items inside chests or item frames and
   tries to add duplicate loot pools (pools with the same name)
 [Tweak] Disable PvP and PvE for Poppets
 [Tweak] Transform to Wolfman form if level >= 5 werewolf is forced to transform
 [Tweak] Damage Werewolves that are wearing armor cursed by Curse of Binding
 [Progress] Unlock progress when player sprays Graveyard Dust on Spectres and Poltergeists
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
        if (!ItemTweaks.barkBelt_tweakCraftTweakerIntegration) {
            return original.call(instance);
        }
        if (BarkBeltApi.canRechargeBarkBelt(instance)) {
            return Blocks.GRASS;
        }
        return Blocks.AIR;
    }

    @Inject(method = "onEntityJoinWorld", remap = false, cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/entity/ai/EntityAITasks;addTask(ILnet/minecraft/entity/ai/EntityAIBase;)V", remap = true))
    private static void removeInjectedAIs(EntityJoinWorldEvent event, CallbackInfo ci) {
        if (EntityTweaks.villager_disableBackportedAI) {
            ci.cancel();
        }
    }

    /** This Mixin injects into the part of codes that forces a WOLF transformation. Changes the target CreatureForm just
     * before the setCurrentForm call, swapping WOLF form with WOLFMAN if player is Werewolf level 5 or higher **/
    @WrapOperation(method = "onLivingUpdate", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/msrandom/witchery/extensions/PlayerExtendedData;setCurrentForm(Lnet/msrandom/witchery/transformation/CreatureForm;)V"))
    private static void changeToBetterForm(PlayerExtendedData instance, CreatureForm creatureForm, Operation<Void> original) {
        if (TransformationTweaks.werewolf_tweakTransformToWolfman) {
            WerewolfCreatureTrait werewolfTrait = instance.getTransformation(WitcheryCreatureTraits.WEREWOLF);
            if (werewolfTrait.getLevel() >= 5) {
                original.call(instance, WitcheryAlternateForms.WOLFMAN);
                return;
            }
        }
        original.call(instance, WitcheryAlternateForms.WOLF);
    }

    /** This Mixin hijacks the call to getItemStackFromSlot, returning a Moon Charm if the EquipmentSlot is occupied by Armor
     * cursed with Curse of Binding. Returning the Moon Charm prevents Witchery from un-equipping that slot. Meanwhile,
     * we deal damage to the Werewolf. Cursed armor will be dropped anyway on Death **/
    @WrapOperation(method = "onLivingUpdate", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/entity/player/EntityPlayer;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack damageWerewolfWithCursedArmor(EntityPlayer instance, EntityEquipmentSlot entityEquipmentSlot, Operation<ItemStack> original) {
        ItemStack result = original.call(instance, entityEquipmentSlot);
        float damage = TransformationTweaks.werewolf_tweakDamageInBindingCurseArmor;
        if (damage > 0f) {
            if (EnchantmentHelper.hasBindingCurse(result)) {
                instance.attackEntityFrom(DamageSource.MAGIC, damage);
                return new ItemStack(WitcheryGeneralItems.MOON_CHARM);
            }
        }
        return result;
    }

    /** This Mixin removes the NBT addition of WitcheryPriIncUsr tag to dropped items, but keeps the rest
     * of the method logic the same. Important for the Vampire extended item expiration **/
    @Inject(method = "onPlayerDrops", remap = false, at = @At("HEAD"), cancellable = true)
    private static void removePriorIncarnationAdd(PlayerDropsEvent event, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.RitesTweaks.priorIncarnation_tweakDisableRite) {
            return;
        }

        // Cancel the event if there's another thing handling player drops
        for (IHandlePlayerDrops handler : WitcheryPotionEffects.PLAYER_DROPS) {
            if (event.isCanceled()) {
                break;
            }
            if (event.getEntityLiving().isPotionActive(handler.getPotion())) {
                handler.onPlayerDrops(event.getEntityPlayer(), event);
            }
        }

        // Cehck if the player has keep inventory potion
        if (event.getEntityPlayer() != null &&
            !event.getEntityPlayer().world.isRemote &&
            event.getEntityPlayer().isPotionActive(WitcheryPotionEffects.KEEP_INVENTORY)) {

            event.setCanceled(true);
        }
        else {
            // Vampire extended item lifespan
            if (!event.getEntityPlayer().world.isRemote && !event.isCanceled() && WitcheryUtils.getExtension(event.getEntityPlayer()).isTransformation(WitcheryCreatureTraits.VAMPIRE)) {
                int ticks = (int)(WitcheryConfigOptions.vampireDeathItemKeepAliveMins * 1200.0);

                EntityItem item;
                for(Iterator<EntityItem> itemIterator = event.getDrops().iterator(); itemIterator.hasNext(); item.lifespan = ticks) {
                    item = itemIterator.next();
                }
            }
        }

        ci.cancel();
    }

    /** Injects before the call to getEntityAttribute, when it is already checked that entity is of EntitySummonedUndead
     * type, has less than 50 health and is enslaved by player. Check which entity has been buffed and unlock progress **/
    @Inject(method = "onEntityInteract", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/minecraft/entity/EntityLiving;getEntityAttribute(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;"))
    private static void unlockProgressSpectralBeingsHealthBuff(PlayerInteractEvent.EntityInteract event, CallbackInfo ci) {
        EntityPlayer player = event.getEntityPlayer();
        Entity entity = event.getTarget();
        Class<? extends EntitySummonedUndead> spectralBeingClass;

        if (player != null) {
            if (entity instanceof EntitySpectre) {
                spectralBeingClass = EntitySpectre.class;
            }
            else if (entity instanceof EntityPoltergeist) {
                spectralBeingClass = EntityPoltergeist.class;
            }
            else {
                return;
            }

            ProgressUtils.unlockProgress(player,
                    ProgressUtils.getCreatureSecret(spectralBeingClass, "health_buff"),
                    WitcheryProgressEvent.EProgressTriggerActivity.CREATURE_INTERACT.activityTrigger
            );
        }
    }

}
