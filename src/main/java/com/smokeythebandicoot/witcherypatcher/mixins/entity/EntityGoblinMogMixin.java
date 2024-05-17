package com.smokeythebandicoot.witcherypatcher.mixins.entity;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityGoblinMog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Introduct own loot table
 */
@Mixin(value = EntityGoblinMog.class, remap = false)
public abstract class EntityGoblinMogMixin extends EntityMob {

    private EntityGoblinMogMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.goblinMog_tweakLootTable ? LootTables.GOBLIN_MOG : null;
    }

    @Inject(method = "dropFewItems", remap = true, cancellable = true, at = @At("HEAD"))
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.goblinMog_tweakLootTable) {
            ci.cancel();
        }
    }
}
