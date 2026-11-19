package mctmods.resourcedatapackloader.common.scripts;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ScriptSender {
    private final CommandSourceStack source;

    public ScriptSender(CommandSourceStack source) { this.source = source; }

    public String getName() { return source.getTextName(); }

    public boolean isPlayer() { return player() != null; }

    @Nullable public ServerPlayer player() { return source.getEntity() instanceof ServerPlayer player ? player : null; }

    public void reply(String text) { source.sendSuccess(() -> Component.literal(text), false); }
}
