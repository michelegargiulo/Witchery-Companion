package com.smokeythebandicoot.witcherypatcher.mixins.entity.passive.coven;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.init.WitcheryDimensions;
import net.msrandom.witchery.registry.WitcheryRegistry;
import net.msrandom.witchery.resources.CovenQuestManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

@Mixin(value = EntityCovenWitch.class, remap = false)
public abstract class EntityCovenWitchMixin extends EntityTameable {

    @Shadow
    private CovenQuest quest;

    @Shadow
    protected abstract Integer getQuestItemsNeeded();

    @Shadow @Final
    protected abstract void setQuestItemsNeeded(Integer var1);

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
    public void WPsetQuestItemsNeeded(EntityCovenWitch instance, Integer _set___, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_fixNegativeRequestAmount && _set___ < 0) {
            WitcheryPatcher.logger.warn("Redirecting SET QUEST to 0");
            original.call(instance, Integer.valueOf(0));
        } else {
            original.call(instance, _set___);
        }
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}
