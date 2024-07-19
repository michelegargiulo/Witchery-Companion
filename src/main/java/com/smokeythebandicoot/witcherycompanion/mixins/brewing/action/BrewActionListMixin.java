package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action;

import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.BrewActionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BrewActionList.class)
public abstract class BrewActionListMixin {

    @Shadow(remap = false) @Final
    public List<BrewAction> actions;

    @Shadow(remap = false) @Final
    public List<ItemStack> items;

    /**
     * @author gg
     * @reason gg
     */
    @Overwrite(remap = false)
    public void applyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers) {
        for(int i = 0; i < this.actions.size(); ++i) {
            BrewAction action = this.actions.get(i);
            if (action.augmentEffectLevels(modifiers.effectLevel)) {
                action.augmentEffectModifiers(modifiers);
                modifiers.strength.setTotal(100);
                modifiers.duration.setTotal(100);
                //ModifiersEffect clone = ReflectionHelper.invokeMethod(modifiers, "witchery_Patcher$getCopy", new Class<?>[]{}, null, false);
                //if (clone == null) clone = modifiers;
                action.applyToEntity(world, targetEntity, modifiers, this.items.get(i));
            }
        }

    }

}
