package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.api.goblintrade.GoblinTradeApi;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityGoblin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

/**
 Mixins:
 [Tweak] Crafttweaker Integration for trades
 */
@Mixin(EntityGoblin.class)
public abstract class EntityGoblinMixin extends EntityAgeable {

    private EntityGoblinMixin(World worldIn) {
        super(worldIn);
    }

    @Shadow(remap = false)
    private MerchantRecipeList buyingList;

    @Shadow(remap = false)
    public abstract int getProfession();

    @Shadow(remap = false)
    public abstract void setProfession(int par1);

    @Shadow public abstract MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer);

    /** This method is responsible to assign custom professions to the Goblin, on init, in case the user adds or removes
     any profession. This simply sets again the profession of the Goblin, selecting one among the possible ones */
    @Inject(method = "<init>", remap = false, at = @At("TAIL"))
    public void WPinjectCustomProfessionsOnInit(World world, CallbackInfo ci) {
        if (!EntityTweaks.goblin_tweakCustomTrades) return;
        this.setProfession(GoblinTradeApi.getRandomProfessionID(this.rand));
    }

    /** Sets an initial random profession on Goblin spawn */
    @Inject(method = "onInitialSpawn", remap = false, at = @At("TAIL"))
    public void WPinjectCustomProfessionsOnSpawn(DifficultyInstance difficulty, IEntityLivingData livingData, CallbackInfoReturnable<IEntityLivingData> cir) {
        if (!EntityTweaks.goblin_tweakCustomTrades) return;
        this.setProfession(GoblinTradeApi.getRandomProfessionID(this.rand));
    }

    /** This method is responsible to inject custom trades into the buying list, that is the only variable that is read
     when the player right-clicks a Hobgoblin and the trade is generated. Modifying that variable, controls the trades */
    @Inject(method = "addDefaultEquipmentAndRecipies", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPcustomGoblinTrades(CallbackInfo ci) {
        if (!EntityTweaks.goblin_tweakCustomTrades) return;

        MerchantRecipeList newTrades = GoblinTradeApi.generateTrades(this.getProfession());
        Collections.shuffle(newTrades);
        if (this.buyingList == null || this.buyingList.isEmpty()) {
            buyingList = newTrades;
        } else {
            this.buyingList.addAll(newTrades);
        }
        ci.cancel();

    }

    @WrapOperation(method = "processInteract", remap = false, at = @At(remap = false, value = "FIELD",
            target = "Lnet/msrandom/witchery/entity/EntityGoblin;village:Lnet/minecraft/village/Village;"))
    public Village ignoreVillageRequirement(EntityGoblin instance, Operation<Village> original) {
        if (EntityTweaks.goblin_tweakRemoveTradingVillageRequirements) {
            return new Village();
        }
        return original.call(instance);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.hobgoblin_tweakLootTable ? LootTables.HOBGOBLIN : null;
    }

    /*
    /** This Mixin makes the missing call to getRecipes after the player starts trading with the Goblin.
     * Note: the At.Shift.AFTER is not required, but makes more sense for code readability in .mixins.out
    @Inject(method = "processInteract", remap = false, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/entity/EntityGoblin;setCustomer(Lnet/minecraft/entity/player/EntityPlayer;)V"))
    public void reinitOnChangeProfession(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (EntityTweaks.goblin_fixNoTrades) {
            this.getRecipes(player);
        }
    }
    */
}
