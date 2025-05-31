package com.smokeythebandicoot.witcherycompanion.patches.transformation;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.transformation.VampireCreatureTrait;
import net.msrandom.witchery.util.WitcheryUtils;

@SideOnly(Side.CLIENT)
public class VampireKeybindsHandler {

    private VampireKeybindsHandler() { }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        VampireCreatureTrait transformation = WitcheryUtils.getExtension(player).getTransformation(WitcheryCreatureTraits.VAMPIRE);
        int level = transformation.getLevel();

        for (ClientProxy.EVampireKeybind keybind : ClientProxy.EVampireKeybind.values()) {
            // Select the first valid ability
            if (keybind.trySelectAbility(transformation, level)) {
                break;
            }
        }

    }

}
