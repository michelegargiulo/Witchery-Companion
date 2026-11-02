package com.smokeythebandicoot.witcherycompanion.api.customrite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.rite.RitualCircle;
import net.msrandom.witchery.rite.RiteHandler;
import net.msrandom.witchery.rite.effect.RiteEffect;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Everything W:R gives to the process() of one of its own rite effects, plus a way to abort the rite.
 * Inputs: world, circle position (the "currentPosition" argument of process), the ritual (initiating player, coven size,
 * circles, area, sacrificed items, location) and the tick counter of the circle.
 */
public class CustomRiteContext {

    /** An item that was thrown into the circle as a sacrifice, with the place where it was consumed */
    public static class SacrificedItem {
        private final long identifier;
        private final ItemStack stack;
        private final BlockPos position;

        public SacrificedItem(long identifier, ItemStack stack, BlockPos position) {
            this.identifier = identifier;
            this.stack = stack;
            this.position = position;
        }

        /** Key under which W:R stores the item: ItemRiteSacrifice.parseIdentifier(identifier string) */
        public long getIdentifier() { return identifier; }
        public ItemStack getStack() { return stack; }
        public BlockPos getPosition() { return position; }
    }

    private final String riteId;
    private final World world;
    private final BlockPos position;
    private final int ticks;
    private final TileEntityCircle.ActivatedRitual ritual;

    private RiteHandler.Result result = RiteHandler.Result.COMPLETED;
    private String abortMessage = null;

    public CustomRiteContext(String riteId, World world, BlockPos position, int ticks, TileEntityCircle.ActivatedRitual ritual) {
        this.riteId = riteId;
        this.world = world;
        this.position = position;
        this.ticks = ticks;
        this.ritual = ritual;
    }

    public String getRiteId() { return riteId; }
    public World getWorld() { return world; }

    /** Position of the circle (golden chalk) */
    public BlockPos getPosition() { return position; }

    /** Location of the ritual, which sacrifices can move (for example a Bound Waystone). Can be null */
    @Nullable
    public BlockPos getLocation() { return ritual.getLocation(); }

    /** Player that started the rite. Null if the player is offline */
    @Nullable
    public EntityPlayer getPlayer() { return ritual.getInitiatingPlayer(world); }

    @Nullable
    public UUID getPlayerId() { return ritual.getInitiatingPlayerId(); }

    /** Number of witches of the coven of the player, as counted when the rite was started */
    public int getCovenSize() { return ritual.covenSize; }

    /** Tick counter of the circle block entity, as passed to RiteEffect.process() */
    public int getTicks() { return ticks; }

    /** Unique id of this ritual, as stored by W:R */
    public long getRitualId() { return ritual.id; }

    /** Area where the rite looks for sacrifices */
    public AxisAlignedBB getBounds() { return ritual.bounds; }

    public List<RitualCircle> getCircles() { return new ArrayList<>(ritual.circles); }

    public List<SacrificedItem> getSacrificedItems() {
        List<SacrificedItem> items = new ArrayList<>();
        for (Map.Entry<Long, RiteEffect.SacrificedItem> entry : ritual.sacrificedItems.entrySet()) {
            RiteEffect.SacrificedItem item = entry.getValue();
            items.add(new SacrificedItem(entry.getKey(), item.getStack(), item.getLocation()));
        }
        return items;
    }

    /** Looks up a sacrificed item by the identifier given with identified(); null if no such item was sacrificed */
    @Nullable
    public SacrificedItem getSacrificedItem(String identifier) {
        long key = ItemRiteSacrifice.parseIdentifier(identifier == null ? "" : identifier);
        RiteEffect.SacrificedItem item = ritual.sacrificedItems.get(key);
        return item == null ? null : new SacrificedItem(key, item.getStack(), item.getLocation());
    }

    // ------------------------------------------------------------------------------------------ Outcome

    /** Aborts the rite. Sacrificed items are lost. The message (can be null) is sent to the player */
    public void abort(@Nullable String message) {
        this.result = RiteHandler.Result.ABORTED;
        this.abortMessage = message;
    }

    /** Aborts the rite. Sacrificed items are dropped back. The message (can be null) is sent to the player */
    public void abortAndRefund(@Nullable String message) {
        this.result = RiteHandler.Result.ABORTED_REFUND;
        this.abortMessage = message;
    }

    public RiteHandler.Result getResult() { return result; }
    @Nullable
    public String getAbortMessage() { return abortMessage; }
}
