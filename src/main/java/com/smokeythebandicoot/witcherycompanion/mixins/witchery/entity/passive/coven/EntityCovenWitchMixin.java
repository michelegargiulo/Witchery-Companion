package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.passive.coven;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.covenwitch.ICovenWitchAccessor;
import com.smokeythebandicoot.witcherycompanion.api.player.PlayerExtendedDataApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.patches.coven.CovenQuestInteraction;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.entity.passive.coven.FightPetCovenQuest;
import net.msrandom.witchery.init.WitcheryDimensions;
import net.msrandom.witchery.registry.WitcheryRegistry;
import net.msrandom.witchery.resources.CovenQuestManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.UUID;

/**
 * Mixins:
 * [Bugfix] Fix quest number of items needed going into negatives
 * [Tweak] Introduce own loot table
 * [Feature] Unlock quest secrets
 * [Tweak] Configurable maximum coven size
 * [Feature] Unlock coven size secrets
 * [Feature] Dynamic quests: levels, weights and requirements, custom quests, events (see CovenQuestInteraction)
 * [Tweak] The other witches forget their quest when a witch joins the coven
 * [Bugfix] Peaceful rerolls of fight quests looped forever when only fight quests were left
 */
@ParametersAreNonnullByDefault
@Mixin(value = EntityCovenWitch.class)
public abstract class EntityCovenWitchMixin extends EntityTameable implements ICovenWitchAccessor {

    @Shadow(remap = false)
    private CovenQuest quest;

    @Shadow(remap = false)
    protected abstract Integer getQuestItemsNeeded();

    @Shadow(remap = false) @Final
    protected abstract void setQuestItemsNeeded(Integer var1);

    @Shadow(remap = false)
    protected abstract boolean isCovenFull(EntityPlayer player);

    @Shadow(remap = false)
    protected abstract UUID getQuestOfferedTo();

    @Shadow(remap = false)
    protected abstract void setQuestOfferedTo(UUID player);

    @Shadow(remap = false)
    protected abstract Boolean isQuestAccepted();

    @Shadow(remap = false)
    protected abstract void setQuestAccepted(Boolean accepted);

    @Shadow(remap = false)
    protected abstract void say(String key, EntityPlayer player, Object... args);

    @Shadow(remap = false)
    protected abstract void talkQuest(String namespace, String path, EntityPlayer player);

    @Shadow(remap = false)
    protected abstract void playWitchTalk();

    @Shadow(remap = false)
    protected abstract boolean addToPlayerCoven(EntityPlayer player);

    @Shadow(remap = false)
    public abstract boolean isDrinkingPotion();

    @Unique
    private EntityPlayer currentInteractingPlayer;

    /** Coven size of the interacting player before the interaction: tells if a witch joined during it */
    @Unique
    private int witcherycompanion$covenSizeBefore;


    private EntityCovenWitchMixin(World worldIn) {
        super(worldIn);
    }


    @Inject(method = "processInteract", remap = true, cancellable = false, at = @At(value = "HEAD"))
    public void WPshrinkCorrectly(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        this.currentInteractingPlayer = player;
        this.witcherycompanion$covenSizeBefore = PlayerExtendedDataApi.getCovenSize(player);
    }


    @WrapOperation(method = "processInteract", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void WPshrinkCorrectly(ItemStack instance, int i, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_fixNegativeRequestAmount) {
            // Original is never called because the ItemStack.shrink() is called wrongly
            // Retrieve the maximum number of items to grab.
            // Cannot grab more than the items needed, cannot grab more than the stack
            // Value also should be coerced to >= 0, otherwise shrinking the stack will make it grow
            int value = this.quest != null ? Math.min(this.getQuestItemsNeeded(), instance.getCount()) : 1;
            if (value < 0) value = 0;
            instance.shrink(value);
            if (this.getQuestItemsNeeded() <= 0) {
                // Should set to 0, but later in the code this is set to (getQuestItemNeeded() - value)
                // and compared to 0 (not <= 0)
                this.setQuestItemsNeeded(value);
            }
        }
    }

    @WrapOperation(method = "processInteract", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/entity/passive/coven/EntityCovenWitch;setQuestItemsNeeded(Ljava/lang/Integer;)V"))
    public void WPsetQuestItemsNeeded(EntityCovenWitch instance, Integer value, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_fixNegativeRequestAmount && value < 0) {
            original.call(instance, Integer.valueOf(0));
        } else {
            original.call(instance, value);
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    // ---- ICovenWitchAccessor

    @Override
    public CovenQuest witcherycompanion$accessor$getQuest() { return this.quest; }

    @Override
    public void witcherycompanion$accessor$setQuest(CovenQuest quest) { this.quest = quest; }

    @Override
    public UUID witcherycompanion$accessor$getQuestOfferedTo() { return this.getQuestOfferedTo(); }

    @Override
    public void witcherycompanion$accessor$setQuestOfferedTo(UUID player) { this.setQuestOfferedTo(player); }

    @Override
    public boolean witcherycompanion$accessor$isQuestAccepted() { return Boolean.TRUE.equals(this.isQuestAccepted()); }

    @Override
    public void witcherycompanion$accessor$setQuestAccepted(boolean accepted) { this.setQuestAccepted(accepted); }

    @Override
    public int witcherycompanion$accessor$getQuestItemsNeeded() {
        Integer items = this.getQuestItemsNeeded();
        return items == null ? 0 : items;
    }

    @Override
    public void witcherycompanion$accessor$setQuestItemsNeeded(int items) { this.setQuestItemsNeeded(items); }

    @Override
    public void witcherycompanion$accessor$say(String key, EntityPlayer player) { this.say(key, player); }

    @Override
    public void witcherycompanion$accessor$talkQuest(String namespace, String path, EntityPlayer player) { this.talkQuest(namespace, path, player); }

    @Override
    public void witcherycompanion$accessor$playWitchTalk() { this.playWitchTalk(); }

    @Override
    public boolean witcherycompanion$accessor$isCovenFull(EntityPlayer player) { return this.isCovenFull(player); }

    @Override
    public boolean witcherycompanion$accessor$addToPlayerCoven(EntityPlayer player) { return this.addToPlayerCoven(player); }

    /** Takes over the offer and the accept of every quest, and the check of custom quests, so that quests are chosen by level,
     * weight and requirements, and the events are posted. The conditions are the ones with which W:R handles the interaction
     * (nothing on the client, nothing if the witch is tamed or drinking, nothing in the Spirit World). See CovenQuestInteraction */
    @Inject(method = "processInteract", remap = true, cancellable = true, at = @At(value = "HEAD"))
    private void witcherycompanion$handleQuestInteraction(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakQuestSystem) return;
        if (this.world.isRemote || this.isTamed() || this.isDrinkingPotion() || WitcheryDimensions.SPIRIT_WORLD.isInDimension(player)) return;

        if (CovenQuestInteraction.handle((EntityCovenWitch) (Object) this, this, player, hand)) {
            cir.setReturnValue(true);
        }
    }

    /** This Mixin makes so that Coven Witches have their own loot table **/
    @Inject(method = "getLootTable", at = @At("HEAD"), remap = false, cancellable = true)
    public void getLootTable(CallbackInfoReturnable<ResourceLocation> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.covenWitch_tweakOwnLootTable) {
            cir.setReturnValue(LootTables.COVEN_WITCH);
        }
    }

    /** Witchery considers the coven full with 6 witches (the only 6 in isCovenFull). The maximum is configurable, and
     * the "coven full" progress and quest rerolls follow it, as they use the same method */
    @ModifyConstant(method = "isCovenFull", remap = false, constant = @Constant(intValue = 6))
    private int witcherycompanion$maxCovenSize(int original) {
        return ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakMaxCovenSize;
    }

    /** This Mixin unlocks the corresponding quest's secret when the player completes a quest (a quest of W:R, the custom ones
     * are completed by CovenQuestInteraction), the secrets of the coven size reached, and ALL the quests when the player has a
     * full coven. The unlock ignores what the player already has, so it is fine that this also runs if the witch could not be
     * added. If the witch joined, the other witches forget their quest **/
    @Inject(method = "processInteract", remap = false, cancellable = false, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/entity/passive/coven/EntityCovenWitch;addToPlayerCoven(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private void unlockSecretQuestCompleted(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation questId = CovenQuestManager.INSTANCE.getRegistry().getKey(this.quest);
        CovenQuestInteraction.unlockJoinProgress(player, questId, this.isCovenFull(player));

        if (PlayerExtendedDataApi.getCovenSize(player) > this.witcherycompanion$covenSizeBefore) {
            CovenQuestInteraction.resetOtherQuests(player, (EntityCovenWitch) (Object) this);
        }
    }

    /** This Mixin forces Quest re-roll if it cannot be completed in peaceful mode. Has failsafe if it takes more than 5 attempts **/
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/registry/WitcheryRegistry;getRandom(Ljava/util/Random;)Ljava/lang/Object;"))
    private Object avoidFightsInPeaceful(WitcheryRegistry<ResourceLocation, CovenQuest> instance, Random random, Operation<ResourceLocation> original) {
        ResourceLocation result = original.call(instance, random);
        if (this.currentInteractingPlayer != null &&
                this.currentInteractingPlayer.getServer() != null && this.currentInteractingPlayer.getServer().getDifficulty() == EnumDifficulty.PEACEFUL &&
                ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakFightQuestsPeacefulRerolls > 0
        ) {
            int attempts = ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakFightQuestsPeacefulRerolls;
            CovenQuest quest = CovenQuestManager.INSTANCE.getRegistry().get(result);
            while (attempts > 0 && (quest instanceof FightPetCovenQuest)) {
                attempts--;
                result = CovenQuestManager.INSTANCE.getRegistry().getRandom(random);
                quest = CovenQuestManager.INSTANCE.getRegistry().get(result);
            }
        }
        return result;
    }
}
