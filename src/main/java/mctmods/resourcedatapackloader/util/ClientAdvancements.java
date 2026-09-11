package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.client.IClientAdvancementManager;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ClientAdvancements {
    private ClientAdvancements() {}

    public static boolean has(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.connection == null) { return false; }
        ClientAdvancementManager manager = mc.player.connection.getAdvancementManager();
        Advancement advancement = manager.getAdvancementList().getAdvancement(new ResourceLocation(name));
        if (advancement == null) { return false; }
        AdvancementProgress progress = ((IClientAdvancementManager) manager).getAdvancementToProgress().get(advancement);
        return progress != null && progress.isDone();
    }
}
