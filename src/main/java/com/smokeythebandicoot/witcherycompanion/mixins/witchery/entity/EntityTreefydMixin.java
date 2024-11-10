package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.TreefydApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.treefyd.IEntityTreefydAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.LootTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityTreefyd;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Tweak Maximum health and damage (base, creeper heart and demon heart)
 * [Tweak] Tweak to require a Creeper Heart to be used before a Demon heart can be applied
 * [Tweak] Give Own Loot Table
 */
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityTreefyd.class)
public abstract class EntityTreefydMixin extends EntityMob implements IEntityOwnable, IEntityTreefydAccessor {

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final DataParameter<Byte> BOOST_LEVEL = EntityDataManager.createKey(EntityTreefyd.class, DataSerializers.BYTE);


    private EntityTreefydMixin(World worldIn) {
        super(worldIn);
    }


    @Inject(method = "entityInit", remap = true, at = @At("TAIL"))
    private void injectNewDataParameter(CallbackInfo ci) {
        this.dataManager.register(BOOST_LEVEL, (byte)0);
    }

    @Override
    public int getBoostLevel() {
        return this.dataManager.get(BOOST_LEVEL);
    }


    /**
     * This Mixin Checks if the item is a Creeper Heart and the Treefyd is non-boosted. If true, boosts the Treefyd,
     * otherwise returns AIR to skip to the next item check
     **/
    @WrapOperation(method = "processInteract", remap = true, at = @At(value = "INVOKE", ordinal = 2, remap = false,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item rememberBoostWhenGaveCreeper(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (EntityTweaks.treefyd_tweakOverhaulUpgrades) {
            if (result == TreefydApi.getLevel1BoostItem() && this.dataManager.get(BOOST_LEVEL) == 0) {
                this.dataManager.set(BOOST_LEVEL, (byte) 1);
                return result;
            } else {
                this.witchery_Patcher$spawnSmokeParticles();
                return Items.AIR;
            }
        }
        return result;
    }

    /**
     * This Mixin checks if the item is a Demon Heart and the Treefyd is boosted with a Creeper Heart. If true, boosts it,
     * otherwise returns AIR to skip to the next item check
     **/
    @WrapOperation(method = "processInteract", remap = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 3))
    private Item requireCreeperBeforeDemon(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (EntityTweaks.treefyd_tweakOverhaulUpgrades) {
            if (result == TreefydApi.getLevel2BoostItem() && this.dataManager.get(BOOST_LEVEL) == 1) {
                this.dataManager.set(BOOST_LEVEL, (byte) 2);
                return result;
            } else {
                this.witchery_Patcher$spawnSmokeParticles();
                // Return air to cancel interaction
                return Items.AIR;
            }
        }
        return result;
    }

    @Unique
    @SuppressWarnings("deprecation")
    private void witchery_Patcher$spawnSmokeParticles() {
        double x = this.getPosition().getX();
        double y = this.getPosition().getY();
        double z = this.getPosition().getZ();
        // Spawn Smoke particles at position
        WitcheryNetworkChannel.sendToAllAround(new PacketParticles(x + 0.5, y + 0.5, z + 0.5,
                0.5f, 1.0f, EnumParticleTypes.SMOKE_NORMAL), world, 0.5 + x, 0.5 + y, 0.5 + z);
    }

    @ModifyConstant(method = "processInteract", remap = false, constant = @Constant(doubleValue = 100.0))
    private double tweakLevel1HealthBoost(double constant) {
        return EntityTweaks.treefyd_tweakHealthWithCreeperHeart;
    }

    @ModifyConstant(method = "processInteract", remap = false, constant = @Constant(doubleValue = 4.0))
    private double tweakLevel1DamageBoost(double constant) {
        return EntityTweaks.treefyd_tweakDamageWithCreeperHeart;
    }

    @ModifyConstant(method = "processInteract", remap = false, constant = @Constant(doubleValue = 150.0))
    private double tweakLevel2HealthBoost(double constant) {
        return EntityTweaks.treefyd_tweakHealthWithDemonHeart;
    }

    @ModifyConstant(method = "processInteract", remap = false, constant = @Constant(doubleValue = 5.0))
    private double tweakLevel2DamageBoost(double constant) {
        return EntityTweaks.treefyd_tweakDamageWithDemonHeart;
    }

    @Inject(method = "applyEntityAttributes", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakBaseAttributeValues(CallbackInfo ci) {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(EntityTweaks.treefyd_tweakSpeedUnboosted);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(EntityTweaks.treefyd_tweakDamageUnboosted);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(EntityTweaks.treefyd_tweakHealthUnboosted);
        ci.cancel();
    }

    @Inject(method = "getDropItem", remap = false, cancellable = true, at = @At("HEAD"))
    private void giveOwnLootTable(CallbackInfoReturnable<Item> cir) {
        if (LootTweaks.treefyd_tweakOwnLootTable) {
            cir.setReturnValue(null);
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return LootTweaks.treefyd_tweakOwnLootTable ? LootTables.TREEFYD : null;
    }


}
