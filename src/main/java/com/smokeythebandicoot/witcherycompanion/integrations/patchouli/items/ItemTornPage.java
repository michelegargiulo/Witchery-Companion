package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.items;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemTornPage extends Item {

    public static final String VAMPIRE_PAGE_PROGRESS =
            WitcheryCompanion.prefix("observations/immortal/level_");


    public ItemTornPage() {
        this.setCreativeTab(WitcheryGeneralItems.TAB);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);

            // Retrieves player progress and unlocks a new page of the book
            if (stack.getItem() == this) {
                IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
                if (progress != null) {
                    int currentProgress = 1;
                    while (currentProgress <= 10) {
                        String progressKey = VAMPIRE_PAGE_PROGRESS + currentProgress;

                        if (!progress.hasProgress(progressKey)) {
                            if ((ModConfig.IntegrationConfigurations.PatchouliIntegration.common_harderImmortalPages ||
                                    world.rand.nextInt(10) + 1 > currentProgress)) {
                                ProgressUtils.unlockProgress(player, progressKey,
                                        WitcheryProgressEvent.EProgressTriggerActivity.USE_VAMPIRE_PAGE.activityTrigger);

                                //PatchouliApiIntegration.immortalBookReloader.reloadFlags();

                                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                            }

                            if (!player.isCreative()) {
                                stack.shrink(1);
                            }
                        }

                        currentProgress += 1;
                    }
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("witcherycompanion.tooltip.torn_page.lore"));
        tooltip.add(I18n.format("witcherycompanion.tooltip.torn_page.usage"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
