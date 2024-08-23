package com.smokeythebandicoot.witcherycompanion.api.capability;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@SuppressWarnings("unchecked")
public class CapabilityWitcheryProgress {

    @CapabilityInject(IWitcheryProgress.class)
    public static final Capability<IWitcheryProgress> WITCHERY_PROGRESS_CAPABILITY = Null();

    public static Capability<IWitcheryProgress> Null() { return null; }

    private static final String PROGRESS_TAG = "WitcheryDiscoveredSecrets";

    private static final ResourceLocation WITCHERY_PROGRESS_RL = new ResourceLocation(WitcheryCompanion.MODID, "witchery_progress");


    public static void register() {
        CapabilityManager.INSTANCE.register(IWitcheryProgress.class, new Storage(), WitcheryProgress::new);
    }

    @Nullable
    public static IWitcheryProgress getWitcheryProgress(final Entity entity) {
        if (entity instanceof EntityPlayer && entity.hasCapability(WITCHERY_PROGRESS_CAPABILITY, null)) {
            return entity.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
        }
        return null;
    }


    public static class Provider implements ICapabilitySerializable<NBTBase> {

        private IWitcheryProgress instance;

        public Provider() {
            instance = new WitcheryProgress(); // Default implementation
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
            if (capability == WITCHERY_PROGRESS_CAPABILITY)
                return true;
            return false;
        }

        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == WITCHERY_PROGRESS_CAPABILITY)
                return WITCHERY_PROGRESS_CAPABILITY.cast(instance);
            return null;
        }

        @Override
        public NBTBase serializeNBT() {
            return WITCHERY_PROGRESS_CAPABILITY.getStorage().writeNBT(WITCHERY_PROGRESS_CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            WITCHERY_PROGRESS_CAPABILITY.getStorage().readNBT(WITCHERY_PROGRESS_CAPABILITY, instance, null, nbt);

        }
    }

    public static class Storage implements Capability.IStorage<IWitcheryProgress> {

        @Override
        public NBTBase writeNBT(Capability<IWitcheryProgress> capability, IWitcheryProgress instance, EnumFacing side) {
            NBTTagCompound witcheryProgress = new NBTTagCompound();
            NBTTagList knownSecrets = new NBTTagList();
            for (String progress : instance.getUnlockedProgress()) {
                knownSecrets.appendTag(new NBTTagString(progress));
            }
            witcheryProgress.setTag(PROGRESS_TAG, knownSecrets);
            return witcheryProgress;
        }

        // Load serialized data from disk
        @Override
        public void readNBT(Capability<IWitcheryProgress> capability, IWitcheryProgress instance, EnumFacing side, NBTBase nbt) {
            instance.resetProgress();
            if (nbt instanceof NBTTagCompound) {
                NBTTagList progress = ((NBTTagCompound) nbt).getTagList(PROGRESS_TAG, 9);
                int index = 0;
                String s = progress.getStringTagAt(index);
                while (!s.isEmpty()) {
                    instance.unlockProgress(s);
                    s = progress.getStringTagAt(++index);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                final WitcheryProgress witcheryProgress = new WitcheryProgress();
                event.addCapability(WITCHERY_PROGRESS_RL, new Provider());
            }
        }

        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event) {
            final IWitcheryProgress oldProgress = getWitcheryProgress(event.getOriginal());
            final IWitcheryProgress newProgress = getWitcheryProgress(event.getEntityPlayer());

            if (oldProgress != null && newProgress != null) {
                newProgress.setUnlockedProgress(oldProgress.getUnlockedProgress());
            }
        }

        @SubscribeEvent
        public static void onPlayerLogsIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
            //EntityPlayer player = event.player;
            //IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
            if (!event.player.world.isRemote) {
                ProgressSync.serverRequest(event.player);
                Utils.logChat("Player joined. Sending progress from server");
            }

        }

    }

}
