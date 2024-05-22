package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes.AltarHandler;
import com.smokeythebandicoot.witcherycompanion.utils.AltarPowerSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.WitcheryWoodTypes;
import net.msrandom.witchery.util.BlockUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 Mixins:
 [Bugfix] Fixes PowerSources being lost upon world load/reload, requiring them to be interacted or
    broken and re-placed to be registered correctly again
 [Tweak] Great performance gain by caching Altar PowerSourceTable
 */
@Mixin(value = TileEntityAltar.class)
public abstract class TileEntityAltarMixin extends WitcheryTileEntity implements IPowerSource {

    @Shadow(remap = false)
    private float power;

    @Shadow(remap = false)
    public abstract void updatePower();

    @Shadow(remap = false)
    private long lastPowerUpdate;

    @Shadow(remap = false)
    private int enhancementLevel;

    @Shadow(remap = false)
    private Set<Block> extraNatureIDs;

    @Shadow(remap = false)
    private float maxPower;

    @Shadow(remap = false) @Final
    private static int SCAN_DISTANCE;

    @Unique
    private static HashMap<Block, AltarPowerSource> witchery_Patcher$powerObjectTable = null;


    /**
     * Skips a world.isRemote check that makes the Altar not give power sometimes
     * @param power
     * @param cir
     */
    @Inject(method = "consumePower", at = @At("HEAD"), remap = false, cancellable = true)
    public void WPconsumePower(float power, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.altar_fixPowerSourcePersistency) {
            if (this.power >= power) {
                this.power -= power;
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Injected method that gets run when the TileEntity is first loaded in the world
     * Registers the power source and updates the power
     */
    public void onLoad() {
        if (!world.isRemote && ModConfig.PatchesConfiguration.BlockTweaks.altar_fixPowerSourcePersistency) {
            PowerSources.instance().registerPowerSource((TileEntityAltar)(Object)this);
            updatePower();
        }
    }

    /**
     * Rewrites the Power updating logic to factor in CraftTweaker integration and caching to improve performance by caching
     */
    @Inject(method = "updatePower(Z)V", remap = false, cancellable = true, at = @At("HEAD"))
    private void WPupdatePowerCached(boolean throttle, CallbackInfo ci) {
        if (!this.world.isRemote && (!throttle || this.ticks - this.lastPowerUpdate <= 0L || this.ticks - this.lastPowerUpdate > 100L)) {
            this.lastPowerUpdate = this.ticks;
            witchery_Patcher$powerObjectTable = witchery_Patcher$updatePowerMap();
            witchery_Patcher$scanSurroundings();
        }
        ci.cancel();
    }

    @Unique
    private HashMap<Block, AltarPowerSource> witchery_Patcher$updatePowerMap() {
        if (witchery_Patcher$powerObjectTable == null || witchery_Patcher$powerObjectTable.isEmpty()) {

            witchery_Patcher$powerObjectTable = new HashMap<>();

            // Add Custom Crafttweaker-defined registers
            for (Block ctBlock : AltarHandler.addingMap.keySet()) {
                AltarPowerSource powerSource = AltarHandler.addingMap.get(ctBlock);
                witchery_Patcher$addToMap(ctBlock, powerSource.getFactor(), powerSource.getLimit());
            }

            // Add default Witchery blocks
            Iterator var3 = OreDictionary.getOres("treeSapling").iterator();

            ItemStack blockItem;
            Block block;
            while(var3.hasNext()) {
                blockItem = (ItemStack)var3.next();
                block = Block.getBlockFromItem(blockItem.getItem());
                this.witchery_Patcher$addToMap(block, 4, 20);
            }

            var3 = OreDictionary.getOres("logWood").iterator();

            while(var3.hasNext()) {
                blockItem = (ItemStack)var3.next();
                block = Block.getBlockFromItem(blockItem.getItem());
                this.witchery_Patcher$addToMap(block, 2, 50);
            }

            var3 = OreDictionary.getOres("treeLeaves").iterator();

            while(var3.hasNext()) {
                blockItem = (ItemStack)var3.next();
                block = Block.getBlockFromItem(blockItem.getItem());
                this.witchery_Patcher$addToMap(block, 3, 100);
            }

            this.witchery_Patcher$addToMap(Blocks.GRASS, 2, 80);
            this.witchery_Patcher$addToMap(Blocks.DIRT, 1, 80);
            this.witchery_Patcher$addToMap(Blocks.FARMLAND, 1, 100);
            this.witchery_Patcher$addToMap(Blocks.TALLGRASS, 3, 50);
            this.witchery_Patcher$addToMap(Blocks.YELLOW_FLOWER, 4, 30);
            this.witchery_Patcher$addToMap(Blocks.RED_FLOWER, 4, 30);
            this.witchery_Patcher$addToMap(Blocks.WHEAT, 4, 20);
            this.witchery_Patcher$addToMap(Blocks.WATER, 1, 50);
            this.witchery_Patcher$addToMap(Blocks.RED_MUSHROOM, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.BROWN_MUSHROOM, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.CACTUS, 3, 50);
            this.witchery_Patcher$addToMap(Blocks.REEDS, 3, 50);
            this.witchery_Patcher$addToMap(Blocks.PUMPKIN, 4, 20);
            this.witchery_Patcher$addToMap(Blocks.PUMPKIN_STEM, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.BROWN_MUSHROOM_BLOCK, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.RED_MUSHROOM_BLOCK, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.MELON_BLOCK, 4, 20);
            this.witchery_Patcher$addToMap(Blocks.MELON_STEM, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.VINE, 2, 50);
            this.witchery_Patcher$addToMap(Blocks.MYCELIUM, 1, 80);
            this.witchery_Patcher$addToMap(Blocks.DRAGON_EGG, 250, 1);
            this.witchery_Patcher$addToMap(WitcheryBlocks.DEMON_HEART, 40, 2);
            this.witchery_Patcher$addToMap(Blocks.COCOA, 3, 20);
            this.witchery_Patcher$addToMap(Blocks.CARROTS, 4, 20);
            this.witchery_Patcher$addToMap(Blocks.POTATOES, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.BELLADONNA_SEEDS, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.MANDRAKE_SEEDS, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.ARTICHOKE_SEEDS, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.SNOWBELL_SEEDS, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.EMBER_MOSS, 4, 20);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.ROWAN.getLeaves(), 4, 50);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.ALDER.getLeaves(), 4, 50);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.HAWTHORN.getLeaves(), 4, 50);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.ROWAN.getLog(), 3, 100);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.ALDER.getLog(), 3, 100);
            this.witchery_Patcher$addToMap(WitcheryWoodTypes.HAWTHORN.getLog(), 3, 100);
            this.witchery_Patcher$addToMap(WitcheryBlocks.SPANISH_MOSS, 3, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.GLINT_WEED, 2, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.EMPTY_CRITTER_SNARE, 2, 10);
            this.witchery_Patcher$addToMap(WitcheryBlocks.BLOOD_POPPY, 2, 10);
            this.witchery_Patcher$addToMap(WitcheryBlocks.GRASSPER, 2, 10);
            this.witchery_Patcher$addToMap(WitcheryBlocks.WISPY_COTTON, 3, 20);
            this.witchery_Patcher$addToMap(WitcheryBlocks.INFINITY_EGG, 1000, 1);
            Block block2;

            if (this.extraNatureIDs == null) {
                this.extraNatureIDs = new HashSet<>();
                var3 = ForgeRegistries.BLOCKS.iterator();

                label90:
                while(true) {
                    do {
                        if (!var3.hasNext()) {
                            break label90;
                        }

                        block2 = (Block)var3.next();
                    } while(!(block2 instanceof BlockFlower) && !(block2 instanceof BlockCrops));

                    if (!witchery_Patcher$powerObjectTable.containsKey(block2)) {
                        this.extraNatureIDs.add(block2);
                    }
                }
            }

            var3 = this.extraNatureIDs.iterator();

            while(var3.hasNext()) {
                block2 = (Block)var3.next();
                this.witchery_Patcher$addToMap(block2, 2, 4);
            }

            // Remove Crafttweaker-defined unregisters
            for (Block ctBlock : AltarHandler.removalList) {
                witchery_Patcher$powerObjectTable.remove(ctBlock);
            }
        }

        return witchery_Patcher$powerObjectTable;
    }

    @Unique
    private void witchery_Patcher$scanSurroundings() {

        HashMap<Block, Integer> sourceCount = new HashMap<>();

        for(int y = this.getPos().getY() - SCAN_DISTANCE; y <= this.getPos().getY() + SCAN_DISTANCE; ++y) {
            for(int z = this.getPos().getZ() + SCAN_DISTANCE; z >= this.getPos().getZ() - SCAN_DISTANCE; --z) {
                for(int x = this.getPos().getX() - SCAN_DISTANCE; x <= this.getPos().getX() + SCAN_DISTANCE; ++x) {

                    Block block3 = this.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    AltarPowerSource source = witchery_Patcher$powerObjectTable.get(block3);

                    if (source != null) {
                        sourceCount.merge(block3, 1, (a, b) -> Math.min(a + b, source.getLimit()));
                    }

                }
            }
        }

        float newMax = 0.0F;

        for (Block foundBlock : sourceCount.keySet()) {
            AltarPowerSource altarPowerSource = witchery_Patcher$powerObjectTable.get(foundBlock);
            newMax += Math.min(sourceCount.get(foundBlock), altarPowerSource.getLimit()) * altarPowerSource.getFactor();
        }

        if (newMax != this.maxPower) {
            this.maxPower = newMax;
            BlockUtil.notifyBlockUpdate(this.world, this.getPos());
        }
    }

    @Unique
    private void witchery_Patcher$addToMap(Block block, int factor, int limit) {
        AltarPowerSource source = new AltarPowerSource(factor, (int)(Math.max((double)this.enhancementLevel * 1.18, 1.0) * (double)limit));
        witchery_Patcher$powerObjectTable.put(block, source);
    }


}
