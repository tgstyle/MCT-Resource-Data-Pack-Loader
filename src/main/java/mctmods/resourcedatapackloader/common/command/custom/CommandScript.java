package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.ScriptEvents;
import mctmods.resourcedatapackloader.common.scripts.ScriptSender;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

final class CommandScript {
    private CommandScript() {}

    static void run(CommandSourceStack source, String name, ScriptRef script, List<CommandArg> args, Map<String, String> values) throws CommandSyntaxException {
        ScriptSender who = new ScriptSender(source);
        Map<String, Object> bindings = new HashMap<>();
        for (CommandArg arg : args) { bindings.put(arg.name, typed(arg, values.get(arg.name))); }
        bindings.put(ScriptEvents.RDPL, new RdplApi(source.getLevel(), who.player()));
        bindings.put(ScriptEvents.SENDER, who);
        if (ScriptEngine.failed(script, bindings)) { throw new SimpleCommandExceptionType(Component.literal("The command /" + name + " failed; the server log names the script")).create(); }
    }

    @Nullable private static Object typed(CommandArg arg, String value) {
        try {
            if (CommandArg.INT.equals(arg.type)) { return value.isEmpty() ? null : Integer.valueOf(value); }
            if (CommandArg.NUMBER.equals(arg.type)) { return value.isEmpty() ? null : Double.valueOf(value); }
        }
        catch (NumberFormatException e) { return null; }
        return value;
    }
}
