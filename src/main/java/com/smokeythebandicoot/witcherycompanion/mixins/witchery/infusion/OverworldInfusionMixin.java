package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.OverworldInfusionApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityWitchProjectile;
import net.msrandom.witchery.infusion.Infusion;
import net.msrandom.witchery.infusion.OtherwhereInfusion;
import net.msrandom.witchery.infusion.OverworldInfusion;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.EarthItems;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Earth Infusion API for throwable rocks
 */
@Mixin(OverworldInfusion.class)
public abstract class OverworldInfusionMixin extends Infusion {

    @Shadow(remap = false)
    protected abstract boolean isThrowableRock(World world, BlockPos pos, EnumFacing sideHit);


    @Unique
    private EntityLivingBase witcherycompanion$capturedLeftClickedEntity = null;


    /** Injects EarthInfusionAPI for throwable blocks **/
    @Inject(method = "isThrowableRock", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectEarthInfusionApi(World world, BlockPos pos, EnumFacing sideHit, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.InfusionTweaks.overworldInfusion_tweakEnableCrafttweakerCompat) {
            cir.setReturnValue(
                    OverworldInfusionApi.isThrowable(world.getBlockState(pos)) && // Is valid material
                    !world.getBlockState(pos.offset(sideHit.getOpposite())).getMaterial().isSolid() // No obstructions
            );
        }
    }

    /** This Mixin replaces the entire logic of the onPlayerStoppedUsing function to inject OverworldInfusionAPI calls
     * and to fix the column raising bug **/
    @Inject(method = "onPlayerStoppedUsing", remap = false, at = @At("HEAD"), cancellable = true)
    private void fixOverworldInfusionColumn(ItemStack itemstack, World world, EntityPlayer player, int countdown, CallbackInfo ci) {

        if (!world.isRemote) {
            int elapsedTicks = this.getMaxItemUseDuration() - countdown;
            RayTraceResult hit = OtherwhereInfusion.doCustomRayTrace(world, player, true, 4.0);
            int DEPTH;

            if (hit != null) {
                switch (hit.typeOfHit) {
                    case ENTITY:
                        if (!player.isSneaking() && hit.entityHit instanceof EntityLiving && this.consumeCharges(world, player, 2)) {
                            witcherycompanion$performEntityHit((EntityLiving)hit.entityHit);
                            ci.cancel();
                            return;
                        }
                        break;
                    case BLOCK:
                        if ( // RAISE COLUMN
                                !player.isSneaking() &&
                                hit.sideHit == EnumFacing.UP &&
                                world.getBlockState(hit.getBlockPos().down(10)).getMaterial().isSolid() &&
                                        this.consumeCharges(world, player, 2)
                        ) {
                            witcherycompanion$raiseColumn(world, hit);
                        }

                        else if ( // THROW ROCK
                                !player.isSneaking() &&
                                hit.sideHit != EnumFacing.DOWN &&
                                hit.sideHit != EnumFacing.UP
                        ) {
                            witcherycompanion$throwRock(world, player, hit);
                        }

                        else if ( // Extract ingot
                                player.isSneaking() &&
                                this.consumeCharges(world, player, 2)
                        ) {
                            witcherycompanion$convertOre(world, hit);
                        }

                        ci.cancel();
                        return;
                }
            }

            DEPTH = elapsedTicks / 20;
            if (DEPTH >= 2 && !player.isSneaking() && this.consumeCharges(world, player, 6 * DEPTH)) {
                OverworldInfusion.Shockwave.SHOCKWAVES.add(new OverworldInfusion.Shockwave(player, 2 * DEPTH));
            } else {
                this.playFailSound(world, player);
            }

        }

        ci.cancel();
    }

    /** This Mixin captures the left-clicked entity for use in the 'injectMetalEntityPulling' method of this Mixin **/
    @WrapOperation(method = "onLeftClickEntity", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/entity/EntityLivingBase;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack captureLeftClickedEntity(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot, Operation<ItemStack> original) {
        witcherycompanion$capturedLeftClickedEntity = instance;
        return original.call(instance, entityEquipmentSlot);
    }

    /** This Mixin into the isMatch call to check if the Entity belongs to a registry of Metal entities that
     * should be knock-backed even if they are not wearing any metallic armor (for example, Iron golems).
     * If the entity is metallic, it returns true as if the entity was wearing metal armor
     * NOTE: the isMatch is mixin-ed itself **/
    @WrapOperation(method = "onLeftClickEntity", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/EarthItems;isMatch(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean injectMetalEntityPulling(EarthItems instance, ItemStack stack, Operation<Boolean> original) {
        if (ModConfig.PatchesConfiguration.InfusionTweaks.overworldInfusion_tweakEnableCrafttweakerCompat) {
            // If entity belongs to registry, then return true
            // NOTE: no need to check if captured entity is null, as it is done API-side
            if (OverworldInfusionApi.isMetalEntity(witcherycompanion$capturedLeftClickedEntity)) {
                return true;
            }
        }
        return original.call(instance, stack);
    }


    @Unique
    private void witcherycompanion$performEntityHit(EntityLiving entity) {
        ItemStack mainItem = entity.getHeldItemMainhand();
        ItemStack offItem = entity.getHeldItemOffhand();
        if (EarthItems.instance().isMatch(mainItem)) {
            entity.entityDropItem(mainItem, 2.0F);
            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        if (EarthItems.instance().isMatch(offItem)) {
            entity.entityDropItem(offItem, 2.0F);
            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Unique
    private void witcherycompanion$raiseColumn(World world, RayTraceResult hit) {

        BlockPos origin = hit.getBlockPos();
        int shift = 3; // How high will the blocks raise at most?
        int depth = 10; // How many blocks will shift upwards?

        // Check for the column below, there must be at least 10 solid, movable blocks (without TileEntities)
        for (int h = 0; h < depth; h++) {
            IBlockState state = world.getBlockState(origin.down(h));
            if (!WitcheryUtils.canBreak(state) || !state.getMaterial().isSolid()) {
                return; // Cannot move the whole column
            }
        }

        // Check for space above, must be replaceable
        for (int offset = 1; offset < 4; offset++) {
            BlockPos offPos = origin.up(offset);

            // Cannot shift upwards
            if (!world.getBlockState(offPos).getMaterial().isReplaceable()) {
                shift = offset - 1;

                // No space to raise the column
                if (shift == 0) {
                    return;
                }
                break;
            }
        }

        // Raise the column, shifting blocks upwards (column does not create new blocks)
        for (int d = 0; d < depth + 3; d++) {
            BlockPos sourcePos = origin.down(d);
            BlockPos targetPos = sourcePos.up(shift);
            if (d < depth) {
                world.setBlockState(targetPos, world.getBlockState(sourcePos), 3);
            }
            else {
                world.setBlockToAir(targetPos);
            }
        }

        // Shift entities upwards
        AxisAlignedBB bounds = new AxisAlignedBB(
                hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(),
                (hit.getBlockPos().getX() + 1), (hit.getBlockPos().getY() + 2), (hit.getBlockPos().getZ() + 1));

        for (Entity entity2 : world.getEntitiesWithinAABB(Entity.class, bounds)) {
            if (entity2 instanceof EntityLivingBase) {
                entity2.setPositionAndUpdate(entity2.posX, entity2.posY + 3.0, entity2.posZ);
            } else {
                entity2.setPosition(entity2.posX, entity2.posY + 3.0, entity2.posZ);
            }
        }

        /*
        for(int h = 0; h < 6; ++h) {
            int originY = origin.getY() - 3;
            IBlockState state = world.getBlockState(hit.getBlockPos());
            if (WitcheryUtils.canBreak(state)) {
                world.setBlockToAir(hit.getBlockPos());
                BlockPos target = new BlockPos(origin.getX(), originY, origin.getZ());
                if (WitcheryUtils.canBreak(world.getBlockState(target))) {
                    world.setBlockState(target, state, 3);
                }

                AxisAlignedBB bounds = new AxisAlignedBB(
                        hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(),
                        (hit.getBlockPos().getX() + 1), (hit.getBlockPos().getY() + 2), (hit.getBlockPos().getZ() + 1));

                for (Entity entity2 : world.getEntitiesWithinAABB(Entity.class, bounds)) {
                    if (entity2 instanceof EntityLivingBase) {
                        entity2.setPositionAndUpdate(entity2.posX, entity2.posY + 3.0, entity2.posZ);
                    } else {
                        entity2.setPosition(entity2.posX, entity2.posY + 3.0, entity2.posZ);
                    }
                }
            }
        }
        */
    }

    @Unique
    private void witcherycompanion$throwRock(World world, EntityPlayer player, RayTraceResult hit) {
        if (this.isThrowableRock(world, hit.getBlockPos(), hit.sideHit) && this.consumeCharges(world, player, 3)) {
            world.setBlockToAir(hit.getBlockPos());
            world.playSound(null, hit.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllAround(new PacketParticles(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), 0.5F, 0.5F, EnumParticleTypes.EXPLOSION_NORMAL), world, hit.getBlockPos(), 8.0);
            EntityWitchProjectile rockEntity = new EntityWitchProjectile(world, player, new ItemStack(WitcheryIngredientItems.ROCK));
            rockEntity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            rockEntity.setPosition((double)hit.getBlockPos().getX() + 0.5, (double)hit.getBlockPos().getY() + 0.5, (double)hit.getBlockPos().getZ() + 0.5);
            world.spawnEntity(rockEntity);
        }
    }

    @Unique
    private void witcherycompanion$convertOre(World world, RayTraceResult hit) {
        IBlockState state = world.getBlockState(hit.getBlockPos());
        BlockPos pos = hit.getBlockPos();
        OverworldInfusionApi.OreTransformationInfo info = OverworldInfusionApi.getOreTransformation(world, pos, state);

        if (info != null && !info.target.isEmpty()) {
            world.setBlockState(pos, info.leftOver);
            world.spawnEntity(new EntityItem(world, hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ(), info.target));
        }
    }


}
