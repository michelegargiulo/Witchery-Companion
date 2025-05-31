package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.ai;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.SpectralFamiliarApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectralfamiliar.IEntitySpectralFamiliarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import net.msrandom.witchery.entity.ai.EntityAIFamiliarFindDiamonds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;


/**
 * Mixins:
 * [Integration] Spectral Familiar CraftTweaker integration
 */
@Mixin(EntityAIFamiliarFindDiamonds.class)
public abstract class EntityAIFamiliarFindDiamondMixin {

    @Shadow(remap = false) @Final
    private EntitySpectralFamiliar spectralFamiliar;


    @Shadow(remap = false)
    protected abstract boolean getNearbySitableBlockDistance();

    /** This Mixin replaces logic of isSittableBlock. Only the blockstate corresponding to
     * the sniffed item should be considered sittable **/
    @Inject(method = "isSittableBlock", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectSittableBlocks(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {

            if (this.spectralFamiliar instanceof IEntitySpectralFamiliarAccessor) {
                IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) this.spectralFamiliar;
                ItemStack sniffed = accessor.witcherycompanion$accessor$getSniffedItem();

                Set<IBlockState> target = SpectralFamiliarApi.getOre(sniffed);
                IBlockState found = world.getBlockState(new BlockPos(x, y, z));

                // Found should never be null, but just in case, we avoid the case of null == null
                // by checking if target is not null
                cir.setReturnValue(target != null && target.contains(found));
                return;
            }

            cir.setReturnValue(false);
        }
    }

    /** If CrTweaker compat is enabled, we should not check if blockIdToFind is not null, but if
     * sniffed item is not null instead **/
    @Inject(method = "shouldExecute", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectShouldExecute(CallbackInfoReturnable<Boolean> cir) {
        if (EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {

            EntityLivingBase owner = this.spectralFamiliar.getOwner();
            IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) this.spectralFamiliar;
            ItemStack sniffed = accessor.witcherycompanion$accessor$getSniffedItem();


            cir.setReturnValue(
                    this.spectralFamiliar.isTamed() &&
                    !this.spectralFamiliar.isSitting() &&
                    sniffed != null &&
                    sniffed != ItemStack.EMPTY &&
                    owner != null &&
                    this.spectralFamiliar.getDistanceSq(owner) < 100.0D &&
                    this.spectralFamiliar.getRNG().nextDouble() <= 0.1D &&
                    this.getNearbySitableBlockDistance()
            );
        }
    }

    /** This Mixin clears EntitySpectralFamiliar sniffed item when it finds an ore,
     * regardless if CraftTweaker compat is enabled or not **/
    @WrapOperation(method = "updateTask", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/entity/EntitySpectralFamiliar;clearItemToFind()V"))
    private void clearItemWhenOreFound(EntitySpectralFamiliar instance, Operation<Void> original) {
        // Clear itemToFind, like original code
        original.call(instance);

        // But also clear injected data
        if (instance instanceof IEntitySpectralFamiliarAccessor) {
            IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) instance;
            accessor.witcherycompanion$accessor$setSniffedItem(ItemStack.EMPTY);
        }
    }

}
