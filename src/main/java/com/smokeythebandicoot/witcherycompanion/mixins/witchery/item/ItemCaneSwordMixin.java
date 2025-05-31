package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.msrandom.witchery.item.ItemCaneSword;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Fix Cane Sword regaining full durability upon sheathing/unsheating
 */
@Mixin(ItemCaneSword.class)
public abstract class ItemCaneSwordMixin extends ItemSword {

    private ItemCaneSwordMixin(ToolMaterial material) {
        super(material);
    }

    /** This Mixin will return a new ActionResult with a new ItemStack with the correct damage **/
    @Inject(method = "onItemRightClick", remap = true, cancellable = true, at = @At(value = "RETURN"))
    private void preserveDamageOnUnsheating(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        if (ItemTweaks.caneSword_fixDamageOnSheathe) {
            int damage = player.getHeldItem(hand).getItemDamage();
            ItemStack stack = cir.getReturnValue().getResult();
            stack.setItemDamage(damage);
            cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, stack));
        }
    }

}
