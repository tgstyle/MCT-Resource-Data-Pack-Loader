package mctmods.resourcedatapackloader.command;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ClientCommands {
    private ClientCommands() {}

    public static void register() { ClientCommandHandler.instance.registerCommand(new Commands()); }
}
