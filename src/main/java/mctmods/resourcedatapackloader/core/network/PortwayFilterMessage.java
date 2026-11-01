package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.gui.ContentPortwayMenu;

import net.minecraft.network.FriendlyByteBuf;

public record PortwayFilterMessage(String text) {
    public static void write(PortwayFilterMessage message, FriendlyByteBuf buf) { buf.writeUtf(message.text, ContentPortwayMenu.FILTER_MAX); }

    public static PortwayFilterMessage read(FriendlyByteBuf buf) { return new PortwayFilterMessage(buf.readUtf(ContentPortwayMenu.FILTER_MAX)); }
}
