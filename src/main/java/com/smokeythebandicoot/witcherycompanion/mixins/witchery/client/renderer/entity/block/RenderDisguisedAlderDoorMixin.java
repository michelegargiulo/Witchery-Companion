package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.entity.TileEntityAlderDoor;
import net.msrandom.witchery.client.renderer.entity.block.RenderDisguisedAlderDoor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderDisguisedAlderDoor.class)
public abstract class RenderDisguisedAlderDoorMixin extends TileEntitySpecialRenderer {

    @Inject(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityAlderDoor;DDDFIF)V", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixTileEntityRendering(TileEntityAlderDoor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.BlockTweaks.alderDoor_fixDisguiseRendering) return;

        super.render((TileEntity)te, x, y, z, partialTicks, destroyStage, alpha);
        IBlockState normal = te.getWorld().getBlockState(te.getPos());
        BlockPos lower = normal.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ? te.getPos().down() : te.getPos();
        BlockPos upper = normal.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? te.getPos().up() : te.getPos();
        IBlockState lowerState = Intrinsics.areEqual(lower, te.getPos()) ? normal : this.getWorld().getBlockState(lower);
        IBlockState upperState = Intrinsics.areEqual(upper, te.getPos()) ? normal : this.getWorld().getBlockState(upper);
        Block var10000 = te.getDisguise();
        if (var10000 == null) {
            var10000 = te.getBlockType();
        }

        IBlockState defaultState = var10000.getDefaultState();
        if (defaultState.getBlock() instanceof BlockDoor) {
            if (upperState.getBlock() instanceof BlockDoor) {
                if (Intrinsics.areEqual(normal.getBlock(), upperState.getBlock())) {
                    var10000 = normal.getBlock();
                    if (Intrinsics.areEqual(var10000, lowerState.getBlock())) {

                        IBlockState state = defaultState
                                .withProperty(BlockDoor.FACING, lowerState.getValue(BlockDoor.FACING))
                                .withProperty(BlockDoor.HALF, normal.getValue(BlockDoor.HALF))
                                .withProperty(BlockDoor.HINGE, upperState.getValue(BlockDoor.HINGE))
                                .withProperty(BlockDoor.OPEN, lowerState.getValue(BlockDoor.OPEN))
                                .withProperty(BlockDoor.POWERED, normal.getValue(BlockDoor.POWERED))
                                ;

                        GlStateManager.pushMatrix();
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuffer();
                        Minecraft var22 = Minecraft.getMinecraft();
                        BlockRendererDispatcher blockRendererDispatcher = var22.getBlockRendererDispatcher();
                        IBakedModel model = blockRendererDispatcher.getModelForState(state);
                        BlockPos var10001 = te.getPos();
                        double var24 = x - (double)var10001.getX();
                        BlockPos var10002 = te.getPos();
                        double var23 = y - (double)var10002.getY();
                        BlockPos var10003 = te.getPos();
                        GlStateManager.translate(var24, var23, z - (double)var10003.getZ());
                        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GlStateManager.disableLighting();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                        buffer.begin(7, DefaultVertexFormats.BLOCK);
                        blockRendererDispatcher.getBlockModelRenderer().renderModel((IBlockAccess)te.getWorld(), model, state, te.getPos(), buffer, true);
                        tessellator.draw();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.enableLighting();
                        GlStateManager.popMatrix();
                    }
                }
            }
        }

        ci.cancel();
    }

}
