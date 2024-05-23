package com.smokeythebandicoot.witcherycompanion.patches.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 [Bugfix] Fixes persistency with NBT data being wiped on death. For example, infusions
 */
public class SymbolEffectPatch {

    private static SymbolEffectPatch INSTANCE = null;

    private SymbolEffectPatch() { }

    public static SymbolEffectPatch getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SymbolEffectPatch();
        return INSTANCE;
    }

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
