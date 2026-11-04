package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.common.blocks.ContentOverrides;
import mctmods.resourcedatapackloader.common.ownership.OwnershipSettings;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;
import mctmods.resourcedatapackloader.common.board.ContentBoards;
import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.cards.CardRules;
import mctmods.resourcedatapackloader.common.dialogs.ContentDialogs;
import mctmods.resourcedatapackloader.common.entities.spawning.SpawnLocks;
import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.itemlocks.ContentItemLocks;
import mctmods.resourcedatapackloader.common.itemlocks.ItemLockSync;
import mctmods.resourcedatapackloader.common.pack.definitions.PackOptions;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.RDPLPack;
import mctmods.resourcedatapackloader.common.recipes.RecipeLocks;
import mctmods.resourcedatapackloader.common.scoring.ContentScoring;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.common.util.worldgen.Biomes;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomeControl;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class CommandShared {
    private static final int FIND_RANGE = 6400;

    private CommandShared() {}

    public static void send(ICommandSender sender, TextFormatting color, String message) {
        Says.line(sender, color, message);
        ContentLog.LOGGER.debug("  {}", message);
    }

    @Nullable static EntityPlayer viewer(ICommandSender sender) { return sender instanceof EntityPlayer ? (EntityPlayer) sender : null; }

    static EntityPlayerMP goer(ICommandSender sender) throws CommandException {
        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) { return (EntityPlayerMP) sender.getCommandSenderEntity(); }
        throw new PlayerNotFoundException("commands.generic.player.unspecified");
    }

    public static void listPacks(ICommandSender sender, BiConsumer<RDPLPack, String> each) {
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

    static void refield(MinecraftServer server) {
        ContentTeams.load();
        ContentScoring.load();
        ContentDice.load();
        ContentBoards.load();
        ContentBoards.rebuild(server);
        CardRules.load();
        ContentDialogs.load();
        ContentHearts.load();
        ContentHearts.refresh(server);
        SpawnLocks.load();
        RecipeLocks.reload();
        ContentItemLocks.load();
        ContentWeights.load();
        ContentItemLocks.report(server);
        ItemLockSync.resendAll(server);
        for (WorldServer world : server.worlds) {
            ContentTeams.field(world);
            ContentScoring.keep(world);
        }
    }

    public static void rescan(ICommandSender sender) throws CommandException {
        Path root = PackManager.get().getRoot();
        if (root == null) { throw new CommandException(Lang.tr(sender, "rdpl.command.noroot")); }
        PackManager.get().scan(root);
        PackManager.get().report();
        ContentOverrides.reload();
        OwnershipSettings.reload();
        ClaimSettings.reload();
    }

    public static void which(ICommandSender sender, String target, boolean clientSide) {
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

    public static void send(ICommandSender sender, ITextComponent message, String logged) {
        sender.sendMessage(message);
        ContentLog.LOGGER.debug("  {}", logged);
    }

    public static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format(Locale.ROOT, "%.02fs",time / 1000D);
    }

    @Nullable static Biome findBiome(String name) {
        Biome found = Biomes.byName(name);
        if (found != null) { return found; }
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (ContentBiomeControl.shownName(biome).equalsIgnoreCase(name)) { return biome; }
        }
        return null;
    }

    public static List<String> biomeNames() {
        List<String> names = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) {
            ResourceLocation name = biome.getRegistryName();
            if (name != null) { names.add(name.toString()); }
        }
        return names;
    }

    public static void biomeHere(ICommandSender sender) { biomeAt(sender, sender.getEntityWorld(), sender.getPosition()); }

    static void biomeAt(ICommandSender sender, World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.here", ContentBiomeControl.shownName(biome), biome.getRegistryName(), Biome.getIdForBiome(biome)));
    }

    public static void biomeList(ICommandSender sender, boolean all) {
        int vanilla = 0;
        int shown = 0;
        for (Biome biome : ForgeRegistries.BIOMES) {
            ResourceLocation name = biome.getRegistryName();
            if (name == null) { continue; }
            if (!all && "minecraft".equals(name.getNamespace())) {
                vanilla++;
                continue;
            }
            send(sender, TextFormatting.GRAY, "  " + Biome.getIdForBiome(biome) + "  " + name + "  '" + ContentBiomeControl.shownName(biome) + "'");
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
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.biomemissing", ContentBiomeControl.shownName(target), FIND_RANGE));
            return;
        }
        int distance = (int) Math.sqrt(from.distanceSq(found.getX(), from.getY(), found.getZ()));
        send(sender, TextFormatting.WHITE, Lang.tr(sender, "rdpl.command.biomefound", ContentBiomeControl.shownName(target), found.getX(), found.getZ(), distance));
    }

    public static void config(ICommandSender sender, String action, String usage, String note) throws CommandException {
        List<String> stale = PackOptions.orphans();
        if (stale.isEmpty()) {
            send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.config.none"));
            return;
        }
        if ("unused".equals(action)) {
            send(sender, TextFormatting.YELLOW, Lang.tr(sender, "rdpl.command.config.unused", stale.size()));
            for (String one : stale) { send(sender, TextFormatting.GRAY, "  " + one + PackManager.JSON_SUFFIX); }
            send(sender, TextFormatting.GRAY, Lang.tr(sender, note));
            return;
        }
        if (!"prune".equals(action)) { throw new WrongUsageException(usage); }
        int gone = PackOptions.prune();
        send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.config.pruned", gone));
    }

    public static void unused(ICommandSender sender, String note) {
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
