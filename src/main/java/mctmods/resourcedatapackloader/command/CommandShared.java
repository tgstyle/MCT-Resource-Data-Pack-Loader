package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreVein;
import mctmods.resourcedatapackloader.content.worldgen.ContentReset;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public final class CommandShared {
    static final int OPERATOR = 3;
    private static final int FIND_RANGE = 6400;

    private CommandShared() {}

    static LiteralArgumentBuilder<CommandSourceStack> tree(String name, Command<CommandSourceStack> reload, String unusedNote, String configNote, boolean server) {
        Predicate<CommandSourceStack> staff = source -> !server || source.hasPermission(OPERATOR);
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name)
                .then(Commands.literal("reload").requires(staff).executes(reload))
                .then(Commands.literal("list").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "list");
                    list(context.getSource(), name, server);
                    return 1;
                }))
                .then(Commands.literal("which").requires(staff).then(Commands.argument("file", StringArgumentType.greedyString()).executes(context -> {
                    String target = StringArgumentType.getString(context, "file");
                    ran(context.getSource(), name, "which " + target);
                    which(context.getSource(), target, server);
                    return 1;
                })))
                .then(Commands.literal("pixelmap").requires(staff).then(Commands.argument("map", StringArgumentType.greedyString()).executes(context -> {
                    String target = StringArgumentType.getString(context, "map");
                    ran(context.getSource(), name, "pixelmap " + target);
                    pixelmap(context.getSource(), target);
                    return 1;
                })))
                .then(biome(name, staff, server))
                .then(Commands.literal("unused").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "unused");
                    unused(context.getSource(), unusedNote);
                    return 1;
                }))
                .then(Commands.literal("config").requires(staff)
                        .then(Commands.literal("unused").executes(context -> {
                            ran(context.getSource(), name, "config unused");
                            config(context.getSource(), false, configNote);
                            return 1;
                        }))
                        .then(Commands.literal("prune").executes(context -> {
                            ran(context.getSource(), name, "config prune");
                            config(context.getSource(), true, configNote);
                            return 1;
                        })));
        if (!server) { return root; }
        return root
                .then(Commands.literal("oregen").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "oregen");
                    CommandGates.blockedReport(context.getSource(), ContentOreControl.blocked(), "rdpl.command.orenone", "rdpl.command.oreblocked");
                    return 1;
                }))
                .then(Commands.literal("generators").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "generators");
                    CommandGates.blockedReport(context.getSource(), ContentGeneratorControl.blocked(), "rdpl.command.gennone", "rdpl.command.genblocked");
                    return 1;
                }))
                .then(Commands.literal("dimensions").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "dimensions");
                    dimensions(context.getSource());
                    return 1;
                }))
                .then(Commands.literal("gate").requires(staff).executes(context -> CommandGates.gateList(context, name))
                        .then(Commands.literal("list").executes(context -> CommandGates.gateList(context, name)))
                        .then(Commands.literal("check").then(Commands.argument("player", EntityArgument.player()).executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            ran(context.getSource(), name, "gate check " + player.getGameProfile().getName());
                            CommandGates.gatesFor(context.getSource(), player);
                            return 1;
                        })))
                        .then(Commands.literal("grant").requires(source -> source.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("gate", StringArgumentType.greedyString()).suggests((context, suggestions) -> CommandGates.suggestGates(suggestions)).executes(context -> CommandGates.gateFor(context, name, true)))))
                        .then(Commands.literal("revoke").requires(source -> source.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("gate", StringArgumentType.greedyString()).suggests((context, suggestions) -> CommandGates.suggestGates(suggestions)).executes(context -> CommandGates.gateFor(context, name, false))))))
                .then(Commands.literal("reset").requires(staff).executes(context -> {
                    ran(context.getSource(), name, "reset");
                    int swept = ContentReset.run(context.getSource().getServer());
                    send(context.getSource(), ChatFormatting.GREEN, Component.literal("The map was reset, " + swept + " entity(s) swept"));
                    return 1;
                }))
                .then(Commands.literal("team").requires(source -> ContentTeams.any()).executes(context -> CommandTeams.teamList(context, name))
                        .then(Commands.literal("list").executes(context -> CommandTeams.teamList(context, name)))
                        .then(Commands.literal("leave").executes(context -> CommandTeams.teamLeave(context, name)))
                        .then(Commands.literal("claim").executes(context -> CommandTeams.teamClaim(context, name)))
                        .then(Commands.literal("join").executes(context -> CommandTeams.teamJoin(context, name, null))
                                .then(Commands.argument("team", StringArgumentType.word()).suggests((context, suggestions) -> {
                                    for (String known : ContentTeams.joinableNames()) { suggestions.suggest(known); }
                                    return suggestions.buildFuture();
                                }).executes(context -> CommandTeams.teamJoin(context, name, StringArgumentType.getString(context, "team")))))
                        .then(Commands.literal("vote").then(Commands.argument("player", StringArgumentType.word()).suggests((context, suggestions) -> {
                            for (String known : context.getSource().getOnlinePlayerNames()) { suggestions.suggest(known); }
                            return suggestions.buildFuture();
                        }).executes(context -> CommandTeams.teamVote(context, name, StringArgumentType.getString(context, "player"))))))
                .then(Commands.literal("round").requires(source -> source.hasPermission(OPERATOR) || ContentScoring.any())
                        .then(Commands.literal("start").executes(context -> CommandTeams.roundStart(context, name)))
                        .then(Commands.literal("reset").executes(context -> CommandTeams.roundReset(context, name)))
                        .then(Commands.literal("vote")
                                .then(Commands.literal("yes").executes(context -> CommandTeams.roundVote(context, name, true)))
                                .then(Commands.literal("no").executes(context -> CommandTeams.roundVote(context, name, false)))))
                .then(Commands.literal("locate").requires(staff).then(Commands.argument("name", StringArgumentType.greedyString()).suggests((context, suggestions) -> {
                    for (String known : ContentLocate.names(context.getSource().getLevel())) { suggestions.suggest(known); }
                    return suggestions.buildFuture();
                }).executes(context -> {
                    String target = StringArgumentType.getString(context, "name");
                    ran(context.getSource(), name, "locate " + target);
                    locate(context.getSource(), target);
                    return 1;
                })))
                .then(Commands.literal("goto").requires(source -> source.hasPermission(ContentStructureSearch.lowestLevel()))
                        .then(Commands.argument("place", StringArgumentType.greedyString()).suggests((context, suggestions) -> CommandPlaces.suggestPlaces(context.getSource(), suggestions))
                                .executes(context -> {
                                    String typed = StringArgumentType.getString(context, "place");
                                    ran(context.getSource(), name, "goto " + typed);
                                    return CommandPlaces.goWhere(context.getSource(), typed);
                                })))
                .then(Commands.literal("vein").requires(staff).then(Commands.argument("entry", ResourceLocationArgument.id()).suggests((context, suggestions) -> {
                    for (String known : ContentWorldgen.veinNames()) { suggestions.suggest(known); }
                    return suggestions.buildFuture();
                }).executes(context -> {
                    ResourceLocation asked = ResourceLocationArgument.getId(context, "entry");
                    ran(context.getSource(), name, "vein " + asked);
                    return vein(context.getSource(), asked, 8);
                }).then(Commands.argument("radius", IntegerArgumentType.integer(1, 64)).executes(context -> {
                    ResourceLocation asked = ResourceLocationArgument.getId(context, "entry");
                    int radius = IntegerArgumentType.getInteger(context, "radius");
                    ran(context.getSource(), name, "vein " + asked + " " + radius);
                    return vein(context.getSource(), asked, radius);
                }))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> biome(String name, Predicate<CommandSourceStack> staff, boolean server) {
        LiteralArgumentBuilder<CommandSourceStack> here = Commands.literal("here").executes(context -> {
            ran(context.getSource(), name, "biome here");
            return biomeHere(context.getSource(), server);
        });
        if (server) {
            here.then(Commands.argument("player", EntityArgument.player()).executes(context -> {
                ServerPlayer player = EntityArgument.getPlayer(context, "player");
                ran(context.getSource(), name, "biome here " + player.getGameProfile().getName());
                biomeAt(context.getSource(), player.level(), player.blockPosition());
                return 1;
            }));
        }
        LiteralArgumentBuilder<CommandSourceStack> find = Commands.literal("find").then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests((context, suggestions) -> SharedSuggestionProvider.suggestResource(context.getSource().registryAccess().registryOrThrow(Registries.BIOME).keySet(), suggestions))
                .executes(context -> {
                    String asked = StringArgumentType.getString(context, "name");
                    ran(context.getSource(), name, "biome find " + asked);
                    return biomeFind(context.getSource(), asked);
                }));
        LiteralArgumentBuilder<CommandSourceStack> tree = Commands.literal("biome").requires(staff)
                .executes(context -> {
                    ran(context.getSource(), name, "biome");
                    if (server) { biomeInspect(context.getSource()); }
                    else { biomeList(context.getSource(), false); }
                    return 1;
                })
                .then(here)
                .then(Commands.literal("list").executes(context -> {
                    ran(context.getSource(), name, "biome list");
                    biomeList(context.getSource(), false);
                    return 1;
                })
                .then(Commands.literal("all").executes(context -> {
                    ran(context.getSource(), name, "biome list all");
                    biomeList(context.getSource(), true);
                    return 1;
                })));
        if (server) { tree.then(find); }
        return tree;
    }

    private static int vein(CommandSourceStack source, ResourceLocation asked, int radius) {
        ContentWorldgen.Entry found = ContentWorldgen.entry(asked);
        if (found == null && "minecraft".equals(asked.getNamespace())) { found = ContentWorldgen.byName(asked.getPath()); }
        if (found == null) {
            send(source, ChatFormatting.RED, tr("rdpl.command.veinunknown", "minecraft".equals(asked.getNamespace()) ? asked.getPath() : asked.toString()));
            return 0;
        }
        if (!(found.shape() instanceof ContentOreVein shape)) {
            send(source, ChatFormatting.RED, tr("rdpl.command.veinnotvein", found.def().key().toString()));
            return 0;
        }
        ServerLevel level = source.getLevel();
        BlockPos here = BlockPos.containing(source.getPosition());
        int chunkX = here.getX() >> 4;
        int chunkZ = here.getZ() >> 4;
        List<ContentOreVein.Vein> veins = new ArrayList<>();
        for (int cx = chunkX - radius; cx <= chunkX + radius; cx++) {
            for (int cz = chunkZ - radius; cz <= chunkZ + radius; cz++) {
                for (ContentOreVein.Vein vein : shape.veinsOf(level.getSeed(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), cx, cz)) {
                    if (ContentWorldgen.dimensionAllows(found, level) && ContentWorldgen.allows(found, level, vein.pos())) { veins.add(vein); }
                }
            }
        }
        if (veins.isEmpty()) {
            send(source, ChatFormatting.GRAY, tr("rdpl.command.veinnone", found.def().key().toString(), radius));
            return 0;
        }
        veins.sort(Comparator.comparingDouble(vein -> here.distSqr(vein.pos())));
        send(source, ChatFormatting.GREEN, tr("rdpl.command.veinfound", veins.size(), found.def().key().toString(), radius));
        for (ContentOreVein.Vein vein : veins.subList(0, Math.min(5, veins.size()))) {
            send(source, ChatFormatting.WHITE, tr("rdpl.command.veinat", vein.x(), vein.y(), vein.z(), (int) Math.sqrt(here.distSqr(vein.pos()))));
        }
        return veins.size();
    }

    static void ran(CommandSourceStack source, String name, String rest) { ContentLog.LOGGER.debug("{} ran /{} {}", source.getTextName(), name, rest); }

    static MutableComponent tr(String key, Object... args) { return Component.translatable(key, args); }

    static void send(CommandSourceStack source, ChatFormatting color, MutableComponent message) {
        source.sendSuccess(() -> message.withStyle(color), false);
        ContentLog.LOGGER.debug("  {}", message.getString());
    }

    static void send(CommandSourceStack source, Component message, String logged) {
        source.sendSuccess(() -> message, false);
        ContentLog.LOGGER.debug("  {}", logged);
    }

    static String elapsed(long start) {
        long time = System.currentTimeMillis() - start;
        return time < 1000L ? (time + "ms") : String.format("%.02fs", time / 1000D);
    }

    static boolean rescanFailed(CommandSourceStack source) {
        Path root = PackManager.get().getRoot();
        if (root == null) {
            source.sendFailure(tr("rdpl.command.noroot"));
            return true;
        }
        PackManager.get().scan(root);
        PackManager.get().report();
        ContentOverrides.reload();
        ContentReplacements.reload();
        ContentWorldTemplates.load();
        return false;
    }

    static CompletableFuture<Void> reloadServer(MinecraftServer server) {
        return CompletableFuture.supplyAsync(() -> {
            PackRepository repository = server.getPackRepository();
            repository.reload();
            return repository.getSelectedIds();
        }, server).thenCompose(server::reloadResources).thenRun(() -> server.execute(() -> refield(server)));
    }

    static void refield(MinecraftServer server) {
        ContentTeams.load();
        ContentScoring.load();
        for (ServerLevel level : server.getAllLevels()) {
            ContentTeams.field(level);
            ContentScoring.keep(level);
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { server.getCommands().sendCommands(player); }
    }

    private static int count(RDPLPack pack, String folder, String ext) { return pack.files(PackManager.CONTENT, folder, ext).size(); }

    static void list(CommandSourceStack source, String name, boolean server) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nopacks", String.valueOf(PackManager.get().getRoot())));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            MutableComponent line = Component.literal("  " + pack.getName() + priority);
            if (pack.isOverriding()) { line.append(tr("rdpl.command.overriding")); }
            String logged = "  " + pack.getName() + priority + (pack.isOverriding() ? " (overriding)" : "");
            if (server) {
                TreeSet<String> namespaces = new TreeSet<>(pack.getNamespaces(PackType.CLIENT_RESOURCES));
                namespaces.addAll(pack.getNamespaces(PackType.SERVER_DATA));
                String counts = "  advancements=" + count(pack, "advancement", PackManager.JSON)
                        + " loot_tables=" + count(pack, ContentFormats.LOOT_FOLDER, PackManager.JSON)
                        + " functions=" + count(pack, PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                        + " namespaces=" + namespaces;
                send(source, line.withStyle(ChatFormatting.WHITE).append(Component.literal(counts).withStyle(ChatFormatting.GRAY)), logged + counts);
                continue;
            }
            String detail = "files=" + pack.getFileCount()
                    + "\nassets=" + pack.getNamespaces(PackType.CLIENT_RESOURCES)
                    + "\ndata=" + pack.getNamespaces(PackType.SERVER_DATA)
                    + "\nadvancements=" + count(pack, "advancement", PackManager.JSON)
                    + " loot_tables=" + count(pack, ContentFormats.LOOT_FOLDER, PackManager.JSON)
                    + " recipes=" + count(pack, "recipe", PackManager.JSON)
                    + "\nfunctions=" + count(pack, PackManager.FUNCTIONS, PackManager.MCFUNCTION)
                    + " remaps=" + count(pack, PackManager.REGISTRY_REMAP, PackManager.JSON);
            line.withStyle(style -> style.withColor(pack.isOverriding() ? ChatFormatting.AQUA : ChatFormatting.WHITE)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(detail + "\n").append(tr("rdpl.command.clickhint"))))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + name + " which " + firstNamespace(pack) + ":")));
            send(source, line, logged + " " + detail.replace('\n', ' '));
        }
    }

    private static String firstNamespace(RDPLPack pack) {
        for (PackType type : PackType.values()) {
            for (String namespace : pack.getNamespaces(type)) { return namespace; }
        }
        return "minecraft";
    }

    static void locate(CommandSourceStack source, String target) {
        BlockPos from = BlockPos.containing(source.getPosition());
        BlockPos found = ContentLocate.nearest(source.getLevel(), target, from);
        if (found == null) {
            source.sendFailure(tr("rdpl.command.locatenone", target));
            return;
        }
        int away = (int) Math.sqrt(found.distSqr(from));
        source.sendSuccess(() -> tr("rdpl.command.located", target, found.getX(), found.getY(), found.getZ(), away), false);
    }

    static void which(CommandSourceStack source, String target, boolean server) {
        int colon = target.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : target.substring(0, colon);
        String path = colon < 0 ? target : target.substring(colon + 1);
        boolean found = false;
        for (PackType type : PackType.values()) {
            List<RDPLPack> holders = PackManager.get().holders(type, namespace, path);
            if (holders.isEmpty()) { continue; }
            found = true;
            String shown = type.getDirectory() + "/" + namespace + "/" + path;
            RDPLPack winner = holders.getLast();
            if (server) { send(source, ChatFormatting.GREEN, tr("rdpl.command.served", shown, winner.getName())); }
            else {
                send(source, ChatFormatting.GREEN, tr("rdpl.command.provided", shown, winner.getName(), winner.isOverriding() ? tr("rdpl.command.overriding") : Component.empty()));
                send(source, ChatFormatting.GRAY, tr("rdpl.command.providednote"));
            }
            for (int i = holders.size() - 2; i >= 0; i--) { send(source, ChatFormatting.GRAY, tr("rdpl.command.shadows", holders.get(i).getName())); }
        }
        if (!found) { send(source, ChatFormatting.YELLOW, tr("rdpl.command.unprovided", namespace + ":" + path)); }
    }

    static void pixelmap(CommandSourceStack source, String target) {
        int colon = target.indexOf(':');
        if (colon < 1) {
            source.sendFailure(tr("rdpl.command.pixelmapname"));
            return;
        }
        String namespace = target.substring(0, colon);
        String path = target.substring(colon + 1);
        if (!path.startsWith("textures/")) { path = "textures/" + path; }
        if (!path.endsWith(ContentPixelMaps.PNG)) { path = path + ContentPixelMaps.PNG; }
        ContentPixelMaps.Resolved resolved = null;
        for (boolean overriding : new boolean[] { false, true }) {
            if (!ContentPixelMaps.exists(namespace, path, overriding)) { continue; }
            resolved = ContentPixelMaps.resolve(namespace, path, overriding);
            break;
        }
        if (resolved == null) {
            source.sendFailure(tr("rdpl.command.pixelmapnone", namespace + ":" + path));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.pixelmapis", namespace + ":" + path, resolved.size()[0], resolved.size()[1]));
        for (String held : resolved.chain()) { send(source, ChatFormatting.GRAY, tr("rdpl.command.pixelmapfrom", held)); }
        if (resolved.rowsFrom() != null) { send(source, ChatFormatting.GRAY, tr("rdpl.command.pixelmaprows", resolved.rowsFrom())); }
        for (Map.Entry<String, String> entry : resolved.palette().entrySet()) {
            String note = resolved.notes().get(entry.getKey());
            send(source, ChatFormatting.WHITE, tr("rdpl.command.pixelmapkey", entry.getKey(), entry.getValue(), resolved.used(entry.getKey()), resolved.from().getOrDefault(entry.getKey(), "?"), note == null ? "" : note));
        }
    }

    static void unused(CommandSourceStack source, String note) {
        List<String> unused = PackManager.get().findUnused();
        if (unused.isEmpty()) {
            send(source, ChatFormatting.GREEN, tr("rdpl.command.allused"));
            return;
        }
        send(source, ChatFormatting.YELLOW, tr("rdpl.command.unused", unused.size()));
        for (String entry : unused) { ContentLog.LOGGER.warn("  {}", entry); }
        send(source, ChatFormatting.GRAY, tr(note));
    }

    static void config(CommandSourceStack source, boolean prune, String note) {
        List<String> stale = PackOptions.orphans();
        if (stale.isEmpty()) {
            send(source, ChatFormatting.GREEN, tr("rdpl.command.config.none"));
            return;
        }
        if (!prune) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.config.unused", stale.size()));
            for (String one : stale) { send(source, ChatFormatting.GRAY, Component.literal("  " + one + ".json")); }
            send(source, ChatFormatting.GRAY, tr(note));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.config.pruned", PackOptions.prune()));
    }

    private static Component shownName(@Nullable ResourceLocation key) { return key == null ? Component.literal("unknown") : Component.translatable(Util.makeDescriptionId("biome", key)); }

    private static int biomeHere(CommandSourceStack source, boolean server) {
        if (server && (source.source instanceof MinecraftServer || source.source instanceof RconConsoleSource)) {
            source.sendFailure(tr("rdpl.command.hereplayer"));
            return 0;
        }
        biomeAt(source, source.getUnsidedLevel(), BlockPos.containing(source.getPosition()));
        return 1;
    }

    private static void biomeAt(CommandSourceStack source, Level level, BlockPos at) {
        Holder<Biome> held = level.getBiome(at);
        ResourceLocation key = held.unwrapKey().map(ResourceKey::location).orElse(null);
        int id = source.registryAccess().registryOrThrow(Registries.BIOME).getId(held.value());
        send(source, ChatFormatting.WHITE, tr("rdpl.command.here", shownName(key), key == null ? "?" : key.toString(), id));
    }

    private static int biomeFind(CommandSourceStack source, String asked) {
        ResourceKey<Biome> target = findBiome(source, asked.trim());
        if (target == null) {
            source.sendFailure(tr("rdpl.command.nobiome", asked));
            return 0;
        }
        BlockPos from = BlockPos.containing(source.getPosition());
        Pair<BlockPos, Holder<Biome>> found = source.getLevel().findClosestBiome3d(held -> held.is(target), from, FIND_RANGE, 32, 64);
        Component shown = shownName(target.location());
        if (found == null) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.biomemissing", shown, FIND_RANGE));
            return 1;
        }
        BlockPos at = found.getFirst();
        int distance = (int) Math.sqrt(from.distSqr(new BlockPos(at.getX(), from.getY(), at.getZ())));
        send(source, ChatFormatting.WHITE, tr("rdpl.command.biomefound", shown, at.getX(), at.getZ(), distance));
        return 1;
    }

    @Nullable private static ResourceKey<Biome> findBiome(CommandSourceStack source, String asked) {
        Registry<Biome> registry = source.registryAccess().registryOrThrow(Registries.BIOME);
        ResourceLocation named = ResourceLocation.tryParse(asked);
        if (named != null && registry.containsKey(named)) { return ResourceKey.create(Registries.BIOME, named); }
        for (ResourceLocation key : registry.keySet()) {
            if (Language.getInstance().getOrDefault(Util.makeDescriptionId("biome", key)).equalsIgnoreCase(asked)) { return ResourceKey.create(Registries.BIOME, key); }
        }
        return null;
    }

    static void biomeList(CommandSourceStack source, boolean all) {
        Registry<Biome> registry = source.registryAccess().registryOrThrow(Registries.BIOME);
        int vanilla = 0;
        int shown = 0;
        for (Biome biome : registry) {
            ResourceLocation key = registry.getKey(biome);
            if (key == null) { continue; }
            if (!all && "minecraft".equals(key.getNamespace())) {
                vanilla++;
                continue;
            }
            send(source, ChatFormatting.GRAY, Component.literal("  " + registry.getId(biome) + "  " + key + "  '").append(shownName(key)).append("'"));
            shown++;
        }
        send(source, ChatFormatting.WHITE, tr("rdpl.command.biomes", shown, all || vanilla == 0 ? "" : Component.translatable("rdpl.command.biomesmore", vanilla).getString()));
    }

    private static void biomeInspect(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos at = BlockPos.containing(source.getPosition());
        Holder<Biome> held = level.getBiome(at);
        ResourceLocation key = held.unwrapKey().map(ResourceKey::location).orElse(null);
        int id = source.registryAccess().registryOrThrow(Registries.BIOME).getId(held.value());
        WorldTemplateDef template = ContentWorldTemplates.active();
        BlockPos ground = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, at);
        send(source, ChatFormatting.WHITE, Component.literal("Biome here: " + key + " (id " + id + ", ").append(shownName(key)).append(")"));
        send(source, ChatFormatting.WHITE, Component.literal("  blockBiomes=" + ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes()) + " template=" + (template == null ? "none" : template.key())));
        send(source, ChatFormatting.WHITE, Component.literal("  ground at " + ground.getX() + "," + (ground.getY() - 1) + "," + ground.getZ() + " is " + blockAt(level, ground.below())));
        send(source, ChatFormatting.WHITE, Component.literal("  one below that: " + blockAt(level, ground.below(2))));
        send(source, ChatFormatting.WHITE, Component.literal("  deep stone at y=40: " + blockAt(level, new BlockPos(at.getX(), 40, at.getZ()))));
    }

    private static ResourceLocation blockAt(ServerLevel level, BlockPos at) { return BuiltInRegistries.BLOCK.getKey(level.getBlockState(at).getBlock()); }

    static void dimensions(CommandSourceStack source) {
        Collection<DimensionDef> defs = ContentDimensions.all();
        if (defs.isEmpty()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nodims"));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.dims", defs.size()));
        for (DimensionDef def : defs) {
            boolean live = source.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, def.key())) != null;
            send(source, live ? ChatFormatting.WHITE : ChatFormatting.GRAY, Component.literal("  " + def.key()
                    + "  terrain=" + def.terrain() + " biomes=" + def.biomeSource())
                    .append(Component.translatable(live ? "rdpl.command.registered" : "rdpl.command.unregistered")));
        }
    }
}
