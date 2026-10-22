package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ContentRuleClient {
    private ContentRuleClient() {}

    public static void settle() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) { ContentRuleSwitch.settle(level); }
    }
}
