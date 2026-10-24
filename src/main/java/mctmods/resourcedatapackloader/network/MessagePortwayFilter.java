package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;

import net.minecraft.network.FriendlyByteBuf;

public record MessagePortwayFilter(String text) {
    public static void write(MessagePortwayFilter message, FriendlyByteBuf buf) { buf.writeUtf(message.text, ContentPortwayMenu.FILTER_MAX); }

    public static MessagePortwayFilter read(FriendlyByteBuf buf) { return new MessagePortwayFilter(buf.readUtf(ContentPortwayMenu.FILTER_MAX)); }
}
