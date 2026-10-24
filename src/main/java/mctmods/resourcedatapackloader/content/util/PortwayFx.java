package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.HashSet;
import java.util.Set;

public final class PortwayFx {
    private static final int PARTICLES = 32;
    private static final double SPREAD = 0.5D;
    private static final Set<String> UNKNOWN = new HashSet<>();
    private static final Set<String> MISSING = new HashSet<>();

    private PortwayFx() {}

    public static void play(ServerLevel level, double x, double y, double z, String sound) {
        SoundEvent event = sound.isEmpty() ? null : ContentSounds.find(sound);
        if (event != null) { level.playSound(null, x, y, z, event, SoundSource.BLOCKS, 1.0F, 1.0F); }
    }

    public static void particles(ServerLevel level, double x, double y, double z, String name) {
        if (name.isEmpty()) { return; }
        Identifier id = Identifier.tryParse(name);
        ParticleType<?> type = id == null ? null : BuiltInRegistries.PARTICLE_TYPE.getOptional(id).orElse(null);
        if (!(type instanceof SimpleParticleType simple)) {
            if (UNKNOWN.add(name)) { ContentLog.LOGGER.error("Portway particle '{}' is not a simple particle type, so it shows nothing", name); }
            return;
        }
        level.sendParticles(simple, x, y, z, PARTICLES, SPREAD, SPREAD, SPREAD, 0.0D);
    }

    public static ItemStack icon(String id, String fallback) {
        Item item = item(id);
        if (item == Items.AIR) {
            if (MISSING.add(id)) { ContentLog.LOGGER.error("Portway menu icon '{}' is not an item, so {} is shown instead", id, fallback); }
            item = item(fallback);
        }
        return new ItemStack(item == Items.AIR ? Items.PAPER : item);
    }

    private static Item item(String name) {
        Identifier id = Identifier.tryParse(name);
        return id == null ? Items.AIR : BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
    }

    public static void card(ServerPlayer player, String trigger, String block, String network, String reason) { CardEvents.portway(player, trigger, block, network, reason); }

    public static void activated(ServerPlayer player, String key, Object... args) { Says.bar(player, CardIds.PORTWAY_ACTIVATED, ChatFormatting.GREEN, Component.literal(Lang.tr(player, key, args))); }

    public static void said(ServerPlayer player, String text) { Says.bar(player, CardIds.PORTWAY_REFUSED, ChatFormatting.RED, Component.literal(text)); }

    public static void refused(ServerPlayer player, String key, Object... args) { said(player, Lang.tr(player, key, args)); }

    public static void forgot(ServerPlayer player, String key, Object... args) { Says.bar(player, CardIds.PORTWAY_FORGOT, ChatFormatting.YELLOW, Component.literal(Lang.tr(player, key, args))); }
}
