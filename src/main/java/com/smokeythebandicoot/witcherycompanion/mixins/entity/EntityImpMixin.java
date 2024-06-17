package com.smokeythebandicoot.witcherycompanion.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.msrandom.witchery.entity.EntityImp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.HashMap;
import java.util.List;

/**
 Mixins:
 [Tweak] Adds Crafttweaker compatibility to customize shinies list and gifts
 [Tweak] Adds LootTable for imp death and extra items
 [Tweak] Adds a custom delay between gifts
 [Tweak] Adds a tweak to avoid voiding items if the Imp is on gift cooldown
 */
@Mixin(EntityImp.class)
public abstract class EntityImpMixin extends EntityTameable {

    @Shadow(remap = false)
    private int secretsShared;

    @Shadow(remap = false)
    private long lastGiftTime;

    @Unique
    private static LootTable witchery_Patcher$impGiftTable;

    @Unique
    private ItemStack witchery_Patcher$capturedStack = null;

    private EntityImpMixin(World worldIn) {
        super(worldIn);
    }


    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.infernalImp_tweakLootTable ? LootTables.IMP_DEATH : null;
    }

    /**
     * This mixin captures player's held item for later use
     */
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;getHeldItem(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;", remap = true))
    public ItemStack captureHeldStack(EntityPlayer instance, EnumHand enumHand, Operation<ItemStack> original) {

        if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomShinies) {
            witchery_Patcher$capturedStack = original.call(instance, enumHand);
            return witchery_Patcher$capturedStack;
        }
        return original.call(instance, enumHand);
    }

    /** This mixin overrides the affection boost that Witchery would have
     * returned by the shinies.get() call with a call to InfernalImpApi.getAffectionBoost() instead */
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE",
            target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    public Object shiniesOverride(HashMap<Item, Integer> instance, Object o, Operation<Integer> original) {

        if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomShinies && witchery_Patcher$capturedStack != null) {
            // If the Imp is on cooldown, then affection boost should not be increased
            if (witchery_Patcher$isOnCooldown())
                return 0;
            int boost = InfernalImpApi.getAffectionBoost(witchery_Patcher$capturedStack);
            witchery_Patcher$capturedStack = null;               // Release capturedStack
            return boost == 0 ? null : boost;   // Must return null because EntityImp checks affectionBoost this way
        }
        return original.call(instance, o);
    }

    /** This mixin is required for the above mixin because Witchery performs an additional check to not give a gift
     * in return if the item's meta is not zero. This mixin overrides that behaviour, by always returning zero.
     * This ensures that the check is always true and metadata of shiny items can also be != 0 */
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItemDamage()I", remap = true))
    public int shiniesOverrideRemoveMetaCheck(ItemStack instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomShinies) {
            return 0;
        }
        return original.call(instance);
    }

    /** This mixin is the main logic for gift-giving. It wraps the world.spawnEntity( ... , entity) by overriding
     * the EntityItem to spawn just before this call. We can pass any EntityItem we want. In this case, we override
     * it depending on current configuration. Actual logic is commented inside the method */
    @SuppressWarnings("ConstantConditions")
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    public boolean giftsOverride(World instance, Entity entity, Operation<Boolean> original) {

        // If tweak is not enabled, just call original with original params
        if (!ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomGifts)
            return original.call(instance, entity);

        // The actual secret if the nth - 1 gift since the variable has been
        // incremented by Witchery by the time this code is executed
        int actualSecret = this.secretsShared - 1;

        // Otherwise, first try to retrieve a set gift.
        ItemStack newStack = InfernalImpApi.getGift(actualSecret);

        // Witchery increments this variable up to 4, then it never increments it again.
        // If tweak is enabled, increase this variable as it is used to give more gifts
        if (this.secretsShared > 3 && actualSecret <= InfernalImpApi.getLastGiftIndex())
            this.secretsShared += 1;

        // If not gift are present for the given secret, then generate one using the loot table
        // If newStack is not null, but is EMPTY, it will be treates as any other item, as it will be
        // interpreted as "INTENTIONALLY LEFT BLANK"
        if (newStack == null) {

            if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomExtraItems) {// First time retrieving the lootTable, retrieve it and cache it
                if (witchery_Patcher$impGiftTable == null) {
                    witchery_Patcher$impGiftTable = instance.getLootTableManager()
                            .getLootTableFromLocation(LootTables.IMP_GIFT);
                }

                // If loot table is not null, return the first item generated by it. If no items are generated,
                // newStack will still be null
                if (witchery_Patcher$impGiftTable != null) {
                    List<ItemStack> loot = witchery_Patcher$impGiftTable.generateLootForPools(instance.rand,
                            new LootContext(0.0f, (WorldServer) instance, world.getLootTableManager(),
                                    null, null, null));
                    if (!loot.isEmpty()) {
                        newStack = loot.get(0);
                    }
                }

            // We are not authorized to modify extra items: if shared secret is > 3, execute orignal method
            } else if (actualSecret > 3) {
                return original.call(instance, entity);

            // If not, it means that a set gift has been removed, but we are not authorized to replace it
            // with another one. We have no choice but to do nothing. Message will be displayed but nothing will be
            // given to the player.
            } else {
                return false;
            }
        }

        // If newStack is still null, it means that there are both no gifts set for the current secret, AND
        // the loot table failed. If enabled, fall back on what Witchery would have given, otherwise give up and give
        // nothing. Message will be displayed but nothing will be given. Arg all of this for nothing
        // This is also executed if this mixin is not allowed to change custom items
        if (newStack == null && ModConfig.PatchesConfiguration.EntityTweaks.flameinfernalImp_tweakCustomGiftFallback) {
            return original.call(instance, entity);
        }

        // newStack is not null, give it to the player in place of the original item
        if (newStack != null)
            return original.call(instance, new EntityItem(this.world, this.posX, this.posY, this.posZ, newStack));

        return false;
    }

    /** Prevents the item from shrinking if the IMP is on cooldown, avoiding resource wasting
     * Since ItemStack.shrink() is called many times (to shrink icy needle, bound contract, spell contract, etc.)
     * we only target the one that shrinks the "shiny" stack, located between the isBoundContract() call and
     * the world.spawnEntity call */
    @WrapOperation(method = "processInteract", remap = false,
            at = @At(value = "INVOKE", remap = true,
                target = "Lnet/minecraft/item/ItemStack;shrink(I)V"),
            slice =  @Slice(
                    from = @At(value = "INVOKE", remap = false,
                            target = "Lnet/msrandom/witchery/item/contracts/ItemActivatableContract;isBoundContract(Lnet/minecraft/item/ItemStack;)Z"),
                    to = @At(value = "INVOKE", remap = true,
                            target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")))
    public void dontShrinkStackIfOnCooldown(ItemStack instance, int i, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakItemConsumptionOnCooldown) {
            if (!witchery_Patcher$isOnCooldown()) {
                original.call(instance, i);
            }
        }
    }

    /** This mixin is required to overwrite the gift cooldown delay. Witchery uses a private static final int, and
     * it means that at compile-time each instance of the constant is replaced by the actual value. Using the
     * @ModifyConstant by MixinExtras to modify each occurrence of the constant */
    @ModifyConstant(method = "processInteract", remap = false, constant = @Constant(longValue = 3600L))
    public long modifyDelay(long constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakGiftDelayTicks;
    }

    /** Returns true if the Imp is on cooldown, that is, if the elapsed time in ticks is less then or equal to the
     number of config-defined cooldown ticks */
    @Unique
    private boolean witchery_Patcher$isOnCooldown() {
        return (MinecraftServer.getCurrentTimeMillis() / 50L) <= this.lastGiftTime + ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakGiftDelayTicks;
    }

}
