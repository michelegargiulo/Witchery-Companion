package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.command.CommandLocate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.init.data.world.WitcheryStructures;
import net.msrandom.witchery.world.gen.structure.WitcheryBasicStructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mixins:
 * [Bugfix] Prevent the `/locate` command to be fully replaced by Witchery, and instead modify the Vanilla one
 */
@Mixin(CommandLocate.class)
public class CommandLocateMixin {

    @ModifyArg(
            method = "getTabCompletions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/CommandLocate;getListOfStringsMatchingLastWord([Ljava/lang/String;[Ljava/lang/String;)Ljava/util/List;"
            ),
            index = 1
    )
    private String[] injectWitcheryTabCompletions(String[] completions) {

        if(!ModConfig.PatchesConfiguration.CommonTweaks.tweak_lessInvasiveLocateCommand) {
            return completions;
        }

        List<String> newCompletions = new ArrayList<>();
        Collections.addAll(newCompletions, completions);

        WitcheryStructures.INSTANCE.getREGISTRY$WitcheryResurrected()
                .forEach(entry -> newCompletions.add(entry.getKey()));

        return newCompletions.toArray(new String[0]);

    }

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;findNearestStructure(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos findNearestWitcheryStructure(World world, String structureName, BlockPos position, boolean findUnexplored, Operation<BlockPos> original) {

        if(ModConfig.PatchesConfiguration.CommonTweaks.tweak_lessInvasiveLocateCommand) {

            WitcheryBasicStructurePiece.BasicStructure witcheryStructure = WitcheryStructures.INSTANCE.getREGISTRY$WitcheryResurrected()
                    .get(structureName);

            if(witcheryStructure != null) {
                return witcheryStructure.getNearestStructurePos(world, position, findUnexplored);
            }

        }

        return original.call(world, structureName, position, findUnexplored);

    }

}
