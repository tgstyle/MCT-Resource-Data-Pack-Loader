package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.world.Biomes;

import java.util.Map;
import java.nio.file.Path;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public final class CommandShared {
    private static final int FIND_RANGE = 6400;

    private CommandShared() {}

    static void send(ICommandSender sender, TextFormatting color, String message) {
        Says.line(sender, color, message);
        ContentLog.LOGGER.debug("  {}", message);
    }

    static void listPacks(ICommandSender sender, BiConsumer<RDPLPack, String> each) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.nopacks", PackManager.get().getRoot()));
            return;
        }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String tier = pack.isOverriding() ? Lang.tr(sender, "rdpl.command.overriding") : "";
            each.accept(pack, "  " + pack.getName() + priority + tier);
        }
    }

    static void rescan(ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noroot")); }
        PackManager.get().scan(root);
        PackManager.get().report();
        ContentOverrides.reload();
    }

    static void which(ICommandSender sender, String target, boolean clientSide) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        List<RDPLPack> holders = PackManager.get().holders(namespace, path);
        if (holders.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.unprovided", namespace + ":" + path));
            return;
        }
        RDPLPack winner = holders.get(holders.size() - 1);
        if (clientSide) {
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.provided", namespace + ":" + path, winner.getName(), winner.isOverriding() ? Lang.tr(sender, "rdpl.command.overriding") : ""));
            send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.providednote"));
        }
        else { send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.served", namespace + ":" + path, winner.getName())); }
        for (int i = holders.size() - 2; i >= 0; i--) { send(sender, TextFormatting.GRAY, Lang.tr(sender, "rdpl.command.shadows", holders.get(i).getName())); }
    }

    static void blockedReport(ICommandSender sender, Map<String, Integer> blocked, String noneKey, String headerKey) {
        if (blocked.isEmpty()) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, noneKey));
            return;
        }
        send(sender, TextFormatting.GREEN, Lang.tr(sender, headerKey));
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) { send(sender, TextFormatting.GRAY, "  " + entry.getKey() + ": " + entry.getValue()); }
    }

    static void send(ICommandSender sender, ITextComponent message, String logged) {
        sender.sendMessage(message);
        ContentLog.LOGGER.debug("  {}", logged);
    }

    static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
    }

    @Nullable static Biome findBiome(String name) {
        Biome found = Biomes.byName(name);
        if (found != null) { return found; }
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome.getBiomeName().equalsIgnoreCase(name)) { return biome; }
        }
        return null;
    }

    static List<String> biomeNames() {
        List<String> names = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) {
            ResourceLocation name = biome.getRegistryName();
            if (name != null) { names.add(name.toString()); }
        }
        return names;
    }

    static void biomeHere(ICommandSender sender) { biomeAt(sender, sender.getEntityWorld(), sender.getPosition()); }

    static void biomeAt(ICommandSender sender, World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.here", biome.getBiomeName(), biome.getRegistryName(), Biome.getIdForBiome(biome)));
    }

    static void biomeList(ICommandSender sender, boolean all) {
        int vanilla = 0;
        int shown = 0;
        for (Biome biome : ForgeRegistries.BIOMES) {
            ResourceLocation name = biome.getRegistryName();
            if (name == null) { continue; }
            if (!all && "minecraft".equals(name.getNamespace())) {
                vanilla++;
                continue;
            }
            send(sender, TextFormatting.GRAY, "  " + Biome.getIdForBiome(biome) + "  " + name + "  '" + biome.getBiomeName() + "'");
            shown++;
        }
        send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.biomes", shown, all || vanilla == 0 ? "" : Lang.tr(sender, "rdpl.command.biomesmore", vanilla)));
    }

    static void biomeFind(ICommandSender sender, String name) throws CommandException {
        Biome target = findBiome(name);
        if (target == null) { throw new WrongUsageException(Lang.tr(sender, "rdpl.command.nobiome", name)); }
        World world = sender.getEntityWorld();
        BlockPos from = sender.getPosition();
        BlockPos found = world.getBiomeProvider().findBiomePosition(from.getX(), from.getZ(), FIND_RANGE, Collections.singletonList(target), new Random());
        if (found == null) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.biomemissing", target.getBiomeName(), FIND_RANGE));
            return;
        }
        int distance = (int) Math.sqrt(from.distanceSq(found.getX(), from.getY(), found.getZ()));
        send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.biomefound", target.getBiomeName(), found.getX(), found.getZ(), distance));
    }

    static void config(ICommandSender sender, String action, String usage, String note) throws CommandException {
        List<String> stale = PackOptions.orphans();
        if (stale.isEmpty()) {
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.config.none"));
            return;
        }
        if ("unused".equals(action)) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.config.unused", stale.size()));
            for (String one : stale) { send(sender, TextFormatting.GRAY, "  " + one + ".json"); }
            send(sender, TextFormatting.GRAY, Lang.tr(sender, note));
            return;
        }
        if (!"prune".equals(action)) { throw new WrongUsageException(usage); }
        int gone = PackOptions.prune();
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.config.pruned", gone));
    }

    static void unused(ICommandSender sender, String note) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.allused"));
            return;
        }
        send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.unused", unused.size()));
        for (String entry : unused) { ContentLog.LOGGER.warn("  {}", entry); }
        send(sender, TextFormatting.GRAY, Lang.tr(sender, note));
    }
}
