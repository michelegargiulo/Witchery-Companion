package com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.LootTables;
import jeresources.api.IJERAPI;
import jeresources.api.IMobRegistry;
import jeresources.compatibility.JERAPI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.witchery.entity.*;
import net.msrandom.witchery.entity.monster.EntityHellhound;
import net.msrandom.witchery.entity.monster.EntityHornedHuntsman;
import net.msrandom.witchery.entity.monster.EntityMandrake;
import net.msrandom.witchery.entity.passive.EntityCatFamiliar;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.entity.passive.EntityToad;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;

@Mod.EventBusSubscriber
public class JERIntegration {

    public static void init() {

        // Api Fetching
        IJERAPI jerApi = JERAPI.getInstance();
        IMobRegistry jerMobRegistry = jerApi.getMobRegistry();
        World jerWorld = jerApi.getWorld();


        // ==================================================================================
        // ================================ Default Witchery ================================
        // ==================================================================================

        jerMobRegistry.register(new EntityHellhound(jerWorld), LootTables.HELLHOUND);

        jerMobRegistry.register(new EntityDemon(jerWorld), LootTables.DEMON);

        jerMobRegistry.register(new EntityHornedHuntsman(jerWorld), LootTables.HORNED_HUNTSMAN);
        jerMobRegistry.registerRenderHook(EntityHornedHuntsman.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityMandrake(jerWorld), LootTables.MANDRAKE);

        jerMobRegistry.register(new EntityOwl(jerWorld), LootTables.OWL);

        jerMobRegistry.register(new EntityToad(jerWorld), LootTables.TOAD);
        jerMobRegistry.registerRenderHook(EntityToad.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.translate(0.0f, 0.5f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityTreefyd(jerWorld), LootTables.TREEFYD);
        jerMobRegistry.registerRenderHook(EntityTreefyd.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityVillageGuard(jerWorld), LootTables.VILLAGE_GUARD);

        jerMobRegistry.register(new EntityWerewolf(jerWorld), LootTables.WEREWOLF);
        jerMobRegistry.registerRenderHook(EntityWerewolf.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityWitchHunter(jerWorld), LootTables.WITCH_HUNTER);



        // ==================================================================================
        // ================================ Custom registers ================================
        // ==================================================================================

        jerMobRegistry.register(new EntityBabaYaga(jerWorld), LootTables.BABA_YAGA_DEATH);
        jerMobRegistry.registerRenderHook(EntityBabaYaga.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            GlStateManager.scale(0.7f, 0.7f, 0.7f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityCatFamiliar(jerWorld),
                ModConfig.PatchesConfiguration.LootTweaks.familiarCat_tweakOwnLootTable ?
                        LootTables.FAMILIAR_CAT : LootTableList.ENTITIES_OCELOT);

        jerMobRegistry.register(new EntityCovenWitch(jerWorld),
                ModConfig.PatchesConfiguration.LootTweaks.covenWitch_tweakOwnLootTable ?
                        LootTables.COVEN_WITCH : LootTableList.ENTITIES_WITCH);

        jerMobRegistry.register(new EntityDeath(jerWorld), LootTables.DEATH);
        jerMobRegistry.registerRenderHook(EntityDeath.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityGoblinMog(jerWorld), LootTables.GOBLIN_MOG);
        jerMobRegistry.registerRenderHook(EntityGoblinMog.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityGoblinGulg(jerWorld), LootTables.GOBLIN_GULG);
        jerMobRegistry.registerRenderHook(EntityGoblinGulg.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityEnt(jerWorld), LootTables.ENT);
        jerMobRegistry.registerRenderHook(EntityEnt.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            GlStateManager.scale(0.65f, 0.65f, 0.65f);
            return renderInfo;
        });

    }


}
