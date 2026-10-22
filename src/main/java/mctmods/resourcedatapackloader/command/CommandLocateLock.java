package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.lang.reflect.Field;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;

final class CommandLocateLock {
    private static final int VANILLA = 2;

    private CommandLocateLock() {}

    static void apply(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandNode<CommandSourceStack> locate = dispatcher.getRoot().getChild("locate");
        if (locate == null) { return; }
        Predicate<CommandSourceStack> vanilla = locate.getRequirement();
        Predicate<CommandSourceStack> locked = source -> {
            int level = level();
            return level == VANILLA ? vanilla.test(source) : CommandShared.allowed(source, level);
        };
        try {
            Field requirement = CommandNode.class.getDeclaredField("requirement");
            requirement.setAccessible(true);
            requirement.set(locate, locked);
        }
        catch (ReflectiveOperationException failed) { ContentLog.LOGGER.error("Could not put the locateLevel setting on /locate, so it stays at its vanilla level", failed); }
    }

    private static int level() { return Mth.clamp(ContentControl.number(ContentControl.COMMANDS, "locateLevel", Config.commands.locateLevel()), 0, CommandShared.OPERATOR + 1); }
}
