package com.smokeythebandicoot.witcherypatcher.mixins.rite.effect;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.rite.RiteHandler;
import net.msrandom.witchery.rite.effect.RiteEffectBindFamiliar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = RiteEffectBindFamiliar.class, remap = false)
public class RiteEffectBindFamiliarMixin {

    @Final
    @Shadow
    private int radius;

    @Inject(method = "process", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;"))
    public void WPinjectPrints(World world, BlockPos position, int ticks, AtomicInteger stage, TileEntityCircle.ActivatedRitual ritual, CallbackInfoReturnable<RiteHandler.Result> cir) {

        HashSet<Entity> boundPlayers = new HashSet<>();
        AxisAlignedBB bounds = new AxisAlignedBB(position.add(-this.radius, 0, -this.radius), position.add(this.radius, 1, this.radius));

        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, bounds)) {
            Intrinsics.checkExpressionValueIsNotNull(entity, "entity");
            if (Familiars.canBeFamiliar(entity)) {
                Familiar<Entity> familiar = Familiars.getFamiliarInstance(entity);
                Entity owner = familiar.getOwner();

                if (owner == null) {
                    log(world, "OWNER IS NULL");
                }
                if (!(owner instanceof EntityPlayer)) {
                    log(world, "OWNER NOT A PLAYER");
                }
                if (!(entity.getDistance(position.getX(), position.getY(), position.getZ()) <= (double) this.radius)) {
                    log(world, "FAMILIAR TOO FAR");
                }
                if (owner.getDistance(position.getX(), position.getY(), position.getZ()) > (double) this.radius) {
                    log(world, "OWNER TOO FAR");
                }
                if (boundPlayers.contains(owner)) {
                    log(world, "OWNER ALREADY BOUND");
                }
            }
        }
    }

    @Unique
    private void log(World world, String msg) {
        MinecraftServer server = world.getMinecraftServer();
        server.commandManager.executeCommand(server, "/tell @a " + msg);
    }

}
