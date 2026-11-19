package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.command.CommandShared;
import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import com.mojang.brigadier.builder.ArgumentBuilder;
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
    private final int required;

    PackCommand(String name, int permission, List<CommandArg> args, @Nullable String runs, @Nullable ScriptRef script) {
        this.name = name;
        this.permission = permission;
        this.args = args;
        this.runs = runs;
        this.script = script;
        StringBuilder text = new StringBuilder("/").append(name);
        int count = 0;
        for (CommandArg arg : args) {
            text.append(arg.optional ? " [" : " <").append(arg.name).append(arg.optional ? "]" : ">");
            if (!arg.optional) { count++; }
        }
        this.usage = text.toString();
        this.required = count;
    }

    LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name).requires(source -> CommandShared.allowed(source, permission));
        ArgumentBuilder<CommandSourceStack, ?> next = null;
        for (int i = args.size() - 1; i >= 0; i--) {
            int given = i + 1;
            RequiredArgumentBuilder<CommandSourceStack, ?> node = Commands.argument(args.get(i).name, args.get(i).brigadier());
            if (next != null) { node.then(next); }
            if (given >= required) { node.executes(context -> run(context, given)); }
            next = node;
        }
        if (next != null) { root.then(next); }
        if (required == 0) { root.executes(context -> run(context, 0)); }
        return root;
    }

    private int run(CommandContext<CommandSourceStack> context, int given) throws CommandSyntaxException {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i++) {
            CommandArg arg = args.get(i);
            values.put(arg.name, i < given ? arg.value(context, usage, runs == null) : arg.absent(context.getSource(), usage));
        }
        if (runs != null) { Functions.runWith(context.getSource(), runs, "The command /" + name, values); }
        else { CommandScript.run(context.getSource(), name, script, args, values); }
        return 1;
    }
}
