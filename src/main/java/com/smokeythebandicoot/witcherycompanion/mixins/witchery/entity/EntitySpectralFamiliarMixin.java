package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.api.SpectralFamiliarApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectralfamiliar.IEntitySpectralFamiliarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.LootTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Mixins:
 * [Tweak] Tweak own loot table
 * [Tweak] CraftTweaker compat for items searching (partial, also requires EntityAIFamiliarFindDiamondsMixin)
 */
@Mixin(EntitySpectralFamiliar.class)
public abstract class EntitySpectralFamiliarMixin extends EntityOcelot implements IEntitySpectralFamiliarAccessor {

    @Unique
    private static final DataParameter<ItemStack> SNIFFED_ITEM =
            EntityDataManager.createKey(EntitySpectralFamiliar.class, DataSerializers.ITEM_STACK);

    @Shadow(remap = false)
    private int searches;

    @Shadow(remap = false)
    public abstract void setItemIDToFind(int itemID);

    @Shadow(remap = false)
    protected abstract int hasOre(ItemStack item);


    private EntitySpectralFamiliarMixin(World worldIn) {
        super(worldIn);
    }


    /** ========== TWEAK SNIFFING CRAFTTWEAKER COMPAT ========== **/

    @Inject(method = "entityInit", remap = true, at = @At("RETURN"))
    private void injectNewDataparameters(CallbackInfo ci) {
        if (!EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat)
            return;
        this.dataManager.register(SNIFFED_ITEM, ItemStack.EMPTY);
    }

    @Inject(method = "writeEntityToNBT", remap = true, at = @At("RETURN"))
    private void writeSniffedItemToNBT(NBTTagCompound nbtTag, CallbackInfo ci) {
        if (!EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat)
            return;
        // The original method also writes ItemToFind. Kept for compatibility
        nbtTag.setString("SniffedItemName", this.dataManager.get(SNIFFED_ITEM).getItem().getRegistryName().toString());
        nbtTag.setInteger("SniffedItemMeta", this.dataManager.get(SNIFFED_ITEM).getMetadata());
    }

    @Inject(method = "readEntityFromNBT", remap = true, at = @At("RETURN"))
    private void readSniffedItemFromNBT(NBTTagCompound nbtTag, CallbackInfo ci) {
        if (!EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat)
            return;
        // The original method also writes ItemToFind. Kept for compatibility
        String itemId = nbtTag.getString("SniffedItemName");
        int itemMeta = nbtTag.getInteger("SniffedItemMeta");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item != null) {
            this.dataManager.set(SNIFFED_ITEM, new ItemStack(item, 1, itemMeta));
        }
    }

    /** This Mixin injects into getDepthToFind to always search from bottom of the world up to entity position,
     * instead of having hard-coded depths where to look for ores **/
    @Inject(method = "getDepthToFind", remap = false, at = @At("HEAD"), cancellable = true)
    private void searchAllDepth(CallbackInfoReturnable<Integer> cir) {
        if (EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {
            cir.setReturnValue(this.getPosition().getY());
        }
    }

    /** Refactors the logic of processInteract to inject tweaks and CrT compat **/
    @Inject(method = "processInteract", remap = true, at = @At("HEAD"), cancellable = true)
    private void processInteractToSniff(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (this.isTamed() && this.isOwner(player) && !this.world.isRemote) {
            // Get held item
            ItemStack item = player.getHeldItemMainhand();

            // CraftTweaker compat is enabled: call the API
            if (EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {
                IBlockState ore = SpectralFamiliarApi.getOre(item);

                // Sets item to sniff and proceeds with searches
                if (ore != null) {
                    ItemStack sniffed = item.copy();
                    // Clear count and tag
                    sniffed.setCount(1);
                    sniffed.setTagCompound(null);
                    this.dataManager.set(SNIFFED_ITEM, sniffed);
                }
                // No ore, simply toggle sit and exit
                else {
                    this.aiSit.setSitting(!this.isSitting());
                    cir.setReturnValue(true);
                    return;
                }
            }

            // CraftTweaker compat is not enabled: proceed with Witchery logic
            else {
                int itemIdToFind = this.hasOre(item);

                // Sets itemIdToFind and proceeds with searches
                if (itemIdToFind != -1) {
                    this.setItemIDToFind(itemIdToFind);
                }
                // No ore, simply toggle sit and exit
                else {
                    this.aiSit.setSitting(!this.isSitting());
                    cir.setReturnValue(true);
                    return;
                }
            }

            ++this.searches;
            item.shrink(1);

            double despawnChance = this.witcherycompanion$getChance();

            // Despawn if too many searches or just unlucky
            if (this.searches > EntityTweaks.spectralFamiliar_tweakMaxSearches || this.world.rand.nextDouble() >= despawnChance) {
                this.playSound(
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT,
                        0.5F,
                        0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F)
                );
                this.world.setEntityState(this, (byte) 5);
                this.setDead();
                cir.setReturnValue(true);
                return;
            }

            // Play DING sound
            player.world.playSound(
                    null, player.getPosition(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    this.getSoundCategory(),
                    0.5F,
                    0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F)
            );
        }
        cir.setReturnValue(true);
    }

    @Unique
    private double witcherycompanion$getChance() {
        int l = EntityTweaks.spectralFamiliar_tweakDespawnChances.length;
        // Empty configuration, return 0.0 (never despawn)
        if (l == 0) {
            return 0.0;
        }
        return EntityTweaks.spectralFamiliar_tweakDespawnChances[Math.min(this.searches - 1, l)];
    }

    @Override
    public ItemStack witcherycompanion$accessor$getSniffedItem() {
        return this.dataManager.get(SNIFFED_ITEM);
    }

    @Override
    public void witcherycompanion$accessor$setSniffedItem(ItemStack stack) {
        this.dataManager.set(SNIFFED_ITEM, stack == null ? ItemStack.EMPTY : stack);
    }


    /** ========== TWEAK LOOT TABLE ========== **/

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    private void dropFewItems(boolean par1, int par2, CallbackInfo ci) {
        if (LootTweaks.spectralFamiliar_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return LootTweaks.spectralFamiliar_tweakLootTable ? LootTables.SPECTRAL_FAMILIAR : null;
    }
}
