package com.smokeythebandicoot.witcherycompanion.commands;

import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodCommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.transformation.VampireCreatureTrait;
import net.msrandom.witchery.util.WitcheryUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Debug/testing command to read and change the blood of a Vampire. There are two values in W:R: the 'power' is the
 * blood bar (spent by vampire powers, filled by drinking), the 'reserve' (max 250) is a stock that is transferred
 * into the blood bar by W:R when needed.
 */
public class WitcheryBloodCommand implements ICommand {

    private static final List<String> TYPES = Arrays.asList("power", "reserve");
    private static final List<String> OPERATIONS = Arrays.asList("set", "add");
    private static final String USAGE = "/witcheryblood <player> get | /witcheryblood <player> <power|reserve> " +
            "<set|add> <value>  (value for set can also be 'max' or 'empty')";

    @Override
    public String getName() {
        return "witcheryblood";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList(getName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString("Wrong command format. Correct format is: " + USAGE));
            return;
        }

        EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);
        if (player == null) {
            sender.sendMessage(new TextComponentString("Could not find player " + args[0]));
            return;
        }

        // isTransformation does not change anything, while getTransformation creates the trait if it is missing
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        if (!playerEx.isTransformation(WitcheryCreatureTraits.VAMPIRE)) {
            sender.sendMessage(new TextComponentString(player.getName() + " is not a Vampire"));
            return;
        }
        VampireCreatureTrait vampire = playerEx.getTransformation(WitcheryCreatureTraits.VAMPIRE);

        if (args[1].equals("get") && args.length == 2) {
            sendStatus(sender, player, vampire);
            return;
        }

        if (args.length != 4 || !TYPES.contains(args[1]) || !OPERATIONS.contains(args[2])) {
            sender.sendMessage(new TextComponentString("Wrong command format. Correct format is: " + USAGE));
            return;
        }

        boolean relative = args[2].equals("add");
        boolean isPower = args[1].equals("power");
        int current = isPower ? vampire.getBloodPower() : vampire.getBloodReserve();
        int max = isPower ? vampire.getMaxBloodPower() : BloodCommandUtils.MAX_RESERVE;

        int newValue;
        try {
            newValue = BloodCommandUtils.resolve(args[3], current, max, relative);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString("Value not understood: " + args[3] +
                    (relative ? " (expected a number)" : " (expected a number, 'max' or 'empty')")));
            return;
        }

        if (isPower) vampire.setBloodPower(newValue);
        else vampire.setBloodReserve(newValue);

        // Sends the new values to the client, so that the blood bar is updated
        playerEx.markChanged();
        sendStatus(sender, player, vampire);
    }

    private static void sendStatus(ICommandSender sender, EntityPlayer player, VampireCreatureTrait vampire) {
        sender.sendMessage(new TextComponentString(player.getName() + ": vampire level " + vampire.getLevel() +
                ", blood power " + vampire.getBloodPower() + "/" + vampire.getMaxBloodPower() +
                ", blood reserve " + vampire.getBloodReserve() + "/" + BloodCommandUtils.MAX_RESERVE));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, getName());
    }

    @Override
    public @Nonnull List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        int curIndex = args.length;
        List<String> possibleArgs;

        switch (curIndex) {
            case 1:
                possibleArgs = Arrays.asList(server.getPlayerList().getOnlinePlayerNames());
                break;
            case 2:
                possibleArgs = Arrays.asList("get", "power", "reserve");
                break;
            case 3:
                possibleArgs = OPERATIONS;
                break;
            case 4:
                possibleArgs = args[2].equals("set") ? Arrays.asList("max", "empty") : Collections.<String>emptyList();
                break;
            default:
                possibleArgs = Collections.emptyList();
        }

        String typed = curIndex > 0 ? args[curIndex - 1] : "";
        return possibleArgs.stream().filter(arg -> arg.startsWith(typed)).collect(Collectors.toList());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getName().compareTo(o.getName());
    }

}
