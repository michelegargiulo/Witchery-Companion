package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.msrandom.witchery.block.BlockChalice;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.WitcheryWoodTypes;

import java.util.*;
import java.util.function.Supplier;

public class AltarApi {

    // Power Sources
    public static final HashMap<IBlockState, AltarPowerSource> validStates = initStateList();
    public static final HashMap<Block, AltarPowerSource> validBlocks = initBlockList();

    // Altar Boosters
    private static final HashMap<IBlockState, AltarBoosterFunc> skullBoosters = new HashMap<>();
    private static final HashMap<IBlockState, AltarBoosterFunc> candleBoosters = new HashMap<>();
    private static final HashMap<IBlockState, AltarBoosterFunc> chaliceBoosters = new HashMap<>();


    /** INIT POWER SOURCES **/
    private static HashMap<Block, AltarPowerSource> initBlockList() {
        HashMap<Block, AltarPowerSource> blocks = new HashMap<>();

        blocks.put(Blocks.GRASS, new AltarPowerSource(2, 80));
        blocks.put(Blocks.DIRT, new AltarPowerSource(1, 80));
        blocks.put(Blocks.FARMLAND, new AltarPowerSource(1, 100));
        blocks.put(Blocks.TALLGRASS, new AltarPowerSource(3, 50));
        blocks.put(Blocks.YELLOW_FLOWER, new AltarPowerSource(4, 30));
        blocks.put(Blocks.RED_FLOWER, new AltarPowerSource(4, 30));
        blocks.put(Blocks.WHEAT, new AltarPowerSource(4, 20, Ingredient.fromItem(Items.WHEAT_SEEDS)));
        blocks.put(Blocks.WATER, new AltarPowerSource(1, 50, Ingredient.fromItem(Items.WATER_BUCKET)));
        blocks.put(Blocks.RED_MUSHROOM, new AltarPowerSource(3, 20));
        blocks.put(Blocks.BROWN_MUSHROOM, new AltarPowerSource(3, 20));
        blocks.put(Blocks.CACTUS, new AltarPowerSource(3, 50));
        blocks.put(Blocks.REEDS, new AltarPowerSource(3, 50, Ingredient.fromItem(Items.REEDS)));
        blocks.put(Blocks.PUMPKIN, new AltarPowerSource(4, 20));
        blocks.put(Blocks.PUMPKIN_STEM, new AltarPowerSource(3, 20, Ingredient.fromItem(Items.PUMPKIN_SEEDS)));
        blocks.put(Blocks.BROWN_MUSHROOM_BLOCK, new AltarPowerSource(3, 20, Ingredient.fromStacks(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK))));
        blocks.put(Blocks.RED_MUSHROOM_BLOCK, new AltarPowerSource(3, 20, Ingredient.fromStacks(new ItemStack(Blocks.RED_MUSHROOM_BLOCK))));
        blocks.put(Blocks.MELON_BLOCK, new AltarPowerSource(4, 20));
        blocks.put(Blocks.MELON_STEM, new AltarPowerSource(3, 20, Ingredient.fromItem(Items.MELON_SEEDS)));
        blocks.put(Blocks.VINE, new AltarPowerSource(2, 50));
        blocks.put(Blocks.MYCELIUM, new AltarPowerSource(1, 80));
        blocks.put(Blocks.COCOA, new AltarPowerSource(3, 20));
        blocks.put(Blocks.CARROTS, new AltarPowerSource(4, 20, Ingredient.fromItem(Items.CARROT)));
        blocks.put(Blocks.POTATOES, new AltarPowerSource(4, 20, Ingredient.fromItem(Items.POTATO)));
        blocks.put(Blocks.DRAGON_EGG, new AltarPowerSource(250, 1));
        blocks.put(WitcheryBlocks.DEMON_HEART, new AltarPowerSource(40, 2));
        blocks.put(WitcheryBlocks.BELLADONNA_SEEDS, new AltarPowerSource(4, 20));
        blocks.put(WitcheryBlocks.MANDRAKE_SEEDS, new AltarPowerSource(4, 20));
        blocks.put(WitcheryBlocks.ARTICHOKE_SEEDS, new AltarPowerSource(4, 20));
        blocks.put(WitcheryBlocks.SNOWBELL_SEEDS, new AltarPowerSource(4, 20));
        blocks.put(WitcheryBlocks.SPANISH_MOSS, new AltarPowerSource(3, 20));
        blocks.put(WitcheryBlocks.GLINT_WEED, new AltarPowerSource(2, 20));
        blocks.put(WitcheryBlocks.EMPTY_CRITTER_SNARE, new AltarPowerSource(2, 10));
        blocks.put(WitcheryBlocks.BLOOD_POPPY, new AltarPowerSource(2, 10));
        blocks.put(WitcheryBlocks.GRASSPER, new AltarPowerSource(2, 10));
        blocks.put(WitcheryBlocks.WISPY_COTTON, new AltarPowerSource(3, 20));
        blocks.put(WitcheryBlocks.INFINITY_EGG, new AltarPowerSource(1000, 1));
        blocks.put(WitcheryBlocks.EMBER_MOSS, new AltarPowerSource(4, 20));
        blocks.put(WitcheryWoodTypes.ROWAN.getLeaves(), new AltarPowerSource(4, 50));
        blocks.put(WitcheryWoodTypes.ALDER.getLeaves(), new AltarPowerSource(4, 50));
        blocks.put(WitcheryWoodTypes.HAWTHORN.getLeaves(), new AltarPowerSource(4, 50));
        blocks.put(WitcheryWoodTypes.ROWAN.getLog(), new AltarPowerSource(3, 100));
        blocks.put(WitcheryWoodTypes.ALDER.getLog(), new AltarPowerSource(3, 100));
        blocks.put(WitcheryWoodTypes.HAWTHORN.getLog(), new AltarPowerSource(3, 100));

        return blocks;

    }

    private static HashMap<IBlockState, AltarPowerSource> initStateList() {
        return new HashMap<>();
    }

    /** INIT ALTAR BOOSTERS **/
    private static void initSkullBoosters() {
        skullBoosters.clear();
        registerAltarBooster(
                Blocks.SKULL, EAltarBoosterType.SKULL,
                new AltarBoosterFunc(100,
                    (state, tile, info) -> {
                        if (!(tile instanceof TileEntitySkull)) {
                            return;
                        }
                        TileEntitySkull tileSkull = (TileEntitySkull)tile;
                        switch (tileSkull.getSkullType()) {
                            case 0:
                                info.newRechargeScale++;
                                info.newPowerScale++;
                            case 1:
                                info.newRechargeScale += 2;
                                info.newPowerScale += 2;
                            case 2:
                            default:
                                break;
                            case 3:
                                info.newRechargeScale += 3;
                                info.newPowerScale += 3;
                        }
                    }
                )
        );
    }

    private static void initCandleBoosters() {
        candleBoosters.clear();
        registerAltarBooster(
                WitcheryBlocks.CANDELABRA, EAltarBoosterType.CANDLE,
                new AltarBoosterFunc(200,
                        (IBlockState state, TileEntity tile, AltarBoosterInfo info) -> info.newRechargeScale += 2
                )
        );
        registerAltarBooster(
            Blocks.TORCH, EAltarBoosterType.CANDLE,
            new AltarBoosterFunc(100,
                (IBlockState state, TileEntity tile, AltarBoosterInfo info) -> info.newRechargeScale += 1
            )
        );
    }

    private static void initChaliceBoosters() {
        chaliceBoosters.clear();
        registerAltarBooster(
            WitcheryBlocks.CHALICE, EAltarBoosterType.CHALICE,
            new AltarBoosterFunc(100,
                (IBlockState state, TileEntity tile, AltarBoosterInfo info) -> {
                Block block = state.getBlock();
                    if (block instanceof BlockChalice) {
                        BlockChalice chalice = (BlockChalice)block;
                        info.newPowerScale += chalice.isFull ? 2 : 1;
                    }
                }
            )
        );
    }


    static {
        initSkullBoosters();
        initCandleBoosters();
        initChaliceBoosters();
    }


    /** Registers a new Block as a valid block that recharges Altar */
    public static void registerBlock(Block block, int factor, int limit) {
        validBlocks.put(block, new AltarPowerSource(factor, limit));
    }

    /** Registers a new Blockstate as a valid block that recharges Altar */
    public static void registerBlockstate(IBlockState state, int factor, int limit) {
        validStates.put(state, new AltarPowerSource(factor, limit));
    }

    /** Registers a new Block as a valid block that recharges Altar */
    public static void registerBlock(Block block, int factor, int limit, Ingredient representativeItem) {
        validBlocks.put(block, new AltarPowerSource(factor, limit, representativeItem));
    }

    /** Registers a new Blockstate as a valid block that recharges Altar */
    public static void registerBlockstate(IBlockState state, int factor, int limit, Ingredient representativeItem) {
        validStates.put(state, new AltarPowerSource(factor, limit, representativeItem));
    }

    /** Registers a new Class as a valid block that recharges Altar */
    public static void registerClass(Class<?> clazz, int factor, int limit) {
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            if (block.getClass() == clazz) {
                validBlocks.put(block, new AltarPowerSource(factor, limit));
            }
        }
    }


    /** Un-registers a new Block as a valid block that recharges Altar */
    public static void removeBlock(Block block) {
        validBlocks.remove(block);
    }

    /** Un-registers a new Blockstate as a valid block that recharged Altar */
    public static void removeBlockstate(IBlockState state) {
        validStates.remove(state);
    }

    /** Un-registers a Class as a valid block that recharged Altar */
    public static void removeClass(Class<?> clazz) {
        List<Block> toRemove = new ArrayList<>();
        for (Block block : validBlocks.keySet()) {
            if (block.getClass() == clazz) {
                toRemove.add(block);
            }
        }

        for (Block block : toRemove) {
            validBlocks.remove(block);
        }
    }


    /** Returns true if the block can recharge Bark Belt */
    public static boolean canRechargeAltar(Block block) {
        return validBlocks.containsKey(block);
    }

    /** Returns true if the blockstate can recharge Bark Belt */
    public static boolean canRechargeAltar(IBlockState state) {
        return validBlocks.containsKey(state.getBlock()) || validStates.containsKey(state);
    }

    /** Returns the AltarPowerSource of the block. If the block can't recharge the Altar, returns null */
    public static AltarPowerSource getPowerSource(Block block) {
        if (validBlocks.containsKey(block)) return validBlocks.get(block);
        return null;
    }

    /** Returns the AltarPowerSource of the blockstate. If the blockstate can't recharge the Altar, returns null */
    public static AltarPowerSource getPowerSource(IBlockState state) {
        if (validStates.containsKey(state)) return validStates.get(state);
        return getPowerSource(state.getBlock());
    }

    /** Returns a Set of Sets of blockstates that can recharge the Altar */
    public static Set<Set<IBlockState>> getRechargers() {
        Set<Set<IBlockState>> finalStates = new HashSet<>();
        for (Block block : validBlocks.keySet()) {
            Set<IBlockState> blockStates = new HashSet<>(block.getBlockState().getValidStates());
            finalStates.add(blockStates);
        }
        for (IBlockState state : validStates.keySet()) {
            // Avoid duplicate entries when player specifies both block and blockstate
            if (!validBlocks.containsKey(state.getBlock()))
                finalStates.add(Collections.singleton(state));
        }
        return finalStates;
    }

    /** Utility function that returns all ItemStacks that can be associated to Altar power chargers.
     * It is a set made of the Ingredients that represent the Blocks and IBlockStates that will charge the Altar **/
    public static Map<Ingredient, AltarPowerSource> getRechargersRepresentativeItems() {
        Map<Ingredient, AltarPowerSource> map = new HashMap<>();

        // For both Blocks and IBlockStates, first check if the representative Ingredient has
        // been set to Ingredient.Emtpy. In that case, the entry is asking explicitely to not be
        // represented and should be invisible to things like JEI. If representative Ingredient is
        // null, the Block -> stack and IBlockState -> stack fallback will be used. If representative
        // item is not null and not empty, it will be used to represent the entry
        for (Map.Entry<Block, AltarPowerSource> entry : validBlocks.entrySet()) {
            if (entry.getValue().representativeItem == Ingredient.EMPTY) {
            } else if (entry.getValue().representativeItem == null) {
                ItemStack stack = new ItemStack(entry.getKey());
                if (!stack.isEmpty()) {
                    map.put(Ingredient.fromStacks(stack), entry.getValue());
                }
            } else {
                map.put(entry.getValue().representativeItem, entry.getValue());
            }
        }

        for (Map.Entry<IBlockState, AltarPowerSource> entry : validStates.entrySet()) {
            if (entry.getValue().representativeItem == Ingredient.EMPTY) {
            } else if (entry.getValue().representativeItem == null) {
                ItemStack stack = Utils.blockstateToStack(entry.getKey());
                if (!stack.isEmpty()) {
                    map.put(Ingredient.fromStacks(stack), entry.getValue());
                }
            } else {
                map.put(entry.getValue().representativeItem, entry.getValue());
            }
        }
        return map;
    }


    /** ========== ALTAR BOOSTERS ========== **/

    /** Registers a new IBlockState as an Altar Booster of the specified type. Support is limited for now **/
    public static void registerAltarBooster(IBlockState state, EAltarBoosterType type, AltarBoosterFunc booster) {
        if (state == null || type == null || booster == null) {
            WitcheryCompanion.logger.warn("[Altar API] A mod tried to register an Altar Booster with a null State, Type or Booster function");
            return;
        }
        type.supplier.get().put(state, booster);
    }

    /** Registers all the valid IBlockStates of a block as an Altar Booster of the specified type. Support is limited for now **/
    public static void registerAltarBooster(Block block, EAltarBoosterType type, AltarBoosterFunc booster) {
        if (block == null || type == null || booster == null) {
            WitcheryCompanion.logger.warn("[Altar API] A mod tried to register an Altar Booster with a null State, Type or Booster function");
            return;
        }
        HashMap<IBlockState, AltarBoosterFunc> boosterMap = type.supplier.get();
        for (IBlockState state : block.getBlockState().getValidStates()) {
            boosterMap.put(state, booster);
        }
    }

    /** Unregisters the specified IBlockState as Altar Booster of the specified type **/
    public static void unregisterAltarBooster(IBlockState state, EAltarBoosterType type) {
        if (type != null) {
            type.supplier.get().remove(state);
        }
    }

    /** Unregisters all valid IBlockStates of the Block as Altar Booster of the specified type **/
    public static void unregisterAltarBooster(Block block, EAltarBoosterType type) {
        if (type != null) {
            HashMap<IBlockState, AltarBoosterFunc> boosterMap = type.supplier.get();
            for (IBlockState state : block.getBlockState().getValidStates()) {
                boosterMap.remove(state);
            }
        }
    }

    /** Returns true if the BlockState is a booster of the specified type **/
    public static boolean isAltarBooster(IBlockState state, EAltarBoosterType type) {
        if (state == null || type == null) {
            return false;
        }
        return type.supplier.get().containsKey(state);
    }

    /** Returns true if the specified blockstate is an Altar Booster **/
    public static AltarBoosterFunc getBooster(IBlockState state, EAltarBoosterType type) {
        if (state == null || type == null) {
            return null;
        }
        try {
            return type.supplier.get().getOrDefault(state, null);
        } catch (Exception ex) {
            WitcheryCompanion.logger.error("[Altar API] Wrong booster return type for BlockState: {}", state);
            return null;
        }
    }


    public static class AltarPowerSource {

        private final int factor;
        private final int limit;
        private final Ingredient representativeItem;

        public AltarPowerSource(int factor, int limit) {
            this.factor = factor;
            this.limit = limit;
            this.representativeItem = null;
        }

        public AltarPowerSource(int factor, int limit, Ingredient representativeItem) {
            this.factor = factor;
            this.limit = limit;
            this.representativeItem = representativeItem;
        }

        public int getFactor() {
            return factor;
        }

        public int getLimit(int enhancementLevel) {
            return (int)(Math.max(1.18 * enhancementLevel, 1) * limit);
        }

        public Ingredient getRepresentativeItem() {
            return this.representativeItem;
        }

    }

    public static class AltarBoosterInfo {
        public int newRechargeScale = 1;
        public int newPowerScale = 1;
        public int newRangeScale = 1;
        public int newEnhancementLevel = 0;
    }

    public static class AltarBoosterFunc {
        public final AltarBoosterConsumer consumer;
        public final int priority;

        public AltarBoosterFunc(int priority, AltarBoosterConsumer consumer) {
            this.priority = priority;
            this.consumer = consumer;
        }
    }

    @FunctionalInterface
    public interface AltarBoosterConsumer {
        void apply(IBlockState state, TileEntity tile, AltarBoosterInfo info);
    }




    public enum EAltarBoosterType {
        SKULL(() -> AltarApi.skullBoosters),
        CANDLE(() -> AltarApi.candleBoosters),
        CHALICE(() -> AltarApi.chaliceBoosters),
        ;

        final Supplier<HashMap<IBlockState, AltarBoosterFunc>> supplier;

        EAltarBoosterType(Supplier<HashMap<IBlockState, AltarBoosterFunc>> supplier) {
            this.supplier = supplier;
        }
    }
}
