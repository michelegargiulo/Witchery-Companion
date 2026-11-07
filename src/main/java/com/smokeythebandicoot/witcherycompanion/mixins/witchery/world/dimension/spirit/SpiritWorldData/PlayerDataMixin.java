package com.smokeythebandicoot.witcherycompanion.mixins.witchery.world.dimension.spirit.SpiritWorldData;

import com.smokeythebandicoot.witcherycompanion.api.accessors.spiritworld.ISpiritPlayerDataAccessor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Spirit World extra slots (Baubles...): store the items taken out of the extra slots together with the
 * Overworld (original) and Spirit World data of the player, so that they are saved and loaded with them
 */
@Mixin(targets = "net.msrandom.witchery.world.dimension.spirit.SpiritWorldData$PlayerData")
public abstract class PlayerDataMixin implements ISpiritPlayerDataAccessor {

    @Unique
    private static final String witcherycompanion$EXTRA_SLOTS_KEY = "WitcheryCompanionExtraSlots";

    @Unique
    private NBTTagList witcherycompanion$extraSlots;

    @Override
    public NBTTagList witcherycompanion$accessor$getExtraSlots() {
        if (this.witcherycompanion$extraSlots == null) {
            this.witcherycompanion$extraSlots = new NBTTagList();
        }
        return this.witcherycompanion$extraSlots;
    }

    @Override
    public void witcherycompanion$accessor$setExtraSlots(NBTTagList list) {
        this.witcherycompanion$extraSlots = list;
    }

    /** Writes the stored items, if any, together with the rest of the data **/
    @Inject(method = "write", remap = false, at = @At("HEAD"))
    private void writeExtraSlots(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.witcherycompanion$extraSlots != null && this.witcherycompanion$extraSlots.tagCount() > 0) {
            tag.setTag(witcherycompanion$EXTRA_SLOTS_KEY, this.witcherycompanion$extraSlots);
        }
    }

    /** Reads the stored items. If the tag is not there (data saved before this tweak) the list is just empty **/
    @Inject(method = "read", remap = false, at = @At("HEAD"))
    private void readExtraSlots(NBTTagCompound tag, CallbackInfo ci) {
        this.witcherycompanion$extraSlots = tag.getTagList(witcherycompanion$EXTRA_SLOTS_KEY, 10);
    }

}
