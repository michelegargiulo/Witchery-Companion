package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;

/** Client side of Tinkers tools: models and Tool Station layout. Models are the .tcon.json files in
 * assets/witcherycompanion/models/item/tools **/
@SideOnly(Side.CLIENT)
public class TinkersToolsClient {

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        ModelRegisterUtil.registerToolModel(TinkersTools.ARTHANA);
        ModelRegisterUtil.registerToolModel(TinkersTools.BOLINE);
    }

    /** Slot positions of the parts in the Tool Station / Crafting Station. Same as Tinkers' tools with two parts **/
    public static void registerToolBuildingInfo() {
        ToolBuildGuiInfo arthana = new ToolBuildGuiInfo(TinkersTools.ARTHANA);
        arthana.addSlotPosition(13, 62);
        arthana.addSlotPosition(53, 22);
        TinkerRegistryClient.addToolBuilding(arthana);

        ToolBuildGuiInfo boline = new ToolBuildGuiInfo(TinkersTools.BOLINE);
        boline.addSlotPosition(22, 53);
        boline.addSlotPosition(31, 22);
        TinkerRegistryClient.addToolBuilding(boline);
    }
}
