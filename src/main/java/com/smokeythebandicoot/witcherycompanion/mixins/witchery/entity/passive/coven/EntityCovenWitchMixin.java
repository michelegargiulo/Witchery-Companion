package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.passive.coven;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.resources.CovenQuestManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.Map;

/**
 Mixins:
 [Bugfix] Fix quest number of items needed going into negatives
 [Tweak] Introduce own loot table
 [Feature] Unlock quest secrets
 */
@ParametersAreNonnullByDefault
@Mixin(value = EntityCovenWitch.class)
public abstract class EntityCovenWitchMixin extends EntityTameable {

    @Shadow(remap = false)
    private CovenQuest quest;

    @Shadow(remap = false)
    protected abstract Integer getQuestItemsNeeded();

    @Shadow(remap = false) @Final
    protected abstract void setQuestItemsNeeded(Integer var1);

    @Shadow protected abstract boolean isCovenFull(EntityPlayer player);

    private EntityCovenWitchMixin(World worldIn) {
        super(worldIn);
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

    /** This Mixin makes so that Coven Witches have their own loot table **/
    @Inject(method = "getLootTable", at = @At("HEAD"), remap = false, cancellable = true)
    public void getLootTable(CallbackInfoReturnable<ResourceLocation> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.covenWitch_tweakOwnLootTable) {
            cir.setReturnValue(LootTables.COVEN_WITCH);
        }
    }

    /** This Mixin unlocks the corresponding quest's secret when the player completes a quest, and ALL of them when
     * the player has a full coven **/
    @Inject(method = "processInteract", remap = false, cancellable = false, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/entity/passive/coven/EntityCovenWitch;addToPlayerCoven(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private void unlockSecretQuestCompleted(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation questId = CovenQuestManager.INSTANCE.getRegistry().getKey(this.quest);
        if (questId != null) {
            ProgressUtils.unlockProgress(player, ProgressUtils.getCovenQuestSecret(questId),
                    WitcheryProgressEvent.EProgressTriggerActivity.COVEN_QUEST_FULFILLED.activityTrigger);
        }

        // To avoid player having some pages impossible to read (they're unlocked by completing quests, but quests are no
        // longer generated due to coven being full)
        if (this.isCovenFull(player)) {
            Iterator<Map.Entry<ResourceLocation, CovenQuest>> it = CovenQuestManager.INSTANCE.getRegistry().iterator();
            while (it.hasNext()) {
                ResourceLocation id = it.next().getKey();
                ProgressUtils.unlockProgress(player, ProgressUtils.getCovenQuestSecret(id),
                        WitcheryProgressEvent.EProgressTriggerActivity.COVEN_FULL.activityTrigger);
            }
        }

    }
}
