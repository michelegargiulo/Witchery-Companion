package com.smokeythebandicoot.witcherycompanion.patches.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.patches.common.CommonEventsPatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.network.PacketEyePosition;
import net.msrandom.witchery.network.PacketSyncEntitySize;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.ResizingUtils;

/**
 Patches:
 [Bugfix] Fixes persistency with NBT data being wiped on death. For example, infusions
 */
public class SymbolEffectPatch {

    public static final SymbolEffectPatch INSTANCE = new SymbolEffectPatch();

    private SymbolEffectPatch() { }


    // NOTE: this event is subscribed only if soulBrews_fixPersistency is enabled in config
    @SubscribeEvent
    public void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        if (!oldPlayer.getEntityData().hasKey("WitcherySpellBook")) return;

        NBTTagCompound acquiredKnowledge = oldPlayer.getEntityData().getCompoundTag("WitcherySpellBook");
        newPlayer.getEntityData().setTag("WitcherySpellBook", acquiredKnowledge);

    }

}
