package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashSet;
import java.util.Set;

public final class PortwayFx {
    private static final String PREFIX = "minecraft:";
    private static final int PARTICLES = 32;
    private static final double SPREAD = 0.5D;
    private static final Set<String> UNKNOWN = new HashSet<>();
    private static final Set<String> MISSING = new HashSet<>();

    private PortwayFx() {}

    public static void play(World world, double x, double y, double z, String sound) {
        SoundEvent event = sound.isEmpty() ? null : SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
        if (event != null) { world.playSound(null, x, y, z, event, SoundCategory.BLOCKS, 1.0F, 1.0F); }
    }

    public static void particles(World world, double x, double y, double z, String name) {
        if (name.isEmpty() || !(world instanceof WorldServer)) { return; }
        EnumParticleTypes type = EnumParticleTypes.getByName(name.startsWith(PREFIX) ? name.substring(PREFIX.length()) : name);
        if (type == null) {
            if (UNKNOWN.add(name)) { ContentLog.LOGGER.error("Portway particle '{}' is not a particle name, so it shows nothing", name); }
            return;
        }
        ((WorldServer) world).spawnParticle(type, x, y, z, PARTICLES, SPREAD, SPREAD, SPREAD, 0.0D);
    }

    public static ItemStack icon(String id, String fallback) {
        Item item = Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(id));
        if (item == null || item == Items.AIR) {
            if (MISSING.add(id)) { ContentLog.LOGGER.error("Portway menu icon '{}' is not an item, so {} is shown instead", id, fallback); }
            item = Registries.find(ForgeRegistries.ITEMS, new ResourceLocation(fallback));
        }
        return item == null || item == Items.AIR ? new ItemStack(Items.PAPER) : new ItemStack(item);
    }

    public static void card(EntityPlayer player, String trigger, String block, String network, String reason) {
        if (player instanceof EntityPlayerMP) { CardEvents.portway((EntityPlayerMP) player, trigger, block, network, reason); }
    }

    public static void activated(EntityPlayer player, String key, Object... args) { Says.bar(player, CardIds.PORTWAY_ACTIVATED, TextFormatting.GREEN, Lang.tr(player, key, args)); }

    public static void said(EntityPlayer player, String text) { Says.bar(player, CardIds.PORTWAY_REFUSED, TextFormatting.RED, text); }

    public static void refused(EntityPlayer player, String key, Object... args) { said(player, Lang.tr(player, key, args)); }

    public static void forgot(EntityPlayer player, String key, Object... args) { Says.bar(player, CardIds.PORTWAY_FORGOT, TextFormatting.YELLOW, Lang.tr(player, key, args)); }
}
