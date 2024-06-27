package com.smokeythebandicoot.witcherycompanion.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.item.WitcheryPoppetItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 Mixins:
 [Tweak] Disable PvP elements of Witchery Poppets, while keeping PvE
 */
@Mixin(WitcheryPoppetItems.ItemPoppet.class)
public abstract class WitcheryPoppetItems_ItemPoppetMixin {

    @Unique
    private boolean witchery_Patcher$boundToPlayer = false;

    @Unique
    private boolean witchery_Patcher$boundChecked = false;

    @WrapOperation(method = "onPlayerStoppedUsing", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/item/ItemTaglockKit;getBoundEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/entity/EntityLivingBase;"))
    public EntityLivingBase storeIsBoundToPlayer(World world, ItemStack stack, int index, Operation<EntityLivingBase> original) {
        EntityLivingBase target = original.call(world, stack, index);
        if (ItemTweaks.poppetItem_tweakDisablePvP || ItemTweaks.poppetItem_tweakDisablePvE) {
            witchery_Patcher$boundToPlayer = target instanceof EntityPlayerMP;
            witchery_Patcher$boundChecked = true;
        }
        return target;
    }

    @Inject(method = "onPlayerStoppedUsing", remap = true, cancellable = true, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/item/ItemTaglockKit;getBoundEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/entity/EntityLivingBase;"))
    public void cancelIfBoundToPlayer(ItemStack itemstack, World world, EntityLivingBase livingBase, int ticks, CallbackInfo ci) {
        if (!witchery_Patcher$boundChecked) return;
        if ((ItemTweaks.poppetItem_tweakDisablePvP && witchery_Patcher$boundToPlayer) ||
                (ItemTweaks.poppetItem_tweakDisablePvE && !witchery_Patcher$boundToPlayer)) {
            ci.cancel();
        }
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "addInformation", remap = true, at = @At("TAIL"))
    public void addDisabledPvXTooltip(ItemStack stack, World worldIn, List<String> list, ITooltipFlag advTooltips, CallbackInfo ci) {
        // If Poppet is not Vampiric or VooDoo, then it cannot damage other players/entities
        if (stack.getItem().getRegistryName() == null ||
            (!stack.getItem().getRegistryName().equals(WitcheryPoppetItems.VOODOO.getRegistryName()) &&
            !stack.getItem().getRegistryName().equals(WitcheryPoppetItems.VAMPIRIC.getRegistryName()))) {
            return;
        }

        // In case of Vampiric or VooDoo, add a tooltip
        if (ItemTweaks.poppetItem_tweakAddTargetRestrictionTooltip) {
            if (ItemTweaks.poppetItem_tweakDisablePvP) {
                list.add(new TextComponentTranslation("witcherycompanion.poppetitem.restrictiontooltip.pvp").getFormattedText());
            }
            if (ItemTweaks.poppetItem_tweakDisablePvE) {
                list.add(new TextComponentTranslation("witcherycompanion.poppetitem.restrictiontooltip.pve").getFormattedText());
            }
        }
    }
}
