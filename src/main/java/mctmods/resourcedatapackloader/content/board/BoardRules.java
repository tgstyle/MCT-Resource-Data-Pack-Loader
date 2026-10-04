package mctmods.resourcedatapackloader.content.board;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class BoardRules {
    public static final int MOST_SQUARES = 16;
    public static final int MOST_LEVEL = 4;
    private static final List<String> MODES = java.util.Arrays.asList("both", "never", "only", "hop");
    public final String id;
    public final String name;
    public final int files;
    public final int ranks;
    public final String light;
    public final String dark;
    public final String[] sides;
    public final String[] colors;
    public final List<BoardPiece> pieces;
    final int[] setup;
    final boolean mustCapture;
    final boolean chainCaptures;
    final int quietDraw;
    final int repeatDraw;
    public final int clockTicks;
    public final int addTicks;
    public final int ai;
    public final String objective;
    public final int win;
    public final int draw;
    public final int loss;
    final boolean royal;
    private final boolean[] keepsMoved;

    private BoardRules(String id, JsonObject json, int files, int ranks, List<BoardPiece> pieces, int[] setup) {
        this.id = id;
        this.files = files;
        this.ranks = ranks;
        this.pieces = pieces;
        this.setup = setup;
        name = text(json, "name", id);
        JsonObject board = object(json, "board");
        light = text(board, "light", "");
        dark = text(board, "dark", "");
        JsonArray sideList = json.has("sides") && json.get("sides").isJsonArray() ? json.getAsJsonArray("sides") : new JsonArray();
        sides = new String[2];
        colors = new String[2];
        for (int side = 0; side < 2; side++) {
            JsonObject one = side < sideList.size() && sideList.get(side).isJsonObject() ? sideList.get(side).getAsJsonObject() : new JsonObject();
            sides[side] = text(one, "name", side == 0 ? "White" : "Black");
            colors[side] = text(one, "color", side == 0 ? "white" : "dark_gray").toLowerCase(Locale.ROOT);
        }
        JsonObject rules = object(json, "rules");
        mustCapture = flag(rules, "mustCapture");
        chainCaptures = flag(rules, "chainCaptures");
        quietDraw = Math.max(0, number(rules, "quietDraw", 0));
        repeatDraw = Math.max(0, number(rules, "repeatDraw", 0));
        JsonObject clock = object(json, "clock");
        clockTicks = Math.max(0, number(clock, "minutes", 0)) * 1200;
        addTicks = Math.max(0, number(clock, "addSeconds", 0)) * 20;
        ai = Mth.clamp(number(json, "ai", 2), 1, MOST_LEVEL);
        JsonObject result = object(json, "result");
        objective = text(result, "objective", "");
        win = number(result, "win", 1);
        draw = number(result, "draw", 0);
        loss = number(result, "loss", 0);
        boolean anyRoyal = false;
        for (BoardPiece piece : pieces) { anyRoyal |= piece.royal; }
        royal = anyRoyal;
        keepsMoved = new boolean[pieces.size()];
        for (int kind = 0; kind < pieces.size(); kind++) {
            BoardPiece piece = pieces.get(kind);
            if (piece.castles >= 0) { keepsMoved[kind] = keepsMoved[piece.castles] = true; }
            if (piece.doubles()) { keepsMoved[kind] = true; }
        }
    }

    boolean keepsMoved(int kind) { return keepsMoved[kind]; }

    @Nullable public static BoardRules parse(String id, JsonObject json) {
        JsonObject board = object(json, "board");
        int files = number(board, "files", 8);
        int ranks = number(board, "ranks", 8);
        if (files < 2 || ranks < 2 || files > MOST_SQUARES || ranks > MOST_SQUARES) {
            ContentLog.LOGGER.error("Game file {} sets a board of {} by {}, and a board is 2 to 16 squares each way, so it is left out", id, files, ranks);
            return null;
        }
        List<BoardPiece> pieces = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object(json, "pieces").entrySet()) {
            BoardPiece piece = piece(id, entry.getKey(), entry.getValue());
            if (piece == null) { return null; }
            for (BoardPiece held : pieces) {
                if (held.letter == piece.letter) {
                    ContentLog.LOGGER.error("Game file {} gives the letter '{}' to both {} and {}, so it is left out", id, piece.letter, held.name, piece.name);
                    return null;
                }
            }
            pieces.add(piece);
        }
        if (pieces.isEmpty() || pieces.size() > 250) {
            ContentLog.LOGGER.error("Game file {} names {} piece(s), and a game has 1 to 250, so it is left out", id, pieces.size());
            return null;
        }
        if (!linked(id, pieces)) { return null; }
        int[] setup = setup(id, json, files, ranks, pieces);
        return setup == null ? null : new BoardRules(id, json, files, ranks, Collections.unmodifiableList(pieces), setup);
    }

    private static boolean linked(String id, List<BoardPiece> pieces) {
        for (BoardPiece piece : pieces) {
            if (!piece.castlesWith.isEmpty()) {
                piece.castles = index(pieces, piece.castlesWith);
                if (piece.castles < 0) {
                    ContentLog.LOGGER.error("Game file {} lets {} castle with {}, which is not one of its pieces, so it is left out", id, piece.name, piece.castlesWith);
                    return false;
                }
            }
            piece.promotes = new int[piece.promotesTo.size()];
            for (int at = 0; at < piece.promotes.length; at++) {
                piece.promotes[at] = index(pieces, piece.promotesTo.get(at));
                if (piece.promotes[at] < 0) {
                    ContentLog.LOGGER.error("Game file {} promotes {} to {}, which is not one of its pieces, so it is left out", id, piece.name, piece.promotesTo.get(at));
                    return false;
                }
            }
        }
        return true;
    }

    public static int index(List<BoardPiece> pieces, String name) {
        for (int at = 0; at < pieces.size(); at++) {
            if (pieces.get(at).name.equals(name)) { return at; }
        }
        return -1;
    }

    @Nullable private static BoardPiece piece(String id, String name, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("Game file {} gives the piece {} no object, so it is left out", id, name);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        String letter = text(json, "letter", name.substring(0, 1)).toLowerCase(Locale.ROOT);
        if (letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            ContentLog.LOGGER.error("Game file {} gives {} the letter '{}', and a letter is one letter, so it is left out", id, name, letter);
            return null;
        }
        List<String> mobs = new ArrayList<>(Json.strings(json, "mobs"));
        if (mobs.isEmpty() && json.has("mob")) { mobs.add(text(json, "mob", "")); }
        if (mobs.isEmpty() || mobs.contains("")) {
            ContentLog.LOGGER.error("Game file {} names no mob for {}, so it is left out", id, name);
            return null;
        }
        List<BoardPiece.Steps> moves = new ArrayList<>();
        JsonArray list = json.has("moves") && json.get("moves").isJsonArray() ? json.getAsJsonArray("moves") : new JsonArray();
        for (JsonElement one : list) {
            BoardPiece.Steps steps = one.isJsonObject() ? steps(id, name, one.getAsJsonObject()) : null;
            if (steps == null) { return null; }
            moves.add(steps);
        }
        return new BoardPiece(name, letter.charAt(0), number(json, "value", 1), flag(json, "royal"), flag(json, "enPassant"),
                text(json, "castles", ""), Json.strings(json, "promotes"), moves, mobs);
    }

    @Nullable private static BoardPiece.Steps steps(String id, String name, JsonObject json) {
        String mode = text(json, "captures", "both");
        if (!MODES.contains(mode)) {
            ContentLog.LOGGER.error("Game file {} lets {} capture by '{}', which is not both, never, only or hop, so it is left out", id, name, mode);
            return null;
        }
        JsonArray list = json.has("steps") && json.get("steps").isJsonArray() ? json.getAsJsonArray("steps") : new JsonArray();
        int[][] steps = new int[list.size()][];
        for (int at = 0; at < steps.length; at++) {
            JsonElement step = list.get(at);
            if (!step.isJsonArray() || step.getAsJsonArray().size() != 2) {
                ContentLog.LOGGER.error("Game file {} gives {} a step that is not [files, ranks], so it is left out", id, name);
                return null;
            }
            steps[at] = new int[] {step.getAsJsonArray().get(0).getAsInt(), step.getAsJsonArray().get(1).getAsInt()};
            if (steps[at][0] == 0 && steps[at][1] == 0) {
                ContentLog.LOGGER.error("Game file {} gives {} the step [0, 0], which goes nowhere, so it is left out", id, name);
                return null;
            }
        }
        return new BoardPiece.Steps(steps, flag(json, "slides"), MODES.indexOf(mode), Math.max(1, number(json, "firstRange", 1)));
    }

    @Nullable private static int[] setup(String id, JsonObject json, int files, int ranks, List<BoardPiece> pieces) {
        List<String> rows = Json.strings(json, "setup");
        if (rows.size() != ranks) {
            ContentLog.LOGGER.error("Game file {} sets up {} rank(s) on a board of {}, so it is left out", id, rows.size(), ranks);
            return null;
        }
        int[] cells = new int[files * ranks];
        for (int rank = 0; rank < ranks; rank++) {
            String row = rows.get(rank);
            if (row.length() != files) {
                ContentLog.LOGGER.error("Game file {} sets up rank {} as '{}', which is not {} squares, so it is left out", id, rank + 1, row, files);
                return null;
            }
            for (int file = 0; file < files; file++) {
                char letter = row.charAt(file);
                if (letter == '.') { continue; }
                int kind = -1;
                for (int at = 0; at < pieces.size(); at++) {
                    if (pieces.get(at).letter == Character.toLowerCase(letter)) { kind = at; }
                }
                if (kind < 0) {
                    ContentLog.LOGGER.error("Game file {} sets up '{}' on rank {}, which is no piece's letter, so it is left out", id, letter, rank + 1);
                    return null;
                }
                cells[rank * files + file] = BoardState.cell(kind, Character.isUpperCase(letter) ? 0 : 1, false);
            }
        }
        return cells;
    }

    public String square(int square) { return (char) ('a' + square % files) + Integer.toString(square / files + 1); }

    public int square(String text) {
        if (text.length() < 2) { return -1; }
        int file = Character.toLowerCase(text.charAt(0)) - 'a';
        try {
            int rank = Integer.parseInt(text.substring(1)) - 1;
            return file < 0 || file >= files || rank < 0 || rank >= ranks ? -1 : rank * files + file;
        }
        catch (NumberFormatException notSquare) { return -1; }
    }

    private static JsonObject object(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonObject() ? json.getAsJsonObject(key) : new JsonObject(); }

    private static String text(JsonObject json, String key, String fallback) { return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString().trim() : fallback; }

    private static int number(JsonObject json, String key, int fallback) {
        try { return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsInt() : fallback; }
        catch (NumberFormatException notNumber) { return fallback; }
    }

    private static boolean flag(JsonObject json, String key) { return json.has(key) && json.get(key).isJsonPrimitive() && json.get(key).getAsBoolean(); }
}
