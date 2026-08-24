package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class CommandShared {
    private static final int FIND_RANGE = 6400;

    private CommandShared() {}

    static void send(ICommandSender sender, TextFormatting color, String message) {
        sender.sendMessage(new TextComponentString(color + message));
        ContentLog.LOGGER.debug("  {}", message);
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
        ResourceLocation location = new ResourceLocation(name);
        if (ForgeRegistries.BIOMES.containsKey(location)) { return ForgeRegistries.BIOMES.getValue(location); }
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

    static void biomeHere(ICommandSender sender) {
        BlockPos pos = sender.getPosition();
        Biome biome = sender.getEntityWorld().getBiome(pos);
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
