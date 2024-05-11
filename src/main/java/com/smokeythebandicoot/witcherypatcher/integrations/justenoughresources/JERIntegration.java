package com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources;

import com.smokeythebandicoot.witcherypatcher.mixins.entity.EntityGoblinMogMixin;
import com.smokeythebandicoot.witcherypatcher.utils.LootTables;
import jeresources.api.IJERAPI;
import jeresources.api.IMobRegistry;
import jeresources.api.render.IMobRenderHook;
import jeresources.compatibility.JERAPI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.msrandom.witchery.entity.*;
import net.msrandom.witchery.entity.monster.EntityHellhound;
import net.msrandom.witchery.entity.monster.EntityHornedHuntsman;
import net.msrandom.witchery.entity.monster.EntityMandrake;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.entity.passive.EntityToad;

@Mod.EventBusSubscriber
public class JERIntegration {

    public static void init() {

        // Api Fetching
        IJERAPI jerApi = JERAPI.getInstance();
        IMobRegistry jerMobRegistry = jerApi.getMobRegistry();
        World jerWorld = jerApi.getWorld();

        // Default Witchery
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


        // Custom registers
        jerMobRegistry.register(new EntityLordOfTorment(jerWorld), LootTables.LORD_OF_TORMENT);
        jerMobRegistry.registerRenderHook(EntityLordOfTorment.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.scale(0.7f, 0.7f, 0.7f);
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

        jerMobRegistry.register(new EntityGoblinMog(jerWorld), LootTables.GOBLIN_MOG);
        jerMobRegistry.registerRenderHook(EntityGoblinMog.class, (renderInfo, entityLivingBase) -> {
            GlStateManager.translate(0.0f, -0.35f, 0.0f);
            return renderInfo;
        });

    }


}
