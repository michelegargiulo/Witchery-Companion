package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.infusion;


import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.OverworldInfusionApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.infusion.OverworldInfusion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Integration] Overworld Infusion can knockback players if they have metal baubles
 */
@Mixin(OverworldInfusion.class)
public abstract class OverworldInfusionMixin {

    /** This Mixin hijacks the getItemStackFromSlot to return a totally different ItemStack. If the entity has a metal
     * bauble equipped, it returns it as if it was equipped in the armor slot. Once the bauble considered metallic,
     * the isMatch func after this mixin will match as it is the same used by OverworldInfusionApi.isMetalItem **/
    @WrapOperation(method = "onLeftClickEntity", remap = false, at = @At(value = "INVOKE", remap = true, ordinal = 0,
            target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack injectMetalBaubles(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot, Operation<ItemStack> original) {
        if (ModConfig.PatchesConfiguration.InfusionTweaks.overworldInfusion_tweakEnableCrafttweakerCompat) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) instance;

                // Iterates over all the worn baubles. Should be compatible with Baubles forks
                IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);
                for (int slotIndex = 0; slotIndex < baublesHandler.getSlots(); slotIndex++) {
                    ItemStack bauble = baublesHandler.getStackInSlot(slotIndex);
                    if (OverworldInfusionApi.isMetalItem(bauble)) {
                        return bauble;
                    }
                }
            }
        }
        return original.call(instance, entityEquipmentSlot);
    }

}
