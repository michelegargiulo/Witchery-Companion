package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityWerewolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWerewolf.class)
public abstract class EntityWerewolfMixin extends EntityMob {

    private EntityWerewolfMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    protected void disableDropFewItems(boolean param, int fortune, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.werewolf_tweakLootTable) {
            ci.cancel();
        }
    }

    @Inject(method = "getDropItem", remap = false, cancellable = true, at = @At("HEAD"))
    protected void disableGetDropItem(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.werewolf_tweakLootTable) {
            cir.setReturnValue(null);
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.werewolf_tweakLootTable ? LootTables.WEREWOLF : null;
    }
}
