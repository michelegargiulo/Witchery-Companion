package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.DimensionTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.DimensionListUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockCircle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Circle magic can be disabled in a configurable list of Dimensions (or enabled only in a list of Dimensions),
    like Witchery already does for the Spirit World
 */
@Mixin(BlockCircle.class)
public abstract class BlockCircleMixin {

    /** Runs right where Witchery checks if the area is nullified or the Dimension is the Spirit World, so that an
     * active ritual can still be deactivated, and the same message and sound are used. Witchery does not use its
     * own nullified branch for us because it sends the message to the player, which is null when the circle is
     * activated by redstone. This is only reached server-side. Applies to every circle activation: right click,
     * Seer Stone coven summon and redstone */
    @Inject(method = "activateBlock", remap = false, cancellable = true,
            at = @At(value = "INVOKE", remap = false, ordinal = 0,
                    target = "Lnet/msrandom/witchery/common/PowerSources;instance()Lnet/msrandom/witchery/common/PowerSources;"))
    private void witcherycompanion$disableCircleMagicInDimensions(World world, BlockPos pos, EntityPlayer player,
                                                                   boolean summonCoven, CallbackInfo ci) {
        if (DimensionListUtils.isBlocked(world.provider.getDimension(),
                DimensionTweaks.circleMagic_tweakDimensionList,
                DimensionTweaks.circleMagic_tweakDimensionListIsWhitelist)) {

            if (player != null) {
                player.sendMessage(new TextComponentTranslation("rite.witchery.area_nullified")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
            }
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.BLOCKS, 0.5f,
                    0.4f / (world.rand.nextFloat() * 0.4f + 0.8f));
            ci.cancel();
        }
    }

}
