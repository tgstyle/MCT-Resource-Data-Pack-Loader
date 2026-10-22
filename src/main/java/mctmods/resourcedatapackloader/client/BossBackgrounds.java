package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.network.MessageBossBackground;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class BossBackgrounds {
    private static final int BAR_WIDTH = 182;
    private static final Map<UUID, Held> BACKGROUNDS = new HashMap<>();

    private BossBackgrounds() {}

    private record Held(Identifier texture, int width, int height, int x, int y) {}

    public static void set(MessageBossBackground message) {
        @Nullable Identifier texture = message.texture().isEmpty() ? null : ContentParser.location(message.texture());
        if (texture == null) { BACKGROUNDS.remove(message.bar()); }
        else { BACKGROUNDS.put(message.bar(), new Held(texture, message.width(), message.height(), message.x(), message.y())); }
    }

    public static void reset() { BACKGROUNDS.clear(); }

    public static void onBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Held held = BACKGROUNDS.get(event.getBossEvent().getId());
        if (held == null) { return; }
        int width = held.width > 0 ? held.width : BAR_WIDTH;
        int height = held.height > 0 ? held.height : event.getIncrement();
        event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, held.texture, event.getX() + held.x, event.getY() - Minecraft.getInstance().font.lineHeight + held.y, 0.0F, 0.0F, width, height, width, height, ARGB.white(1.0F));
    }
}
