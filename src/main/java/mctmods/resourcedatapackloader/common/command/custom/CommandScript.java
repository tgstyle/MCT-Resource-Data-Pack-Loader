package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.scripts.RdplApi;
import mctmods.resourcedatapackloader.common.scripts.ScriptSender;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

final class CommandScript {
    private CommandScript() {}

    static void run(MinecraftServer server, ICommandSender sender, String name, ScriptRef script, List<CommandArg> args, Map<String, String> values) throws CommandException {
        World world = sender.getEntityWorld();
        WorldServer level = world instanceof WorldServer ? (WorldServer) world : server.getWorld(0);
        ScriptSender who = new ScriptSender(sender);
        Map<String, Object> bindings = new HashMap<>();
        for (CommandArg arg : args) { bindings.put(arg.name, typed(arg, values.get(arg.name))); }
        bindings.put("rdpl", new RdplApi(level, who.player()));
        bindings.put("sender", who);
        if (ScriptEngine.failed(script, bindings)) { throw new CommandException("The command /" + name + " failed; the server log names the script"); }
    }

    @Nullable private static Object typed(CommandArg arg, String value) {
        try {
            if (CommandArg.INT.equals(arg.type)) { return value.isEmpty() ? null : Integer.valueOf(value); }
            if (CommandArg.NUMBER.equals(arg.type)) { return value.isEmpty() ? null : Double.valueOf(value); }
        } catch (NumberFormatException e) { return null; }
        return value;
    }
}
