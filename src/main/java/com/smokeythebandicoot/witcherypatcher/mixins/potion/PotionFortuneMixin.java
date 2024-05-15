package com.smokeythebandicoot.witcherypatcher.mixins.potion;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.msrandom.witchery.potion.PotionFortune;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Bugfix] Fix Fortune Brew not working because of a wrong (inverted) TileEntity == null check. Basically,
 * it would only have worked if the block had a TileEntity, instead of not having it
 */
@Mixin(value = PotionFortune.class)
public class PotionFortuneMixin {

    @Inject(method = "onHarvestDrops", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPfixTileEntityInvertedCheck(World world, BlockEvent.HarvestDropsEvent event, int amplifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.PotionTweaks.fortunePotion_fixNoEffect) {
            if (!event.getWorld().isRemote && !event.isSilkTouching() && world.getTileEntity(event.getPos()) == null && !event.getDrops().isEmpty()) {
                NonNullList<ItemStack> drops = NonNullList.create();
                event.getState().getBlock().getDrops(drops, event.getWorld(), event.getPos(), event.getState(), event.getFortuneLevel() + (amplifier > 2 ? 2 : 1));
                event.getDrops().clear();
                event.getDrops().addAll(drops);
            }
            ci.cancel();
        }
    }

}
