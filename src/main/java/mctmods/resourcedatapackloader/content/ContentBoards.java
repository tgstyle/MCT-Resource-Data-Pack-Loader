package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.content.board.BoardRules;
import mctmods.resourcedatapackloader.content.board.BoardSearch;
import mctmods.resourcedatapackloader.content.board.BoardState;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;

public final class ContentBoards {
    private static final Map<String, BoardRules> RULES = new LinkedHashMap<>();
    private static final int SECOND = 20;
    private static final ExecutorService THINKER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "RDPL board AI");
        thread.setDaemon(true);
        return thread;
    });
    private static int beat;

    private ContentBoards() {}

    public static void load() {
        RULES.clear();
        Json.eachFile(PackManager.GAMES, "game file", (key, contents) -> {
            JsonObject json = ContentParser.GSON.fromJson(contents, JsonObject.class);
            if (json == null) { return; }
            String name = key.getPath();
            if (RULES.containsKey(name)) {
                ContentLog.LOGGER.error("Game file {} names the game '{}', which another pack already named, so the later one is left out", key, name);
                return;
            }
            BoardRules rules = BoardRules.parse(name, json);
            if (rules != null) { RULES.put(name, rules); }
        });
        if (!RULES.isEmpty()) { Summary.info("games", "Playing " + RULES.size() + " board game(s) " + RULES.keySet()); }
    }

    public static List<String> gameNames() { return new ArrayList<>(RULES.keySet()); }

    @Nullable public static BoardRules rules(String game) { return RULES.get(game); }

    @Nullable public static BoardGame board(MinecraftServer server, String name) { return ContentBoardData.get(server).boards.get(name); }

    public static List<String> boardNames(MinecraftServer server) { return new ArrayList<>(ContentBoardData.get(server).boards.keySet()); }

    @Nullable static ServerLevel world(MinecraftServer server, BoardGame game) {
        ResourceLocation id = ResourceLocation.tryParse(game.dimension);
        return id == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }

    static void dirty(MinecraftServer server) { ContentBoardData.get(server).setDirty(); }

    public static void onServerStarted(ServerStartedEvent event) { rebuild(event.getServer()); }

    public static void rebuild(MinecraftServer server) {
        ContentBoardData data = ContentBoardData.get(server);
        for (BoardGame game : data.boards.values()) {
            BoardRules rules = RULES.get(game.game);
            ServerLevel level = world(server, game);
            if (rules == null || level == null) {
                ContentLog.LOGGER.error("Board {} plays {} in dimension {}, which this pack no longer has, so its pieces wait", game.name, game.game, game.dimension);
                continue;
            }
            ContentBoardPieces.clear(level, game);
            game.spawn = level.random.nextLong();
            BoardState state = game.replay(rules);
            game.thinking = false;
            game.wait = SECOND * 2;
            game.version++;
            ContentBoardPieces.sync(level, game, rules);
            int pieces = 0;
            for (int square = 0; square < state.size(); square++) { pieces += state.at(square) == 0 ? 0 : 1; }
            ContentLog.LOGGER.info("Board {} ({}) rebuilt from saved data at ply {}: {} piece(s) on the board, {} beside it", game.name, game.game, game.moves.size(), pieces, state.taken.get(0).size() + state.taken.get(1).size());
        }
        data.setDirty();
    }

    public static void onTick(ServerTickEvent.Post event) {
        if (!RULES.isEmpty()) { tick(event.getServer()); }
    }

    private static void tick(MinecraftServer server) {
        ContentBoardData data = ContentBoardData.get(server);
        ContentBoardPlay.expire(server);
        boolean second = ++beat % SECOND == 0;
        for (BoardGame game : new ArrayList<>(data.boards.values())) {
            BoardRules rules = RULES.get(game.game);
            BoardState state = game.state;
            if (rules == null || state == null || !game.running()) { continue; }
            if (rules.clockTicks > 0 && !game.moves.isEmpty()) {
                if (--game.clock[state.turn] <= 0) {
                    game.clock[state.turn] = 0;
                    ContentBoardPlay.finish(server, game, rules, 1 - state.turn, "time");
                    continue;
                }
                if (second) { data.setDirty(); }
            }
            if (game.wait > 0) { game.wait--; }
            else if (!game.thinking && game.computer(state.turn)) { think(server, game, state, game.level(state.turn, rules)); }
        }
    }

    private static void think(MinecraftServer server, BoardGame game, BoardState state, int level) {
        BoardState snapshot = state.copy();
        int version = game.version;
        long seed = server.overworld().random.nextLong();
        game.thinking = true;
        THINKER.execute(() -> {
            int move = BoardSearch.best(snapshot, level, new Random(seed));
            server.execute(() -> ContentBoardPlay.answer(server, game, version, move));
        });
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        String board = ContentBoardPieces.boardOf(entity);
        if (board == null || event.getLevel().isClientSide() || event.getLevel().getServer() == null) { return; }
        if (ContentBoardPieces.stale(entity, board(event.getLevel().getServer(), board))) { event.setCanceled(true); }
    }

    public static void onAttack(AttackEntityEvent event) {
        if (ContentBoardPieces.boardOf(event.getTarget()) != null) { event.setCanceled(true); }
    }

    public static void onTouch(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        String board = ContentBoardPieces.boardOf(target);
        if (board == null) { return; }
        event.setCanceled(true);
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        BoardGame game = board(player.server, board);
        if (game != null) { ContentBoardPlay.clickPiece(player, game, target); }
    }

    public static void onSquare(PlayerInteractEvent.RightClickBlock event) {
        if (RULES.isEmpty() || event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        String dimension = player.level().dimension().location().toString();
        for (BoardGame game : ContentBoardData.get(player.server).boards.values()) {
            BoardRules rules = RULES.get(game.game);
            if (rules == null || !game.dimension.equals(dimension)) { continue; }
            int square = ContentBoardPieces.square(game, rules, event.getPos());
            if (square >= 0 && ContentBoardPlay.clickSquare(player, game, square)) {
                event.setCanceled(true);
                return;
            }
        }
    }
}
