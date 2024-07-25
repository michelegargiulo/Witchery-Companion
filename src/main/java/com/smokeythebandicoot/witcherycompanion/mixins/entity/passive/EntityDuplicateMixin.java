package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.EntityDuplicate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityDuplicate.class)
public abstract class EntityDuplicateMixin extends EntityCreature {

    private EntityDuplicateMixin(World worldIn) {
        super(worldIn);
    }

    protected ResourceLocation getLootTable() {
        return LootTables.DUPLICATE;
    }

}
