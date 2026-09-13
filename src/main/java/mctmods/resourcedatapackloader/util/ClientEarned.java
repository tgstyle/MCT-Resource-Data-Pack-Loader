package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.client.IClientAdvancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;

public final class ClientEarned {
    private ClientEarned() {}

    public static boolean has(String name) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (connection == null || id == null) { return false; }
        ClientAdvancements manager = connection.getAdvancements();
        Advancement advancement = manager.getAdvancements().get(id);
        if (advancement == null) { return false; }
        AdvancementProgress progress = ((IClientAdvancements) manager).rdpl$getProgress().get(advancement);
        return progress != null && progress.isDone();
    }
}
