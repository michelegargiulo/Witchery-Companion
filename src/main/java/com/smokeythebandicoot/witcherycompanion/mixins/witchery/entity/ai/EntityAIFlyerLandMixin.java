package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.ai.EntityAIFlyerLand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Fix broken AI
 */
@Mixin(EntityAIFlyerLand.class)
public abstract class EntityAIFlyerLandMixin {

    @Shadow(remap = false)
    protected abstract boolean isLanded();

    @Shadow(remap = false)
    protected abstract boolean liquidBelow(int y);

    @Shadow(remap = false) @Final
    EntityLiving living;

    @Shadow(remap = false) @Final
    World world;


    @Inject(method = "shouldExecute", remap = false, at = @At("HEAD"), cancellable = true)
    private void fixShouldExecute(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isLanded() && !this.liquidBelow((int)this.living.posY - 1) && !this.liquidBelow((int)this.living.posY) && this.world.rand.nextInt(20) == 0);
    }

    @Inject(method = "shouldContinueExecuting", remap = false, at = @At("HEAD"), cancellable = true)
    private void fixShouldContinueExecuting(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isLanded() && !this.liquidBelow((int)this.living.posY - 1) && !this.liquidBelow((int)this.living.posY));
    }

    @Inject(method = "liquidBelow", remap = false, at = @At("HEAD"), cancellable = true)
    private void fixLiquidBelow(int y, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.world.getBlockState(new BlockPos(this.living.posX, y, this.living.posZ)).getMaterial().isLiquid());
    }

}
