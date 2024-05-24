package com.smokeythebandicoot.witcherycompanion.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 Mixins:
 [Tweak] Since bottling skill is not implemented in W:R, then do not print it when using the Seer Stone
 */
@Mixin(net.msrandom.witchery.item.ItemSeerStone.class)
public class ItemSeerStoneMixin {

    /**
     * Targets the "appendSibling" method, but since there are so many instances of the method, we make sure that this
     * code is injected AFTER the bottling skill retrieval and BEFORE the "Curse.REGISTRY.iterator()" call.
     * This should only target the "appendSibling" call that appends Throwing Skill. The mixin itself just cancels the
     * method by wrapping the original operation and returning the instance (result of previous call in the chain)
     */
    @WrapOperation(method = "readPlayer", remap = false,
            at = @At(value = "INVOKE", remap = true,
                target = "Lnet/minecraft/util/text/ITextComponent;appendSibling(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/ITextComponent;"),
            slice = @Slice(
                from = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/extensions/PlayerExtendedData;getThrowingSkill()I", remap = false),
                to = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/registry/WitcheryIdentityRegistry;iterator()Ljava/util/Iterator;", remap = false)))
    public ITextComponent WPunprintThrowingSkills(ITextComponent instance, ITextComponent iTextComponent, Operation<ITextComponent> original) {
        if (ModConfig.PatchesConfiguration.ItemTweaks.seerStone_tweakUnprintThrowingSkill) {
            return instance;
        }
        return original.call(instance, iTextComponent);
    }

    /**
     * Targets the same portion of code as above, but this time it targets the "appendText" call to cancel it,
     * preventing the injection of an additional blank line
     */
    @WrapOperation(method = "readPlayer", remap = false,
            at = @At(value = "INVOKE", remap = true,
                target = "Lnet/minecraft/util/text/ITextComponent;appendText(Ljava/lang/String;)Lnet/minecraft/util/text/ITextComponent;"),
            slice = @Slice(
                from = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/extensions/PlayerExtendedData;getThrowingSkill()I", remap = false),
                to = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/registry/WitcheryIdentityRegistry;iterator()Ljava/util/Iterator;", remap = false)))
    public ITextComponent WPunprintLineFeedChar(ITextComponent instance, String s, Operation<ITextComponent> original) {
        if (ModConfig.PatchesConfiguration.ItemTweaks.seerStone_tweakUnprintThrowingSkill) {
            return instance;
        }
        return original.call(instance, s);
    }

}
