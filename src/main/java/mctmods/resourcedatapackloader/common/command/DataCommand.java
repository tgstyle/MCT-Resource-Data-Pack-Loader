package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.syncdata.SyncData;
import mctmods.resourcedatapackloader.common.syncdata.SyncDataRules;
import mctmods.resourcedatapackloader.common.syncdata.SyncValue;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

final class DataCommand {
    private static final String WORLD = "world";
    private static final String ASKING = "The command /" + CommandShared.NAME;
    private static final List<String> ACTIONS = Arrays.asList("set", "add", "remove", "get", "list");

    private DataCommand() {}

    static void run(MinecraftServer server, ICommandSender sender, String[] args, String usage) throws CommandException {
        if (args.length < 2 || !ACTIONS.contains(args[1])) { throw new WrongUsageException(usage); }
        String action = args[1];
        if ("list".equals(action)) {
            if (args.length > 3) { throw new WrongUsageException(usage); }
            list(sender, target(server, sender, args.length == 3 ? args[2] : WORLD));
            return;
        }
        if (args.length < 4) { throw new WrongUsageException(usage); }
        EntityPlayer target = target(server, sender, args[2]);
        String name = args[3];
        if ("get".equals(action) && args.length == 4) { get(sender, target, name); }
        else if ("remove".equals(action) && args.length == 4) { remove(sender, target, name); }
        else if ("add".equals(action) && args.length == 5) { add(sender, target, name, CommandBase.parseDouble(args[4])); }
        else if ("set".equals(action) && args.length >= 5) { set(sender, target, name, args, usage); }
        else { throw new WrongUsageException(usage); }
    }

    static List<String> complete(MinecraftServer server, String[] args) {
        if (args.length == 2) { return CommandBase.getListOfStringsMatchingLastWord(args, ACTIONS); }
        if (args.length == 3) {
            List<String> targets = new ArrayList<>(Arrays.asList(server.getOnlinePlayerNames()));
            targets.add(WORLD);
            return CommandBase.getListOfStringsMatchingLastWord(args, targets);
        }
        if (args.length == 4 && !"list".equals(args[1])) {
            List<String> names = new ArrayList<>();
            for (SyncDataRules.Declaration declared : SyncDataRules.all()) { names.add(declared.name); }
            return CommandBase.getListOfStringsMatchingLastWord(args, names);
        }
        if (args.length == 5 && "set".equals(args[1])) {
            return CommandBase.getListOfStringsMatchingLastWord(args, SyncDataRules.declared(args[3]) == null ? SyncValue.KIND_NAMES : Arrays.asList("true", "false"));
        }
        return Collections.emptyList();
    }

    @Nullable private static EntityPlayer target(MinecraftServer server, ICommandSender sender, String word) throws CommandException {
        return WORLD.equals(word) ? null : CommandBase.getPlayer(server, sender, word);
    }

    private static String label(@Nullable EntityPlayer target) { return target == null ? WORLD : target.getName(); }

    @Nullable private static SyncValue own(ICommandSender sender, @Nullable EntityPlayer target, String name) {
        SyncData data = SyncData.peek(sender.getEntityWorld());
        if (data == null) { return null; }
        return values(data, target).get(name);
    }

    private static Map<String, SyncValue> values(SyncData data, @Nullable EntityPlayer target) { return target == null ? data.worldValues() : data.playerValues(target.getUniqueID()); }

    private static void get(ICommandSender sender, @Nullable EntityPlayer target, String name) {
        SyncValue value = target == null ? own(sender, null, name) : SyncData.read(target, name);
        if (value == null && target == null) { value = SyncDataRules.defaultOf(name); }
        if (value == null) { CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.dataunset", name, label(target))); }
        else { CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.datashown", name, label(target), value.display)); }
    }

    private static void remove(ICommandSender sender, @Nullable EntityPlayer target, String name) {
        boolean removed = SyncData.remove(sender.getEntityWorld(), target, name);
        CommandShared.send(sender, removed ? TextFormatting.GREEN : TextFormatting.YELLOW, Lang.tr(sender, removed ? "rdpl.command.dataremoved" : "rdpl.command.dataunset", name, label(target)));
    }

    private static void add(ICommandSender sender, @Nullable EntityPlayer target, String name, double amount) {
        SyncValue old = own(sender, target, name);
        if (old == null) { old = SyncDataRules.defaultOf(name); }
        if (old != null && old.kind != SyncValue.NUMBER) {
            CommandShared.send(sender, TextFormatting.RED, Lang.tr(sender, "rdpl.command.datarefused", name, label(target)));
            return;
        }
        store(sender, target, name, SyncValue.number((old == null ? 0.0D : old.number) + amount));
    }

    private static void set(ICommandSender sender, @Nullable EntityPlayer target, String name, String[] args, String usage) throws CommandException {
        SyncDataRules.Declaration declared = SyncDataRules.declared(name);
        boolean typed = SyncValue.KIND_NAMES.contains(args[4]) && args.length >= 6 && (declared == null || SyncValue.kindName(declared.kind).equals(args[4]));
        if (declared == null && !typed) { throw new WrongUsageException(usage); }
        String type = typed ? args[4] : SyncValue.kindName(declared.kind);
        String text = String.join(" ", Arrays.copyOfRange(args, typed ? 5 : 4, args.length));
        SyncValue value;
        int kind = SyncValue.kindOf(type);
        if (kind == SyncValue.NUMBER) { value = SyncValue.number(CommandBase.parseDouble(text)); }
        else if (kind == SyncValue.BOOL) { value = SyncValue.bool(CommandBase.parseBoolean(text)); }
        else { value = SyncValue.text(text); }
        store(sender, target, name, value);
    }

    private static void store(ICommandSender sender, @Nullable EntityPlayer target, String name, SyncValue value) {
        World world = sender.getEntityWorld();
        boolean applied = SyncData.set(world, target, name, value.raw(), ASKING);
        String message = applied ? Lang.tr(sender, "rdpl.command.dataset", name, value.display, label(target)) : Lang.tr(sender, "rdpl.command.datarefused", name, label(target));
        CommandShared.send(sender, applied ? TextFormatting.GREEN : TextFormatting.RED, message);
    }

    private static void list(ICommandSender sender, @Nullable EntityPlayer target) {
        SyncData data = SyncData.peek(sender.getEntityWorld());
        Map<String, SyncValue> values = data == null ? Collections.emptyMap() : values(data, target);
        if (values.isEmpty()) {
            CommandShared.send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.datanone", label(target)));
            return;
        }
        for (Map.Entry<String, SyncValue> entry : values.entrySet()) {
            CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.datashown", entry.getKey(), label(target), entry.getValue().display));
        }
    }
}
