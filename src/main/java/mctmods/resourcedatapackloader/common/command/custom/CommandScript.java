package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.scripts.ScriptEvents;
import mctmods.resourcedatapackloader.common.scripts.ScriptSender;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;

import java.util.List;
import java.util.Map;
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
        Map<String, Object> bindings = ScriptEvents.bind(level, who.player());
        for (CommandArg arg : args) { bindings.put(arg.name, arg.typed(values.get(arg.name))); }
        bindings.put(ScriptEvents.SENDER, who);
        if (ScriptEngine.failed(script, bindings)) { throw new CommandException("The command /" + name + " failed; the server log names the script"); }
    }
}
