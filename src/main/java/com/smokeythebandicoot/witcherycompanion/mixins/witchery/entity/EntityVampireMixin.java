package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityVampire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityVampire.class)
public abstract class EntityVampireMixin extends EntityCreature {

    private EntityVampireMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "getDropItem", remap = false, cancellable = true, at = @At("HEAD"))
    protected void dropFewItems(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.vampire_tweakLootTable) {
            cir.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.vampire_tweakLootTable ? LootTables.VAMPIRE : null;
    }

}
