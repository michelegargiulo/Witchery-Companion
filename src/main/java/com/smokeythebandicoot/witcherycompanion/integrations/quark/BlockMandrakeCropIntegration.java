package com.smokeythebandicoot.witcherycompanion.integrations.quark;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.block.BlockMandrakeCrop;
import net.msrandom.witchery.entity.monster.EntityMandrake;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.List;

/**
 Integration:
 [Bugfix] Fix Mandrake entity not spawning when right-click harvesting feature is implemented by Quark
 */
@Mod.EventBusSubscriber
public class BlockMandrakeCropIntegration {

    public static BlockMandrakeCropIntegration INSTANCE = new BlockMandrakeCropIntegration();

    private BlockMandrakeCropIntegration() { }

    @SubscribeEvent
    public void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        IBlockState state = event.getState();
        List<ItemStack> drops = event.getDrops();

        if (state.getBlock() != WitcheryBlocks.MANDRAKE_SEEDS) return;
        if (event.getWorld().isRemote) return;

        // If mandrake has been spawned, remove the mandrake seeds and mandrake root drops
        // as the Mandrake Entity can drop them instead
        if (checkSpawnMandrake(
                state,
                event.getWorld(),
                event.getHarvester(),
                event.getPos()
        )) {
            removeCropDrops(drops);
        }
    }

    private static void removeCropDrops(List<ItemStack> drops) {
        drops.removeIf(stack ->
                stack.getItem() == WitcheryIngredientItems.MANDRAKE_ROOT ||
                stack.getItem() == WitcheryBlocks.MANDRAKE_SEEDS.getCrop());
    }

    public static boolean checkSpawnMandrake(IBlockState state, World world, EntityPlayer player, BlockPos pos) {
        // Check if it is a Mandrake Crop
        if (!(state.getBlock() instanceof BlockMandrakeCrop))
            return false;

        // Check if it is at max age
        BlockMandrakeCrop cropBlock = (BlockMandrakeCrop) state.getBlock();
        if (!cropBlock.isMaxAge(state) || !ModConfig.PatchesConfiguration.BlockTweaks.mandrakeCrop_fixMandrakeSpawningNotMature)
            return false;

        // Check if world is remote or difficulty is peaceful
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL)
            return false;

        // Depending on daytime and chance, spawn the mandrake
        if (world.rand.nextDouble() < (world.provider.isDaytime() ? 0.1 : 0.9)) {
            return spawnMandrake(world, pos, cropBlock, player);
        }

        return false;
    }

    public static boolean spawnMandrake(World world, BlockPos pos, BlockMandrakeCrop cropBlock, EntityPlayer player) {
        EntityMandrake mandrake = new EntityMandrake(world);
        mandrake.setLocationAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
        boolean result = world.spawnEntity(mandrake);
        WitcheryUtils.addNewParticles(world, EnumParticleTypes.EXPLOSION_NORMAL, mandrake.posX, mandrake.posY, mandrake.posZ, 0.0, 0, 0.5, 0.0);
        player.addStat(StatList.getBlockStats(cropBlock));
        player.addExhaustion(0.005F);
        return result;
    }

}
