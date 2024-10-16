package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.msrandom.witchery.entity.EntityBabaYaga;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 Mixins:
 [Tweak] Tweak to give loot to owner based on loot table instead of hardcoded items
 [Tweak] Tweak to modify max distance within the owner must stay for Baba to give loot
 [Tweak] Tweak to modify interval in ticks between each loot generation attempt
 [Tweak] Tweak to modify how long in ticks Baba gives loot to their owner before vanishing
 [Tweak] Introduce own loot table
 */
@Mixin(EntityBabaYaga.class)
public abstract class EntityBabaYagaMixin extends EntityMob {

    @Shadow(remap = false)
    private EntityPlayer owner;

    @Final @Shadow(remap = false)
    private static Item[] witchDrops;

    private EntityBabaYagaMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "dropFewItems", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPlootTweakDropFewItems(boolean par1, int par2, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.babaYaga_tweakLootTable) {
            ci.cancel();
        }
    }

    @Inject(method = "onLivingUpdate", remap = true, cancellable = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/msrandom/witchery/entity/EntityBabaYaga;getDistanceSq(Lnet/minecraft/entity/Entity;)D"))
    public void WPdropItemsFromLootTable(CallbackInfo ci) {

        if (ModConfig.PatchesConfiguration.EntityTweaks.babaYaga_enableTweaks) {

            double distance = this.getDistanceSq(owner);
            int delay = ModConfig.PatchesConfiguration.EntityTweaks.babaYaga_tweakGiveLootTickInterval;

            // Configurable distance and delay
            if (distance < ModConfig.PatchesConfiguration.EntityTweaks.babaYaga_tweakLivingDropMaxDistance && this.ticksExisted % delay == 0) {

                // Drop based on Loot Tables
                if (ModConfig.PatchesConfiguration.LootTweaks.babaYaga_tweakGiveDropLootTable) {
                    List<ItemStack> dropStacks;
                    dropStacks = witchery_Patcher$giveLootToOwner();

                    for (ItemStack stack : dropStacks) {
                        this.entityDropItem(stack, 0.0f);
                    }

                // Default Witchery Behaviour
                } else {
                    int l = this.rand.nextInt(3);
                    Item i1 = witchDrops[this.rand.nextInt(witchDrops.length - 3)];

                    for(int j1 = 0; j1 < l; ++j1) {
                        this.entityDropItem(new ItemStack(i1), 0.0F);
                    }
                }


            }

            if (this.ticksExisted > ModConfig.PatchesConfiguration.EntityTweaks.babaYaga_tweakMaxGiveTicks) {
                this.setDead();
                this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
                this.world.setEntityState(this, (byte) 16);
            }

            super.onLivingUpdate();

        }

    }

    @Unique
    protected List<ItemStack> witchery_Patcher$giveLootToOwner() {

        LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(LootTables.BABA_YAGA_OWNER);
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world));

        return loottable.generateLootForPools(this.rand, lootcontext$builder.build());

    }

    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.babaYaga_tweakLootTable ?
                LootTables.BABA_YAGA_DEATH : null;
    }
}
