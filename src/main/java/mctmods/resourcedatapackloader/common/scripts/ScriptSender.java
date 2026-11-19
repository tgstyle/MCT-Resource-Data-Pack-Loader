package mctmods.resourcedatapackloader.common.scripts;

import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public final class ScriptSender {
    private final ICommandSender sender;

    public ScriptSender(ICommandSender sender) { this.sender = sender; }

    public String getName() { return sender.getName(); }

    public boolean isPlayer() { return player() != null; }

    @Nullable public EntityPlayer player() { return sender.getCommandSenderEntity() instanceof EntityPlayer ? (EntityPlayer) sender.getCommandSenderEntity() : null; }

    public void reply(String text) { sender.sendMessage(new TextComponentString(text)); }
}
