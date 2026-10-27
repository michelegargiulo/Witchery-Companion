package com.smokeythebandicoot.witcherycompanion.patches.rite;

import com.google.gson.JsonObject;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.CustomRiteApi;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteContext;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteDefinition;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteEffectHandler;
import kotlin.jvm.functions.Function1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.rite.RiteHandler;
import net.msrandom.witchery.rite.effect.RiteEffect;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The only RiteEffect of custom rites. It is created by W:R's RiteManager from the "result" of the rite JSON
 * ({"type": "witcherycompanion:custom_rite", "rite": "<id>"}) and forwards the rite to the handler registered in
 * {@link CustomRiteApi}. The checks mimic the ones of W:R's own effects (RiteEffectSummonCreature for the coven).
 */
public class RiteEffectCustomRite extends RiteEffect {

    public static final Function1<JsonObject, RiteEffect> DESERIALIZER = new Function1<JsonObject, RiteEffect>() {
        @Override
        public RiteEffect invoke(JsonObject json) {
            return new RiteEffectCustomRite(json.get("rite").getAsString());
        }
    };

    private final String riteId;

    public RiteEffectCustomRite(String riteId) {
        this.riteId = riteId;
    }

    @Override
    public RiteHandler.Result process(World world, BlockPos pos, int ticks, AtomicInteger stage, TileEntityCircle.ActivatedRitual ritual) {
        if (world.isRemote) return RiteHandler.Result.COMPLETED;

        CustomRiteDefinition definition = CustomRiteApi.getDefinition(riteId);
        CustomRiteEffectHandler handler = CustomRiteApi.getHandler(riteId);
        EntityPlayer player = ritual.getInitiatingPlayer(world);
        if (definition == null || handler == null) {
            WitcheryCompanion.logger.error("Custom Rite {} has no registered effect (was its script removed?). Aborting", riteId);
            return RiteHandler.Result.ABORTED_REFUND;
        }

        // Same check and message of W:R's RiteEffectSummonCreature
        if (ritual.covenSize < definition.getCovenSize()) {
            if (player != null) {
                player.sendMessage(new TextComponentTranslation("rite.witchery.weak_coven")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
            }
            return RiteHandler.Result.ABORTED_REFUND;
        }

        CustomRiteContext context = new CustomRiteContext(riteId, world, pos, ticks, ritual);
        try {
            handler.run(context);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error("The effect of Custom Rite " + riteId + " threw an exception. The rite is aborted", t);
            if (player != null) {
                player.sendMessage(new TextComponentString("The rite failed due to a script error (see log)")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
            }
            return RiteHandler.Result.ABORTED_REFUND;
        }

        String message = context.getAbortMessage();
        if (message != null && !message.isEmpty() && player != null) {
            player.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return context.getResult();
    }

}
