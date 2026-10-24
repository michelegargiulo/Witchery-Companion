package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.tools.Kama;

/**
 * Tinkers equivalent of the Witchery Boline: a Kama Head and a Tool Rod. It is a Kama (so it harvests like one) that
 * additionally does everything the Boline does: collecting blocks such as Webs and Plant Mines, shearing creatures and
 * the Boline's mining speed. These are delegated to the Witchery Boline, so they stay identical. Where the Boline is
 * required (rites, Fetish, Goblet, Treefyd) it is handled by Hegemony. When it breaks, it loses all of it
 **/
public class ToolBoline extends Kama {

    public ToolBoline() {
        super(PartMaterialType.handle(TinkerTools.toolRod),
                PartMaterialType.head(TinkerTools.kamaHead));
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!ToolHelper.isBroken(stack) && WitcheryGeneralItems.BOLINE.onBlockStartBreak(stack, pos, player)) {
            return true;
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!ToolHelper.isBroken(stack) && WitcheryGeneralItems.BOLINE.itemInteractionForEntity(stack, player, target, hand)) {
            return true;
        }
        return super.itemInteractionForEntity(stack, player, target, hand);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        if (!ToolHelper.isBroken(stack)) {
            speed = Math.max(speed, WitcheryGeneralItems.BOLINE.getDestroySpeed(new ItemStack(WitcheryGeneralItems.BOLINE), state));
        }
        return speed;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return super.canHarvestBlock(state, stack) ||
                (!ToolHelper.isBroken(stack) && WitcheryGeneralItems.BOLINE.canHarvestBlock(state, stack));
    }
}
