package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockPlacedItem;
import net.msrandom.witchery.init.WitcheryBlocks;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

/**
 * Tinkers equivalent of the Witchery Arthana: a Tool Rod and a Knife Blade. Like the original, it can be placed on
 * the top of an Altar. Everything else (extra drops, altar artifact, rites...) is handled by Hegemony, so the effects
 * are exactly the ones of the Arthana. When it breaks, it loses all of them
 **/
public class ToolArthana extends SwordCore {

    public static final float DURABILITY_MODIFIER = 0.8f;

    public ToolArthana() {
        super(PartMaterialType.handle(TinkerTools.toolRod),
                PartMaterialType.head(TinkerTools.knifeBlade));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 0.5f;
    }

    @Override
    public double attackSpeed() {
        return 1.8d;
    }

    @Override
    protected ToolNBT buildTagData(List<Material> materials) {
        ToolNBT data = buildDefaultTag(materials);
        data.durability *= DURABILITY_MODIFIER;
        return data;
    }

    /** Same as the Witchery Arthana: placed on top of an Altar. A broken tool cannot be placed **/
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!ToolHelper.isBroken(stack) &&
                world.getBlockState(pos).getBlock() == WitcheryBlocks.ALTAR &&
                facing == EnumFacing.UP &&
                world.isAirBlock(pos.up())) {
            BlockPlacedItem.placeInWorld(hand, player, world, pos.up());
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}
