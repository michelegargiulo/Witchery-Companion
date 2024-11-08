package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.block.entity.TileEntityWolfAltar;
import net.msrandom.witchery.block.entity.TileEntityWorshipStatue;
import net.msrandom.witchery.client.renderer.entity.block.RenderWolfAltar;
import net.msrandom.witchery.client.renderer.entity.block.RenderWorshipStatue;
import net.msrandom.witchery.client.renderer.entity.block.model.ModelWolfAltar;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Mixins:
 * [Bugfix] Render a small model if the small hitbox is enabled
 */
@Mixin(RenderWorshipStatue.class)
public abstract class RenderWorshipStatueMixin extends TileEntitySpecialRenderer<TileEntityWorshipStatue> {

    @Shadow(remap = false) @Final
    private ModelPlayer model;

    @Shadow(remap = false)
    private EntityPlayer fake;

    @Unique
    private static final ResourceLocation TEXTURE_FIXED  = new ResourceLocation("witchery", "textures/blocks/statue_of_worship.png");

    @Shadow(remap = false) @Final
    private static GameProfile MINECRAFT;


    @Inject(method = "render(Lnet/msrandom/witchery/block/entity/TileEntityWorshipStatue;DDDFIF)V", remap = false, at = @At("HEAD"), cancellable = true)
    public void render(TileEntityWorshipStatue entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.BlockTweaks.hobgoblinPatronStatue_fixRendering)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        this.model.isChild = true;
        this.model.leftArmPose = ModelBiped.ArmPose.ITEM;
        this.model.rightArmPose = ModelBiped.ArmPose.ITEM;
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        AbstractClientPlayer owner = (AbstractClientPlayer)entity.getOwner(this.getWorld());
        ResourceLocation skin = null;
        ModelPlayer mainModel;
        if (owner == null) {
            mainModel = this.model;
        } else {
            Render<?> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(owner);
            RenderPlayer playerRenderer = (RenderPlayer)render;
            mainModel = playerRenderer.getMainModel();
            skin = playerRenderer.getEntityTexture(owner);
        }

        if (skin == null) {
            skin = DefaultPlayerSkin.getDefaultSkinLegacy();
        }

        if (entity.hasWorld()) {
            GlStateManager.rotate(EnumFacing.byHorizontalIndex(entity.getBlockMetadata() & 3).getOpposite().getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        }

        this.bindTexture(skin);
        GlStateManager.color(0.7F, 0.7F, 0.7F);
        if (this.fake == null) {
            this.fake = new EntityOtherPlayerMP(this.getWorld(), owner == null ? MINECRAFT : owner.getGameProfile());
        }

        modelPlayerRender(mainModel, this.fake, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        //mainModel.render(this.fake, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.bindTexture(TEXTURE_FIXED);
        GlStateManager.color(0.8F, 0.8F, 0.8F);
        modelPlayerRender(mainModel, this.fake, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        //mainModel.render(this.fake, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625f);
        GlStateManager.popMatrix();
        ci.cancel();
    }

    private void modelPlayerRender(ModelPlayer modelPlayer, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        modelBipedRender(modelPlayer, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.pushMatrix();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
        modelPlayer.bipedLeftLegwear.render(scale);
        modelPlayer.bipedRightLegwear.render(scale);
        modelPlayer.bipedLeftArmwear.render(scale);
        modelPlayer.bipedRightArmwear.render(scale);
        modelPlayer.bipedBodyWear.render(scale);

        GlStateManager.popMatrix();
    }

    public void modelBipedRender(ModelBiped modelBiped, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        modelBiped.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        GlStateManager.pushMatrix();

        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
        modelBiped.bipedHead.render(scale);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
        modelBiped.bipedBody.render(scale);
        modelBiped.bipedRightArm.render(scale);
        modelBiped.bipedLeftArm.render(scale);
        modelBiped.bipedRightLeg.render(scale);
        modelBiped.bipedLeftLeg.render(scale);
        modelBiped.bipedHeadwear.render(scale);

        GlStateManager.popMatrix();
    }


}
