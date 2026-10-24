package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public final class AnvilCosts {
    public static final int VANILLA_CAP = 40;
    public static final int HIGHEST = Short.MAX_VALUE;
    public static final int SLOTS = 3;
    private static final int[] VANILLA = new int[0];
    private static volatile int servedCap = VANILLA_CAP;
    private static volatile int shownCap = VANILLA_CAP;
    private static volatile int[] levels = VANILLA;
    private static volatile int[] servedPaid = VANILLA;
    private static volatile int[] shownPaid = VANILLA;
    private static volatile int[] servedLapis = VANILLA;
    private static volatile int[] shownLapis = VANILLA;
    private static volatile boolean servedOpening;
    private static volatile boolean shownOpening;

    private AnvilCosts() {}

    public static void resetAnvil() { servedCap = VANILLA_CAP; }

    public static void resetTable() {
        levels = VANILLA;
        servedPaid = VANILLA;
        servedLapis = VANILLA;
        servedOpening = false;
    }

    public static int servedCap() { return servedCap; }

    public static void serveCap(int cap) { servedCap = cap; }

    public static int[] levels() { return levels.clone(); }

    public static void serveLevels(int[] shown) { levels = shown.clone(); }

    public static int[] servedPaid() { return servedPaid.clone(); }

    public static void servePaid(int[] paid) { servedPaid = paid.clone(); }

    public static int[] servedLapis() { return servedLapis.clone(); }

    public static void serveLapis(int[] lapis) { servedLapis = lapis.clone(); }

    public static boolean servedOpening() { return servedOpening; }

    public static void serveOpening(boolean opening) { servedOpening = opening; }

    public static void show(int cap, int[] paid, int[] lapis, boolean opening) {
        shownCap = cap;
        shownPaid = paid.length == SLOTS ? paid.clone() : VANILLA;
        shownLapis = lapis.length == SLOTS ? lapis.clone() : VANILLA;
        shownOpening = opening;
    }

    public static int cap(boolean client) { return client ? shownCap : servedCap; }

    public static int paid(boolean client, int slot) { return perSlot(client ? shownPaid : servedPaid, slot); }

    public static int lapis(boolean client, int slot) { return perSlot(client ? shownLapis : servedLapis, slot); }

    public static boolean opening(boolean client) { return client ? shownOpening : servedOpening; }

    private static int perSlot(int[] held, int slot) { return held.length == SLOTS && slot >= 0 && slot < SLOTS ? held[slot] : slot + 1; }

    @SubscribeEvent public static void onLevelSet(EnchantmentLevelSetEvent event) {
        int[] held = levels;
        if (held.length == SLOTS && event.getEnchantRow() >= 0 && event.getEnchantRow() < SLOTS) { event.setLevel(held[event.getEnchantRow()]); }
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { if (event.player instanceof EntityPlayerMP) { RDPLNetwork.sendAnvilCosts((EntityPlayerMP) event.player); } }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { show(VANILLA_CAP, VANILLA, VANILLA, false); }
}
