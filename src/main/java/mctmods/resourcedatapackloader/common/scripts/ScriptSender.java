package mctmods.resourcedatapackloader.common.scripts;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class ScriptSender {
    private final CommandSourceStack source;

    public ScriptSender(CommandSourceStack source) { this.source = source; }

    public String getName() { return source.getTextName(); }

    public boolean isPlayer() { return player() != null; }

    @Nullable public Player player() { return source.getEntity() instanceof Player who ? who : null; }

    public void reply(String text) { source.sendSuccess(() -> Component.literal(text), false); }
}
