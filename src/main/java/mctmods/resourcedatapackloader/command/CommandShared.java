package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreVein;
import mctmods.resourcedatapackloader.content.worldgen.ContentReset;
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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.biome.Biome;

public final class CommandShared {
    private CommandShared() {}

    static LiteralArgumentBuilder<CommandSourceStack> tree(String name, Command<CommandSourceStack> reload, String unusedNote, String configNote, boolean server) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name)
                .then(Commands.literal("reload").executes(reload))
                .then(Commands.literal("list").executes(context -> {
                    ran(context.getSource(), name, "list");
                    list(context.getSource(), name);
                    return 1;
                }))
                .then(Commands.literal("which").then(Commands.argument("file", StringArgumentType.greedyString()).executes(context -> {
                    String target = StringArgumentType.getString(context, "file");
                    ran(context.getSource(), name, "which " + target);
                    which(context.getSource(), target);
                    return 1;
                })))
                .then(Commands.literal("pixelmap").then(Commands.argument("map", StringArgumentType.greedyString()).executes(context -> {
                    String target = StringArgumentType.getString(context, "map");
                    ran(context.getSource(), name, "pixelmap " + target);
                    pixelmap(context.getSource(), target);
                    return 1;
                })))
                .then(Commands.literal("oregen").requires(source -> server).executes(context -> {
                    ran(context.getSource(), name, "oregen");
                    blockedReport(context.getSource(), ContentOreControl.blocked());
                    return 1;
                }))
                .then(Commands.literal("dimensions").requires(source -> server).executes(context -> {
                    ran(context.getSource(), name, "dimensions");
                    dimensions(context.getSource());
                    return 1;
                }))
                .then(Commands.literal("gate").requires(source -> server).executes(context -> {
                    ran(context.getSource(), name, "gate");
                    gates(context.getSource());
                    return 1;
                })
                .then(Commands.literal("check").then(Commands.argument("player", EntityArgument.player()).executes(context -> {
                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                    ran(context.getSource(), name, "gate check " + player.getGameProfile().getName());
                    gatesFor(context.getSource(), player);
                    return 1;
                })))
                .then(Commands.literal("grant").requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("gate", StringArgumentType.string()).executes(context -> gateFor(context, name, true)))))
                .then(Commands.literal("revoke").requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("gate", StringArgumentType.string()).executes(context -> gateFor(context, name, false))))))
                .then(Commands.literal("reset").requires(source -> server && source.hasPermission(2)).executes(context -> {
                    ran(context.getSource(), name, "reset");
                    int swept = ContentReset.run(context.getSource().getServer());
                    send(context.getSource(), ChatFormatting.GREEN, Component.literal("The map was reset, " + swept + " entity(s) swept"));
                    return 1;
                }))
                .then(Commands.literal("team").requires(source -> server).executes(context -> teamList(context, name))
                        .then(Commands.literal("list").executes(context -> teamList(context, name)))
                        .then(Commands.literal("leave").executes(context -> teamLeave(context, name)))
                        .then(Commands.literal("claim").executes(context -> teamClaim(context, name)))
                        .then(Commands.literal("join").executes(context -> teamJoin(context, name, null))
                                .then(Commands.argument("team", StringArgumentType.word()).suggests((context, suggestions) -> {
                                    for (String known : ContentTeams.joinableNames()) { suggestions.suggest(known); }
                                    return suggestions.buildFuture();
                                }).executes(context -> teamJoin(context, name, StringArgumentType.getString(context, "team")))))
                        .then(Commands.literal("vote").then(Commands.argument("player", StringArgumentType.word()).suggests((context, suggestions) -> {
                            for (String known : context.getSource().getOnlinePlayerNames()) { suggestions.suggest(known); }
                            return suggestions.buildFuture();
                        }).executes(context -> teamVote(context, name, StringArgumentType.getString(context, "player"))))))
                .then(Commands.literal("biome")
                        .then(Commands.literal("here").executes(context -> {
                            ran(context.getSource(), name, "biome here");
                            biomeHere(context.getSource());
                            return 1;
                        }))
                        .then(Commands.literal("list").executes(context -> {
                            ran(context.getSource(), name, "biome list");
                            biomeList(context.getSource(), false);
                            return 1;
                        })
                        .then(Commands.literal("all").executes(context -> {
                            ran(context.getSource(), name, "biome list all");
                            biomeList(context.getSource(), true);
                            return 1;
                        }))))
                .then(Commands.literal("unused").executes(context -> {
                    ran(context.getSource(), name, "unused");
                    unused(context.getSource(), unusedNote);
                    return 1;
                }))
                .then(Commands.literal("config")
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
        if (server) {
            root
                    .then(Commands.literal("locate").then(Commands.argument("name", StringArgumentType.greedyString()).suggests((context, suggestions) -> {
                        for (String known : ContentLocate.names(context.getSource().getLevel())) { suggestions.suggest(known); }
                        return suggestions.buildFuture();
                    }).executes(context -> {
                        String target = StringArgumentType.getString(context, "name");
                        ran(context.getSource(), name, "locate " + target);
                        locate(context.getSource(), target);
                        return 1;
                    })))
                    .then(Commands.literal("goto")
                            .requires(source -> source.hasPermission(ContentStructureSearch.lowestLevel()))
                            .then(Commands.argument("place", StringArgumentType.string()).suggests((context, suggestions) -> {
                                for (String known : ContentLocate.names(context.getSource().getLevel())) { suggestions.suggest(known); }
                                for (String known : ContentStructureSearch.aliases()) { suggestions.suggest(known); }
                                return suggestions.buildFuture();
                            })
                                    .executes(context -> {
                                        String place = StringArgumentType.getString(context, "place");
                                        ran(context.getSource(), name, "goto " + place);
                                        return goTo(context.getSource(), place, "gotoLevel", Config.commands.gotoLevel(), false);
                                    })
                                    .then(Commands.literal("next").executes(context -> {
                                        String place = StringArgumentType.getString(context, "place");
                                        ran(context.getSource(), name, "goto " + place + " next");
                                        return goTo(context.getSource(), place, "gotoNextLevel", Config.commands.gotoNextLevel(), true);
                                    }))
                                    .then(Commands.literal("back").executes(context -> {
                                        String place = StringArgumentType.getString(context, "place");
                                        ran(context.getSource(), name, "goto " + place + " back");
                                        return goBack(context.getSource(), place);
                                    }))))
                    .then(Commands.literal("vein").then(Commands.argument("entry", ResourceLocationArgument.id()).suggests((context, suggestions) -> {
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
        return root;
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

    private static boolean denied(CommandSourceStack source, String place, String key, int fallback) {
        if (source.hasPermission(ContentStructureSearch.levelFor(place, key, fallback))) { return false; }
        source.sendFailure(tr("rdpl.command.gotodenied", place));
        return true;
    }

    static int goTo(CommandSourceStack source, String asked, String key, int fallback, boolean next) {
        if (denied(source, asked, key, fallback)) { return 0; }
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(tr("rdpl.command.gotonoplayer"));
            return 0;
        }
        ServerLevel level = source.getLevel();
        String place = ContentStructureSearch.named(asked);
        BlockPos from = BlockPos.containing(source.getPosition());
        BlockPos found = ContentLocate.names(level).contains(place) ? recorded(level, place, from, next ? ContentStructureSearch.been(player, place) : List.of())
                                                                   : ContentStructureSearch.find(level, place, from);
        if (found == null) {
            source.sendFailure(tr("rdpl.command.gotonothing", asked));
            return 0;
        }
        return carry(source, player, level, asked, place, found);
    }

    @Nullable private static BlockPos recorded(ServerLevel level, String place, BlockPos from, List<BlockPos> skip) {
        BlockPos found = ContentLocate.nearest(level, place, from);
        if (found == null || skip.isEmpty() || !ContentStructureSearch.beenNear(skip, found)) { return found; }
        return ContentLocate.nearestBeyond(level, place, from, skip);
    }

    static int goBack(CommandSourceStack source, String asked) {
        if (denied(source, asked, "gotoBackLevel", Config.commands.gotoBackLevel())) { return 0; }
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(tr("rdpl.command.gotonoplayer"));
            return 0;
        }
        String place = ContentStructureSearch.named(asked);
        BlockPos previous = ContentStructureSearch.stepBack(player, place);
        if (previous == null) {
            source.sendFailure(tr("rdpl.command.gotonoback", asked));
            return 0;
        }
        return carry(source, player, source.getLevel(), asked, place, previous);
    }

    private static int carry(CommandSourceStack source, ServerPlayer player, ServerLevel level, String asked, String place, BlockPos found) {
        BlockPos landing = ContentStructureSearch.landing(level, found);
        if (landing == null) {
            source.sendFailure(tr("rdpl.command.gotonoground", asked, found.getX(), found.getZ()));
            return 0;
        }
        ContentStructureSearch.remember(player, place, found);
        player.teleportTo(level, landing.getX() + 0.5D, ContentStructureSearch.stand(level, landing), landing.getZ() + 0.5D, player.getYRot(), player.getXRot());
        send(source, ChatFormatting.GREEN, tr("rdpl.command.gotodone", asked, landing.getX(), landing.getY(), landing.getZ()));
        return 1;
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
    }

    static void list(CommandSourceStack source, String name) {
        List<RDPLPack> packs = PackManager.get().getPacks();
        if (packs.isEmpty()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nopacks", String.valueOf(PackManager.get().getRoot())));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.packs", packs.size()));
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " [" + pack.getPriority() + "]" : "";
            String detail = "files=" + pack.getFileCount()
                    + "\nassets=" + pack.getNamespaces(PackType.CLIENT_RESOURCES)
                    + "\ndata=" + pack.getNamespaces(PackType.SERVER_DATA);
            MutableComponent line = Component.literal("  " + pack.getName() + priority);
            if (pack.isOverriding()) { line.append(tr("rdpl.command.overriding")); }
            line.withStyle(style -> style.withColor(pack.isOverriding() ? ChatFormatting.AQUA : ChatFormatting.WHITE)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(detail + "\n").append(tr("rdpl.command.clickhint"))))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + name + " which " + firstNamespace(pack) + ":")));
            send(source, line, "  " + pack.getName() + priority + (pack.isOverriding() ? " (overriding)" : "") + " " + detail.replace('\n', ' '));
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

    static void which(CommandSourceStack source, String target) {
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
            send(source, ChatFormatting.GREEN, tr("rdpl.command.provided", shown, winner.getName(), winner.isOverriding() ? tr("rdpl.command.overriding") : Component.empty()));
            send(source, ChatFormatting.GRAY, tr("rdpl.command.providednote"));
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

    static void biomeHere(CommandSourceStack source) {
        BlockPos at = BlockPos.containing(source.getPosition());
        Holder<Biome> held = source.getUnsidedLevel().getBiome(at);
        String named = held.unwrapKey().map(key -> key.location().toString()).orElse("?");
        send(source, ChatFormatting.WHITE, tr("rdpl.command.here", named, at.getX(), at.getY(), at.getZ()));
    }

    static void biomeList(CommandSourceStack source, boolean all) {
        Registry<Biome> registry = source.registryAccess().registryOrThrow(Registries.BIOME);
        int vanilla = 0;
        int shown = 0;
        for (ResourceLocation key : registry.keySet().stream().sorted(Comparator.comparing(ResourceLocation::toString)).toList()) {
            if (!all && "minecraft".equals(key.getNamespace())) {
                vanilla++;
                continue;
            }
            send(source, ChatFormatting.GRAY, Component.literal("  " + key));
            shown++;
        }
        send(source, ChatFormatting.WHITE, tr("rdpl.command.biomes", shown, all || vanilla == 0 ? "" : Component.translatable("rdpl.command.biomesmore", vanilla).getString()));
    }

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

    static void gates(CommandSourceStack source) {
        if (!ContentGates.enabled()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nogates"));
            return;
        }
        Collection<GateDef> defs = ContentGates.all();
        send(source, ChatFormatting.GREEN, tr("rdpl.command.gates", defs.size()));
        for (GateDef def : defs) {
            send(source, ChatFormatting.WHITE, Component.literal("  " + def.key()
                    + "  dimension=" + def.dimension() + " scope=" + (def.global() ? GateDef.GLOBAL : GateDef.PLAYER)
                    + (def.open() ? Component.translatable("rdpl.command.open").getString() : "")));
        }
    }

    static void blockedReport(CommandSourceStack source, Map<String, Integer> blocked) {
        if (blocked.isEmpty()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.orenone"));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.oreblocked"));
        for (Map.Entry<String, Integer> entry : blocked.entrySet()) { send(source, ChatFormatting.GRAY, Component.literal("  " + entry.getKey() + ": " + entry.getValue())); }
    }

    static void gatesFor(CommandSourceStack source, ServerPlayer player) {
        if (!ContentGates.enabled()) {
            send(source, ChatFormatting.YELLOW, tr("rdpl.command.nogates"));
            return;
        }
        send(source, ChatFormatting.GREEN, tr("rdpl.command.gatesfor", player.getGameProfile().getName()));
        for (GateDef def : ContentGates.all()) {
            boolean open = ContentGates.unlocked(player, def);
            send(source, open ? ChatFormatting.WHITE : ChatFormatting.GRAY, Component.literal("  " + def.key())
                    .append(Component.translatable(open ? "rdpl.command.open" : "rdpl.command.closed")));
        }
    }

    private static int gateFor(CommandContext<CommandSourceStack> context, String name, boolean grant) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String asked = StringArgumentType.getString(context, "gate");
        ran(source, name, "gate " + (grant ? "grant " : "revoke ") + player.getGameProfile().getName() + " " + asked);
        GateDef def = ContentGates.find(asked);
        if (def == null) {
            send(source, ChatFormatting.RED, tr("rdpl.command.nogate", asked));
            return 0;
        }
        String scope = def.global() ? GateDef.GLOBAL : GateDef.PLAYER;
        if (grant) { ContentGates.unlock(player, def, false); }
        else { ContentGates.lock(player, def); }
        send(source, ChatFormatting.GREEN, tr(grant ? "rdpl.command.gateopened" : "rdpl.command.gateclosed", def.key(), player.getGameProfile().getName(), scope));
        return 1;
    }


    private static int teamList(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ran(source, name, "team");
        if (!ContentTeams.any()) {
            send(source, ChatFormatting.RED, Component.literal("No pack has fielded any team"));
            return 0;
        }
        ServerPlayer asking = source.getPlayer();
        for (TeamDef def : ContentTeams.all().values()) {
            String standing = def.joinable() ? "" : " (closed)";
            String lead = ContentTeams.leadOf(source.getLevel(), def);
            if (lead != null) { standing = standing + " - lead " + lead; }
            send(source, def.color(), Component.literal(def.name() + " - " + def.displayName() + standing));
        }
        String on = asking == null ? null : ContentTeams.standingOf(asking);
        send(source, ChatFormatting.GRAY, Component.literal(on == null ? "You are on no team" : "You are on " + on));
        return 1;
    }

    @Nullable private static TeamDef mine(ServerPlayer player) {
        String standing = ContentTeams.standingOf(player);
        return standing == null ? null : ContentTeams.named(standing);
    }

    @Nullable private static ServerPlayer teamPlayer(CommandSourceStack source, String name, String action) {
        ran(source, name, "team " + action);
        if (!ContentTeams.any()) {
            send(source, ChatFormatting.RED, Component.literal("No pack has fielded any team"));
            return null;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) { send(source, ChatFormatting.RED, Component.literal("Only a player can join or leave a team")); }
        return player;
    }

    private static int teamVote(CommandContext<CommandSourceStack> context, String name, String choice) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "vote " + choice);
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            send(source, ChatFormatting.RED, Component.literal("You are on no team, so there is nobody to vote for"));
            return 0;
        }
        if (!TeamDef.VOTE.equals(mine.lead())) {
            send(source, ChatFormatting.RED, Component.literal(mine.displayName() + " does not choose its lead by a vote"));
            return 0;
        }
        boolean stood = ContentTeams.standsFor(player.serverLevel(), player.getGameProfile().getName(), choice, mine);
        send(source, stood ? mine.color() : ChatFormatting.RED, Component.literal(stood ? "You voted for " + choice : choice + " is not on your team"));
        if (stood) {
            String lead = ContentTeams.leadOf(player.serverLevel(), mine);
            send(source, ChatFormatting.GRAY, Component.literal(lead == null ? "The vote is tied, so nobody leads" : lead + " leads " + mine.displayName()));
        }
        return stood ? 1 : 0;
    }

    private static int teamClaim(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "claim");
        if (player == null) { return 0; }
        TeamDef mine = mine(player);
        if (mine == null) {
            send(source, ChatFormatting.RED, Component.literal("You are on no team, so there is nothing to claim"));
            return 0;
        }
        if (!TeamDef.CLAIM.equals(mine.lead())) {
            send(source, ChatFormatting.RED, Component.literal(mine.displayName() + " does not let its lead be claimed"));
            return 0;
        }
        boolean took = ContentTeams.claim(player.serverLevel(), player.getGameProfile().getName(), mine);
        send(source, took ? mine.color() : ChatFormatting.RED, Component.literal(took ? "You lead " + mine.displayName() : ContentTeams.holding(mine) + " already leads " + mine.displayName()));
        return took ? 1 : 0;
    }

    private static int teamLeave(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "leave");
        if (player == null) { return 0; }
        if (ContentScoring.roundRunning()) {
            send(source, ChatFormatting.RED, Component.literal("A round is running, so you cannot leave your team until it ends"));
            return 0;
        }
        send(source, ChatFormatting.GREEN, Component.literal(ContentTeams.stand(player) ? "You left your team" : "You were on no team"));
        return 1;
    }

    private static int teamJoin(CommandContext<CommandSourceStack> context, String name, @Nullable String asked) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = teamPlayer(source, name, "join " + (asked == null ? "" : asked));
        if (player == null) { return 0; }
        TeamDef wanted = asked != null ? ContentTeams.named(asked) : ContentTeams.smallest(player.serverLevel());
        if (asked == null && wanted == null) {
            send(source, ChatFormatting.RED, Component.literal("No team takes players by balance, so name the one you want"));
            return 0;
        }
        if (wanted == null) {
            send(source, ChatFormatting.RED, Component.literal("There is no team called " + asked));
            return 0;
        }
        if (!wanted.joinable()) {
            send(source, ChatFormatting.RED, Component.literal(wanted.name() + " is not a team you can join"));
            return 0;
        }
        if (ContentScoring.roundRunning()) {
            ContentTeams.waitFor(player, wanted);
            send(source, ChatFormatting.GRAY, Component.literal("A round is running, so you join " + wanted.displayName() + " when it ends"));
            return 1;
        }
        ContentTeams.take(player, wanted);
        send(source, wanted.color(), Component.literal("You joined " + wanted.displayName()));
        return 1;
    }
}
