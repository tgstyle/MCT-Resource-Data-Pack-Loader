package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

final class PackCommand extends CommandBase {
    private final String name;
    private final int permission;
    private final List<CommandArg> args;
    @Nullable private final String runs;
    @Nullable private final ScriptRef script;
    private final String usage;

    PackCommand(String name, int permission, List<CommandArg> args, @Nullable String runs, @Nullable ScriptRef script) {
        this.name = name;
        this.permission = permission;
        this.args = args;
        this.runs = runs;
        this.script = script;
        StringBuilder text = new StringBuilder("/").append(name);
        for (CommandArg arg : args) { text.append(arg.optional ? " [" : " <").append(arg.name).append(arg.optional ? "]" : ">"); }
        this.usage = text.toString();
    }

    @Override @Nonnull public String getName() { return name; }

    @Override @Nonnull public String getUsage(@Nonnull ICommandSender sender) { return usage; }

    @Override public int getRequiredPermissionLevel() { return permission; }

    @Override public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
        return permission == 0 || sender.canUseCommand(permission, name);
    }

    @Override public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] given) throws CommandException {
        int required = 0;
        for (CommandArg arg : args) {
            if (!arg.optional) { required++; }
        }
        boolean open = !args.isEmpty() && args.get(args.size() - 1).text();
        if (given.length < required || !open && given.length > args.size()) { throw new WrongUsageException(usage); }
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i++) {
            CommandArg arg = args.get(i);
            values.put(arg.name, arg.value(server, sender, given, i, usage, runs == null));
        }
        if (runs != null) { Functions.runWith(sender, runs, "The command /" + name, values); }
        else { CommandScript.run(server, sender, name, script, args, values); }
    }

    @Override @Nonnull public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] given, @Nullable BlockPos target) {
        CommandArg arg = at(given.length - 1);
        return arg != null && arg.player() ? getListOfStringsMatchingLastWord(given, server.getOnlinePlayerNames()) : Collections.emptyList();
    }

    @Override public boolean isUsernameIndex(@Nonnull String[] given, int index) {
        CommandArg arg = at(index);
        return arg != null && arg.player();
    }

    @Nullable private CommandArg at(int index) {
        if (index < 0 || args.isEmpty()) { return null; }
        CommandArg last = args.get(args.size() - 1);
        if (index < args.size()) { return args.get(index); }
        return last.text() ? last : null;
    }
}
