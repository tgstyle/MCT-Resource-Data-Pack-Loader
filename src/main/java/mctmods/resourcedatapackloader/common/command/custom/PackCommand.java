package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

final class PackCommand {
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

    LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
        if (permission > 0) { root.requires(source -> source.hasPermission(permission)); }
        int required = 0;
        for (CommandArg arg : args) {
            if (!arg.optional) { required++; }
        }
        if (required == 0) { root.executes(context -> run(context, 0)); }
        RequiredArgumentBuilder<CommandSourceStack, ?> tail = null;
        for (int i = args.size() - 1; i >= 0; i--) {
            RequiredArgumentBuilder<CommandSourceStack, ?> node = args.get(i).node();
            int given = i + 1;
            if (given >= required) { node.executes(context -> run(context, given)); }
            if (tail != null) { node.then(tail); }
            tail = node;
        }
        if (tail != null) { root.then(tail); }
        return root;
    }

    private int run(CommandContext<CommandSourceStack> context, int given) throws CommandSyntaxException {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i++) {
            CommandArg arg = args.get(i);
            values.put(arg.name, arg.value(context, i < given, usage, runs == null));
        }
        if (runs != null) { Functions.runWith(context.getSource(), runs, "The command /" + name, values); }
        else { CommandScript.run(context.getSource(), name, script, args, values); }
        return 1;
    }
}
