package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.cauldron.ITileEntityCauldronAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.api.CauldronApi;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.CauldronBrewData;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.init.WitcheryFluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 Mixins:
 [Integration] Crafttweaker integration for Cauldron (heat sources)
 [Bugfix] Fix exploit when right-clicking an empty bucket on an empty cauldron, producing a water bucket
 [Bugfix] Cauldron recipes can see ingredients that are not brewing ingredients (needed for custom recipes)
 [Integration] The One Probe can list every ingredient in the cauldron, not only brewing ones
 */
@Mixin(TileEntityCauldron.class)
public abstract class TileEntityCauldronMixin implements ITileEntityCauldronAccessor {

    @Shadow(remap = false)
    private CauldronBrewData brewData;

    @Shadow(remap = false)
    public abstract int getLiquidQuantity();

    /** Every stack accepted by the cauldron since its last reset, in order. Not persisted by Witchery, and not
     * cleared when the cauldron empties, so it is only used to detect which stacks are still "inside" */
    @Shadow(remap = false) @Final
    private List<ItemStack> addedStacks;

    /** Stacks accepted as ingredients that have NO brew action. Witchery only stores brewing ingredients in
     * brewData.actions.items (the only list CauldronRecipe.craft reads), so these would otherwise be invisible
     * to recipes. Deliberately NOT stored in brewData, whose items/actions lists must stay index-parallel.
     * Not persisted: after a server restart mid-recipe they are lost and the recipe fails gracefully */
    @Unique
    private final List<ItemStack> witcherycompanion$nonBrewStacks = new ArrayList<>();

    /** The brewData instance that witcherycompanion$nonBrewStacks belongs to. Witchery creates a new CauldronBrewData
     * every time the cauldron empties (drain) or is loaded from NBT, so a different instance means "new content" */
    @Unique
    private CauldronBrewData witcherycompanion$nonBrewStacksOwner = null;

    @Unique
    private float witchery_Patcher$currentPowerNeeded = -1.0f;


    /** Since Witchery has hardcoded check to if the block below is FIRE, we will use this
     * as the "true" return value for the method. Anything else is considered "false" */
    @WrapOperation(method = "update", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;", remap = true))
    public Block injectHeatSourcesApi(IBlockState instance, Operation<Block> original) {
        if (BlockTweaks.witchsCauldron_tweakCustomHeatSources) {
            return CauldronApi.isHeatSource(instance) ? CauldronApi.getFireBlock() : Blocks.AIR;
        }
        return original.call(instance);
    }

    /** This Mixin fixes an exploit that allows players to get unlimited water buckets by right-clicking the cauldron
     with an empty bucket, due to the TankProperties always reporting the *potential* fluid capacity instead of the
     *actual* fluid inside. The Witchery.BREW always reports max capacity because the caludron should never accept more
     brew, because other fluids might have different NBTs */
    @Inject(method = "getTankProperties", remap = false, cancellable = true, at = @At("HEAD"))
    public void fixUnlimitedWaterExploit(CallbackInfoReturnable<IFluidTankProperties[]> cir) {
        if (BlockTweaks.witchsCauldron_fixUnlimitedWaterWhenEmpty) {
            FluidStack fluidStack = null;
            if (this.brewData.getActions().size() > 0) {
                fluidStack = new FluidStack(WitcheryFluids.BREW, 3000);
            } else if (this.getLiquidQuantity() > 0) {
                fluidStack = new FluidStack(FluidRegistry.WATER, this.getLiquidQuantity());
            }
            cir.setReturnValue(new IFluidTankProperties[]{new FluidTankProperties(fluidStack, 3000)});
        }
    }

    @WrapOperation(method = "update", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;getNeededPower(ILjava/util/concurrent/atomic/AtomicInteger;)F"))
    private float saveCurrentPowerNeeded(TileEntityCauldron instance, int currentPower, AtomicInteger risk, Operation<Float> original) {
        witchery_Patcher$currentPowerNeeded = original.call(instance, currentPower, risk);
        return witchery_Patcher$currentPowerNeeded;
    }

    /** Records ingredients that have no brew action. Injected right before addItem stores the stack in addedStacks,
     * which is the point where the item has already been accepted (canNotAdd passed). */
    @Inject(method = "addItem", remap = false, at = @At(value = "FIELD", opcode = 180 /* GETFIELD */, remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;addedStacks:Ljava/util/List;"))
    private void witcherycompanion$trackNonBrewIngredients(BrewAction action, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        this.witcherycompanion$syncNonBrewStacks();
        if (action == null) {
            // Same instance that Witchery is about to put in addedStacks, so we can tell later if it was given back
            this.witcherycompanion$nonBrewStacks.add(stack);
        }
    }

    @Unique
    private void witcherycompanion$syncNonBrewStacks() {
        if (this.witcherycompanion$nonBrewStacksOwner != this.brewData) {
            this.witcherycompanion$nonBrewStacks.clear();
            this.witcherycompanion$nonBrewStacksOwner = this.brewData;
        }
    }

    /** When a recipe fails Witchery gives the trigger back and removes it from addedStacks (by reference) */
    @Unique
    private void witcherycompanion$purgeGivenBackStacks() {
        for (Iterator<ItemStack> it = this.witcherycompanion$nonBrewStacks.iterator(); it.hasNext(); ) {
            if (!witcherycompanion$containsInstance(this.addedStacks, it.next())) it.remove();
        }
    }

    @Unique
    private static boolean witcherycompanion$containsInstance(List<ItemStack> list, ItemStack stack) {
        for (ItemStack other : list) {
            if (other == stack) return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> witcherycompanion$accessor$getAllIngredients() {
        this.witcherycompanion$syncNonBrewStacks();
        this.witcherycompanion$purgeGivenBackStacks();

        List<ItemStack> brewStacks = this.brewData.getActions().items;
        List<ItemStack> ingredients = new ArrayList<>();

        // Brewing ingredients that addedStacks does not know about (it is not saved: the cauldron was loaded from
        // NBT) were necessarily added before anything that addedStacks knows about
        for (ItemStack stack : brewStacks) {
            if (!witcherycompanion$containsInstance(this.addedStacks, stack)) ingredients.add(stack);
        }

        // addedStacks holds the real order of arrival, but also leftovers of previous contents (it is never
        // cleared), so only keep what is actually part of the current brew data or tracked as non-brew
        for (ItemStack added : this.addedStacks) {
            if (witcherycompanion$containsInstance(brewStacks, added)
                    || witcherycompanion$containsInstance(this.witcherycompanion$nonBrewStacks, added)) {
                ingredients.add(added);
            }
        }
        return ingredients;
    }

    @Override
    public List<ItemStack> witcherycompanion$accessor$getCraftingPool() {
        this.witcherycompanion$syncNonBrewStacks();
        this.witcherycompanion$purgeGivenBackStacks();

        // The last added stack is the recipe trigger and must not count as one of its inputs
        ItemStack trigger = this.addedStacks.isEmpty() ? null : this.addedStacks.get(this.addedStacks.size() - 1);

        List<ItemStack> pool = new ArrayList<>();
        for (ItemStack stack : this.brewData.getActions().items) {
            if (stack != trigger) pool.add(stack);
        }
        for (ItemStack stack : this.witcherycompanion$nonBrewStacks) {
            if (stack != trigger) pool.add(stack);
        }
        return pool;
    }

    /** Accessor for 'getNeededPower'. Used by The One Probe */
    @Override
    public float witcherycompanion$accessor$getNeededPower() {
        return witchery_Patcher$currentPowerNeeded;
    }

}
