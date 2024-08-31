package com.smokeythebandicoot.witcherycompanion.commands;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

public class WitcheryProgressCommand implements ICommand {

    private final static Set<String> cmds = Stream.of("unlock", "lock", "reset", "has", "list")
            .collect(Collectors.toSet());

    @Override
    public String getName() {
        return "witcheryprogress";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/witcheryprogress";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList(getName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!isArgsLengthCorrect(args)) {
            sender.sendMessage(new TextComponentString("Wrong command format: Expected 3 arguments (2 for reset and list) but received " + args.length));
            sender.sendMessage(new TextComponentString("Correct format is: /witcheryprogress <player> [unlock|lock|has] <key> or /witcheryprogress <player> [reset|list]"));
            return;
        }
        EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);

        if (player == null) {
            sender.sendMessage(new TextComponentString("Could not find player " + args[0]));
            return;
        }

        if (!cmds.contains(args[1])) {
            sender.sendMessage(new TextComponentString("Progress command not recognized: " + args[1]));
            sender.sendMessage(new TextComponentString(String.join("|", cmds)));
            return;
        }

        String key = args.length == 3 ? WitcheryCompanion.prefix(args[2]) : null;

        IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
        if (progress == null) {
            sender.sendMessage(new TextComponentString("An unexpected error occured. Could not update player progress"));
            return;
        }

        switch (args[1]) {
            case "unlock":
                progress.unlockProgress(key);
                sender.sendMessage(new TextComponentString("Unlocked progress " + key + " for " + player.getName()));
                ProgressSync.serverRequest(player);
                break;
            case "lock":
                progress.lockProgress(key);
                sender.sendMessage(new TextComponentString("Locked progress " + key + " for " + player.getName()));
                ProgressSync.serverRequest(player);
                break;
            case "reset":
                sender.sendMessage(new TextComponentString("Reset progress for " + player.getName()));
                progress.resetProgress();
                ProgressSync.serverRequest(player);
                break;
            case "has":
                sender.sendMessage(new TextComponentString(args[0] + ((progress.hasProgress(key) ? " has " : " has not ") + key)));
                break;
            case "list":
                for (String p : progress.getUnlockedProgress()) {
                    sender.sendMessage(new TextComponentString(p));
                }
                break;
        }


    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, getName());
    }

    @Override
    public @Nonnull List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        Set<String> possibleArgs = new HashSet<>();

        int curIndex = args.length;

        switch (curIndex) {
            case 1:
                possibleArgs.addAll(Arrays.asList(server.getPlayerList().getOnlinePlayerNames()));
                break;
            case 2:
                possibleArgs.addAll(cmds);
            case 3:
                // No tabs, there's no holder for all Witchery secrets
                break;
        }

        return possibleArgs
                .stream()
                .filter(arg -> arg.startsWith(args[curIndex - 1]))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private boolean isArgsLengthCorrect(String[] args) {
        if (args.length < 2) return false;
        if (args[1].equals("reset") || args[1].equals("list")) return args.length == 2;
        return args.length == 3;
    }
}
