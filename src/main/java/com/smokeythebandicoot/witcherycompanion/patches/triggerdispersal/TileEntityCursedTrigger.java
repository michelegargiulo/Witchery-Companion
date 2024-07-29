package com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.EntityUtil;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.UUID;

/** An improved version of TileEntityCursedBlock that does not implement ITickable */
public class TileEntityCursedTrigger extends TileEntity {

    public BrewActionList actionList;
    public int duration;
    public int expansion;
    public int count;
    public long lastActivation;
    public UUID thrower;

    //TODO: make this configurable
    public static int cooldown = 10;

    public static ResourceLocation registryName = new ResourceLocation(WitcheryCompanion.MODID, "cursed_trigger");

    public TileEntityCursedTrigger() {
    }

    public static ResourceLocation getRegistryName() {
        return registryName;
    }


    /** Used when the TileEntity is created and curses need to be set */
    public void initialize(ModifiersImpact impactModifiers, BrewActionList actionList) {
        this.actionList = actionList;
        this.duration = impactModifiers.lifetime.get() >= 0 ? 5 + impactModifiers.lifetime.get() * impactModifiers.lifetime.get() * 5 : 100;
        this.expansion = Math.min(4 + impactModifiers.extent.get(), 10);
        if (impactModifiers.thrower != null) {
            this.thrower = impactModifiers.thrower.getUniqueID();
        }

        this.count = 1;
        IBlockState state = world.getBlockState(this.getPos());
        world.notifyBlockUpdate(this.getPos(), state, state, 0);
        this.markDirty();
    }

    /** Used when curses have to be updated instead */
    public void updateCurse(ModifiersImpact impactModifiers, BrewActionList actionList) {
        if (this.actionList.equals(actionList)) {
            ++this.count;
        } else {
            this.actionList = actionList;
            this.count = 1;
            this.duration = impactModifiers.lifetime.get() >= 0 ? 5 + impactModifiers.lifetime.get() * impactModifiers.lifetime.get() * 5 : 100;
            this.expansion = Math.min(4 + impactModifiers.extent.get(), 10);
            if (impactModifiers.thrower != null) {
                this.thrower = impactModifiers.thrower.getUniqueID();
            }
        }
        this.markDirty();
    }

    /** When the attached block is triggered, this function is called. Returns true if the TE should be destroyed afterwards */
    public boolean applyToEntityAndDestroy(Entity entity) {
        long currentTickCount = entity.world.getTotalWorldTime();
        if (entity instanceof EntityLivingBase && (lastActivation + cooldown < currentTickCount)) {

            EntityLivingBase living = (EntityLivingBase)entity;
            EntityPlayer player = WitcheryUtils.getPlayer(this.world, this.thrower);
            player = EntityUtil.playerOrFake(this.world, player);

            // This player object goes into ModifiersEffect caster, that is used for many brews
            // Having player still == null *should* be supported by all brews, that in turn pass this
            // caster to other objects (like world.createExplosion(EntityPlayer player, ...)
            // All usages should be @Nullable, so no problems should arise.
            // A null check is not implemented here, as the effect should effectively be executed
            // even when the caster is null. It is responsibility of each BrewEffect to check for null

            // If action list is null, return early
            if (this.actionList == null) {
                return true;
            }

            this.actionList.applyToEntity(entity.world, living, new ModifiersEffect(1.0, 1.0, false, living.getPositionVector(), false, 0, player));
            living.world.playSound(null, living.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 0.4F / (living.world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllTracking(new PacketParticles(living.posX, living.posY, living.posZ, 1.0F, 1.0F, 16777215), living);
            lastActivation = currentTickCount;
            this.count--;
            this.markDirty();
        }

        return this.count <= 0;
    }

    /** TileEntities get removed when BlockState changes. To prevent this, this method should return false,
     * except when the Block itself gets replaced by another one (for example, AIR). Even if the block is
     * replaced by another IDispersalTrigger, this should return false. (For example, if a Triggerable button
     * is replaced by a Triggerable pressure plate, curses should have to be reapplied */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.getPos(), 1, nbtTag);
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtRoot) {
        super.writeToNBT(nbtRoot);
        // Makes no sense to save elements if actionList is null
        if (this.actionList != null) {
            nbtRoot.setTag("Effect", this.actionList.write());
            nbtRoot.setInteger("Duration", this.duration);
            nbtRoot.setInteger("Expansion", this.expansion);
            nbtRoot.setInteger("Count", this.count);
            nbtRoot.setLong("LastActivation", this.lastActivation);
            if (this.thrower != null) {
                nbtRoot.setUniqueId("Creator", this.thrower);
            }
        }
        return nbtRoot;
    }

    public void readFromNBT(NBTTagCompound nbtRoot) {
        super.readFromNBT(nbtRoot);
        this.actionList = new BrewActionList(nbtRoot.getTagList("Effect", 10));
        this.duration = nbtRoot.getInteger("Duration");
        this.expansion = nbtRoot.getInteger("Expansion");
        this.count = nbtRoot.getInteger("Count");
        this.lastActivation = nbtRoot.getLong("LastActivation");
        if (nbtRoot.hasUniqueId("Creator")) {
            this.thrower = nbtRoot.getUniqueId("Creator");
        }

    }
}
