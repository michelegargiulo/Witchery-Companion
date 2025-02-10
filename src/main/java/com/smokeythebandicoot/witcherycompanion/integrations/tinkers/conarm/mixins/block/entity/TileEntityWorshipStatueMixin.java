package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.entity.TileEntityWorshipStatue;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mixin(TileEntityWorshipStatue.class)
public abstract class TileEntityWorshipStatueMixin extends WitcheryTileEntity {

    @Shadow(remap = false)
    public abstract EntityPlayer getOwner();

    /** This Mixin keeps count of Armor with Goblin's Favor trait. Every worn piece with this trait counts as an additional
     * worshipped Goblin. Full gear counts as 5 **/
    @WrapOperation(method = "update", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityWorshipStatue;updateWorshippersAndGetLevel()I"))
    private int countBlessedPieces(TileEntityWorshipStatue instance, Operation<Integer> original) {
        int originalCount = original.call(instance);
        int additionalCount = 0;
        EntityPlayer player = this.getOwner();

        // Check Armor of the statue's owner
        if (player != null) {
            // Check every worn piece
            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] {
                    EntityEquipmentSlot.HEAD,
                    EntityEquipmentSlot.CHEST,
                    EntityEquipmentSlot.LEGS,
                    EntityEquipmentSlot.FEET,
            }) {
                ItemStack wornStack = player.getItemStackFromSlot(slot);
                if (TinkerUtil.hasTrait(wornStack.getTagCompound(), Integration.TRAIT_GOBLINS_FAVOR.identifier)) {
                    additionalCount++;
                }
            }

            // If all pieces have it, have an additional goblin
            if (additionalCount == 4) {
                additionalCount = 5;
            }
        }
        return originalCount + additionalCount;
    }

}
