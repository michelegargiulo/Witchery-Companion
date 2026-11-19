package com.smokeythebandicoot.witcherycompanion.commands;

import com.smokeythebandicoot.witcherycompanion.api.accessors.extensions.IPlayerSkillsAccessor;
import com.smokeythebandicoot.witcherycompanion.utils.SkillCommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows to read and change the bottling skill and the throwing skill of a player (0 to 100)
 */
public class WitcherySkillCommand implements ICommand {

    private static final List<String> TYPES = Arrays.asList("bottling", "throwing");
    private static final List<String> OPERATIONS = Arrays.asList("set", "add");
    private static final String USAGE = "/witcheryskill <player> get | /witcheryskill <player> <bottling|throwing> " +
            "<set|add> <value>  (value for set can also be 'max' or 'empty')";

    @Override
    public String getName() {
        return "witcheryskill";
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

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerSkillsAccessor skills = (IPlayerSkillsAccessor) (Object) playerEx;

        if (args[1].equals("get") && args.length == 2) {
            sendStatus(sender, player, skills);
            return;
        }

        if (args.length != 4 || !TYPES.contains(args[1]) || !OPERATIONS.contains(args[2])) {
            sender.sendMessage(new TextComponentString("Wrong command format. Correct format is: " + USAGE));
            return;
        }

        boolean relative = args[2].equals("add");
        boolean bottling = args[1].equals("bottling");
        int current = bottling ? skills.witcherycompanion$accessor$getBottlingSkillRaw() : skills.witcherycompanion$accessor$getThrowingSkillRaw();

        int newValue;
        try {
            newValue = SkillCommandUtils.resolve(args[3], current, relative);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString("Value not understood: " + args[3] +
                    (relative ? " (expected a number)" : " (expected a number, 'max' or 'empty')")));
            return;
        }

        if (bottling) skills.witcherycompanion$accessor$setBottlingSkill(newValue);
        else skills.witcherycompanion$accessor$setThrowingSkill(newValue);

        // Marks the data as changed so that it is saved and sent to the client
        playerEx.markChanged();
        sendStatus(sender, player, skills);
    }

    private static void sendStatus(ICommandSender sender, EntityPlayer player, IPlayerSkillsAccessor skills) {
        sender.sendMessage(new TextComponentString(player.getName() + ": bottling skill " +
                skills.witcherycompanion$accessor$getBottlingSkillRaw() + "/" + SkillCommandUtils.MAX_SKILL +
                ", throwing skill " + skills.witcherycompanion$accessor$getThrowingSkillRaw() + "/" + SkillCommandUtils.MAX_SKILL));
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
                possibleArgs = Arrays.asList("get", "bottling", "throwing");
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
