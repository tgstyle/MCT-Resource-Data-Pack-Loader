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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
    private static boolean armed;
    private static int beat;

    private ContentBoards() {}

    public static void load() {
        RULES.clear();
        Json.eachFile(PackManager.GAMES, "game file", (key, contents) -> {
            JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
            if (json == null) { return; }
            String name = key.getPath();
            if (RULES.containsKey(name)) {
                ContentLog.LOGGER.error("Game file {} names the game '{}', which another pack already named, so the later one is left out", key, name);
                return;
            }
            BoardRules rules = BoardRules.parse(name, json);
            if (rules != null) { RULES.put(name, rules); }
        });
        if (RULES.isEmpty()) { return; }
        Summary.info("games", "Playing " + RULES.size() + " board game(s) " + RULES.keySet());
        if (!armed) {
            MinecraftForge.EVENT_BUS.register(ContentBoards.class);
            armed = true;
        }
    }

    public static List<String> gameNames() { return new ArrayList<>(RULES.keySet()); }

    @Nullable public static BoardRules rules(String game) { return RULES.get(game); }

    @Nullable public static BoardGame board(MinecraftServer server, String name) {
        ContentBoardData data = ContentBoardData.get(server);
        return data == null ? null : data.boards.get(name);
    }

    public static List<String> boardNames(MinecraftServer server) {
        ContentBoardData data = ContentBoardData.get(server);
        return data == null ? new ArrayList<>() : new ArrayList<>(data.boards.keySet());
    }

    public static boolean piece(Entity entity) { return ContentBoardPieces.boardOf(entity) != null; }

    @Nullable static World world(MinecraftServer server, BoardGame game) {
        try { return server.getWorld(Integer.parseInt(game.dimension)); }
        catch (NumberFormatException notDimension) { return null; }
    }

    static void dirty(MinecraftServer server) {
        ContentBoardData data = ContentBoardData.get(server);
        if (data != null) { data.markDirty(); }
    }

    public static void rebuild(MinecraftServer server) {
        ContentBoardData data = ContentBoardData.get(server);
        if (data == null) { return; }
        Random random = server.getWorld(0).rand;
        for (BoardGame game : data.boards.values()) {
            BoardRules rules = RULES.get(game.game);
            World world = world(server, game);
            if (rules == null || world == null) {
                ContentLog.LOGGER.error("Board {} plays {} in dimension {}, which this pack no longer has, so its pieces wait", game.name, game.game, game.dimension);
                continue;
            }
            ContentBoardPieces.clear(world, game);
            game.spawn = random.nextLong();
            BoardState state = game.replay(rules);
            game.thinking = false;
            game.wait = SECOND * 2;
            game.version++;
            ContentBoardPieces.sync(world, game, rules);
            int pieces = 0;
            for (int square = 0; square < state.size(); square++) { pieces += state.at(square) == 0 ? 0 : 1; }
            ContentLog.LOGGER.info("Board {} ({}) rebuilt from saved data at ply {}: {} piece(s) on the board, {} beside it", game.name, game.game, game.moves.size(), pieces, state.taken.get(0).size() + state.taken.get(1).size());
        }
        data.markDirty();
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        ContentBoardData data = server == null ? null : ContentBoardData.get(server);
        if (data == null) { return; }
        ContentBoardPlay.expire(server);
        boolean second = ++beat % SECOND == 0;
        for (BoardGame game : new ArrayList<>(data.boards.values())) {
            BoardRules rules = RULES.get(game.game);
            BoardState state = game.state;
            World world = rules != null && game.strike > 0 && --game.strike == 0 ? world(server, game) : null;
            if (world != null) { ContentBoardPieces.sync(world, game, rules); }
            if (rules == null || state == null || !game.running()) { continue; }
            if (rules.clockTicks > 0 && !game.moves.isEmpty()) {
                if (--game.clock[state.turn] <= 0) {
                    game.clock[state.turn] = 0;
                    ContentBoardPlay.finish(server, game, rules, 1 - state.turn, "time");
                    continue;
                }
                if (second) { data.markDirty(); }
            }
            if (game.wait > 0) { game.wait--; }
            else if (!game.thinking && game.computer(state.turn)) { think(server, game, state, game.level(state.turn, rules)); }
        }
    }

    private static void think(MinecraftServer server, BoardGame game, BoardState state, int level) {
        BoardState snapshot = state.copy();
        int version = game.version;
        long seed = server.getWorld(0).rand.nextLong();
        game.thinking = true;
        THINKER.execute(() -> {
            int move = BoardSearch.best(snapshot, level, new Random(seed));
            server.addScheduledTask(() -> ContentBoardPlay.answer(server, game, version, move));
        });
    }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        String board = ContentBoardPieces.boardOf(entity);
        if (board == null || event.getWorld().isRemote) { return; }
        MinecraftServer server = event.getWorld().getMinecraftServer();
        ContentBoardData data = server == null ? null : ContentBoardData.get(server);
        if (data != null && ContentBoardPieces.stale(entity, data.boards.get(board))) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        if (ContentBoardPieces.boardOf(event.getTarget()) != null) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onTouch(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        String board = ContentBoardPieces.boardOf(target);
        if (board == null) { return; }
        event.setCanceled(true);
        if (event.getWorld().isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        BoardGame game = found(event.getWorld(), board);
        if (game != null) { ContentBoardPlay.clickPiece((EntityPlayerMP) event.getEntityPlayer(), game, target); }
    }

    @SubscribeEvent public static void onSquare(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote || event.getHand() != EnumHand.MAIN_HAND || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        MinecraftServer server = event.getWorld().getMinecraftServer();
        ContentBoardData data = server == null ? null : ContentBoardData.get(server);
        if (data == null) { return; }
        String dimension = Integer.toString(event.getWorld().provider.getDimension());
        for (BoardGame game : data.boards.values()) {
            BoardRules rules = RULES.get(game.game);
            if (rules == null || !game.dimension.equals(dimension)) { continue; }
            int square = ContentBoardPieces.square(game, rules, event.getPos());
            if (square >= 0 && ContentBoardPlay.clickSquare((EntityPlayerMP) event.getEntityPlayer(), game, square)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @Nullable private static BoardGame found(World world, String board) {
        MinecraftServer server = world.getMinecraftServer();
        return server == null ? null : board(server, board);
    }
}
