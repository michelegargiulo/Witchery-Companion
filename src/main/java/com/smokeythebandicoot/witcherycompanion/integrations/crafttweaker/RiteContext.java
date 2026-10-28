package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteContext;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.msrandom.witchery.rite.RitualCircle;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/** ZenScript view of the inputs that W:R gives to the effect of a rite. Passed to the function of CustomRiteBuilder.setEffect */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.RiteContext")
@ZenRegister
public class RiteContext {

    private final CustomRiteContext context;

    public RiteContext(CustomRiteContext context) {
        this.context = context;
    }

    @ZenGetter("riteId")
    @ZenDoc(value = "Id of the rite that is being performed")
    public String getRiteId() {
        return context.getRiteId();
    }

    @ZenGetter("world")
    @ZenDoc(value = "The world where the rite is performed")
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(context.getWorld());
    }

    @ZenGetter("position")
    @ZenDoc(value = "Position of the circle (the golden chalk in the center)")
    public IBlockPos getPosition() {
        return CraftTweakerMC.getIBlockPos(context.getPosition());
    }

    @ZenGetter("location")
    @ZenDoc(value = "Location of the ritual, which a sacrifice can change (for example a Bound Waystone). Null if not set")
    public IBlockPos getLocation() {
        BlockPos location = context.getLocation();
        return location == null ? null : CraftTweakerMC.getIBlockPos(location);
    }

    @ZenGetter("player")
    @ZenDoc(value = "The player that started the rite. Null if the player is offline")
    public IPlayer getPlayer() {
        EntityPlayer player = context.getPlayer();
        return player == null ? null : CraftTweakerMC.getIPlayer(player);
    }

    @ZenGetter("playerUuid")
    @ZenDoc(value = "UUID of the player that started the rite")
    public String getPlayerUuid() {
        return context.getPlayerId() == null ? null : context.getPlayerId().toString();
    }

    @ZenGetter("covenSize")
    @ZenDoc(value = "Number of witches in the coven of the player that started the rite")
    public int getCovenSize() {
        return context.getCovenSize();
    }

    @ZenGetter("ticks")
    @ZenDoc(value = "Tick counter of the circle, as W:R passes to the effects of its own rites")
    public int getTicks() {
        return context.getTicks();
    }

    @ZenGetter("ritualId")
    @ZenDoc(value = "Unique id of this ritual")
    public long getRitualId() {
        return context.getRitualId();
    }

    @ZenGetter("bounds")
    @ZenDoc(value = "Area of the rite as [minX, minY, minZ, maxX, maxY, maxZ]")
    public double[] getBounds() {
        AxisAlignedBB bounds = context.getBounds();
        return new double[]{bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ};
    }

    @ZenGetter("circles")
    @ZenDoc(value = "The circles drawn for the rite: glyph block id and size")
    public RiteCircle[] getCircles() {
        List<RitualCircle> circles = context.getCircles();
        RiteCircle[] result = new RiteCircle[circles.size()];
        for (int i = 0; i < result.length; i++) {
            RitualCircle circle = circles.get(i);
            // W:R stores the size of a circle as twice the size written in the rite data (1, 2, 3 -> 2, 4, 6)
            result[i] = new RiteCircle(String.valueOf(circle.getGlyph().getRegistryName()), circle.getSize() / 2);
        }
        return result;
    }

    @ZenGetter("sacrifices")
    @ZenDoc(value = "The items that were sacrificed, with the position where each was consumed")
    public RiteSacrificedItem[] getSacrifices() {
        List<CustomRiteContext.SacrificedItem> items = context.getSacrificedItems();
        RiteSacrificedItem[] result = new RiteSacrificedItem[items.size()];
        for (int i = 0; i < result.length; i++) result[i] = new RiteSacrificedItem(items.get(i));
        return result;
    }

    @ZenMethod
    @ZenDoc(value = "The sacrificed item that was given the identifier with identified(...). Null if it was not sacrificed " +
            "(for example an optional item)")
    public RiteSacrificedItem getSacrifice(String identifier) {
        CustomRiteContext.SacrificedItem item = context.getSacrificedItem(identifier);
        return item == null ? null : new RiteSacrificedItem(item);
    }

    @ZenMethod
    @ZenDoc(value = "Aborts the rite: sacrificed items are lost. The message is shown to the player (can be null)")
    public void abort(String message) {
        context.abort(message);
    }

    @ZenMethod
    @ZenDoc(value = "Aborts the rite: sacrificed items are dropped back. The message is shown to the player (can be null)")
    public void abortAndRefund(String message) {
        context.abortAndRefund(message);
    }

}
