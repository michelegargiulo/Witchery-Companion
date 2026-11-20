package com.smokeythebandicoot.witcherycompanion.commands;

import com.smokeythebandicoot.witcherycompanion.patches.coven.CovenQuestDiagnostics;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows to debug the custom quests of Coven Witches.
 * Explains, for a player, how every quest is offered and what each requirement says about the player
 */
public class WitcheryCovenCommand implements ICommand {

    private static final String USAGE = "/witcherycoven <player> quests";

    @Override
    public String getName() {
        return "witcherycoven";
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
        if (args.length != 2 || !args[1].equals("quests")) {
            sender.sendMessage(new TextComponentString("Wrong command format. Correct format is: " + USAGE));
            return;
        }

        EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);
        if (player == null) {
            sender.sendMessage(new TextComponentString("Could not find player " + args[0]));
            return;
        }

        for (String line : CovenQuestDiagnostics.describe(player)) {
            sender.sendMessage(new TextComponentString(line));
        }
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
                possibleArgs = Collections.singletonList("quests");
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
