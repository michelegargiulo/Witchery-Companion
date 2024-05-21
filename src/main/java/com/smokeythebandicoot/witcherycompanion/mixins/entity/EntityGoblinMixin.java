package com.smokeythebandicoot.witcherycompanion.mixins.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.api.GoblinTradeApi;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityGoblin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

/**
 Mixins:
 [Tweak] Crafttweaker Integration for trades
 [Tweak]
 */
@Mixin(EntityGoblin.class)
public abstract class EntityGoblinMixin {

    @Shadow
    private MerchantRecipeList buyingList;

    @Shadow
    public abstract int getProfession();

    @Shadow
    public abstract void setProfession(int par1);

    // This method is responsible to assign custom professions to the Goblin, on init, in case the user adds or removes
    // any profession. This simply sets again the profession of the Goblin, selecting one among the possible ones.
    @Inject(method = "<init>", remap = false, at = @At("TAIL"))
    public void WPinjectCustomProfessions(World world, CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakCustomTrades) return;
        this.setProfession(GoblinTradeApi.getRandomProfessionID(world));
    }

    // This method is responsible to inject custom trades into the buying list, that is the only variable that is read
    // when the player right-clicks a Hobgoblin and the trade is generated. Modifying that variable, controls the trades
    @Inject(method = "addDefaultEquipmentAndRecipies", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPcustomGoblinTrades(CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakCustomTrades) return;
        this.buyingList = GoblinTradeApi.generateTrades(this.getProfession());
        Collections.shuffle(buyingList);
        ci.cancel();
    }

}
