package mctmods.resourcedatapackloader.common.board;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.flag;
import static mctmods.resourcedatapackloader.core.util.json.Json.number;
import static mctmods.resourcedatapackloader.core.util.json.Json.object;
import static mctmods.resourcedatapackloader.core.util.json.Json.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public final class BoardRules {
    public static final int MOST_SQUARES = 16;
    public static final int MOST_LEVEL = 4;
    private static final List<String> GAME_KEYS = List.of("name", "board", "pieces", "sides", "rules", "clock", "ai", "result", "quiet", "setup");
    private static final List<String> BOARD_KEYS = List.of("files", "ranks", "light", "dark");
    private static final List<String> SIDE_KEYS = List.of("name", "color");
    private static final List<String> RULE_KEYS = List.of("mustCapture", "chainCaptures", "quietDraw", "repeatDraw");
    private static final List<String> CLOCK_KEYS = List.of("minutes", "addSeconds");
    private static final List<String> RESULT_KEYS = List.of("objective", "win", "draw", "loss");
    private static final List<String> PIECE_KEYS = List.of("letter", "mob", "mobs", "value", "royal", "enPassant", "castles", "promotes", "moves");
    private static final List<String> STEP_KEYS = List.of("captures", "steps", "slides", "firstRange");
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
    public final boolean quiet;
    final boolean royal;
    private final boolean[] keepsMoved;

    public int advance(int side, int rank) { return side == 0 ? rank : ranks - 1 - rank; }

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
        JsonArray sideList = GsonHelper.isArrayNode(json, "sides") ? json.getAsJsonArray("sides") : new JsonArray();
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
        clockTicks = Math.max(0, number(clock, "minutes", 0)) * SharedConstants.TICKS_PER_MINUTE;
        addTicks = Math.max(0, number(clock, "addSeconds", 0)) * SharedConstants.TICKS_PER_SECOND;
        ai = Mth.clamp(number(json, "ai", 2), 1, MOST_LEVEL);
        JsonObject result = object(json, "result");
        objective = text(result, "objective", "");
        win = number(result, "win", 1);
        draw = number(result, "draw", 0);
        loss = number(result, "loss", 0);
        quiet = flag(json, "quiet");
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

    private static void check(JsonObject json) {
        Json.unknownKeys(json, "the top level", GAME_KEYS);
        Json.unknownKeys(object(json, "board"), "board", BOARD_KEYS);
        Json.unknownKeys(object(json, "rules"), "rules", RULE_KEYS);
        Json.unknownKeys(object(json, "clock"), "clock", CLOCK_KEYS);
        Json.unknownKeys(object(json, "result"), "result", RESULT_KEYS);
        if (!GsonHelper.isArrayNode(json, "sides")) { return; }
        for (JsonElement side : json.getAsJsonArray("sides")) {
            if (side.isJsonObject()) { Json.unknownKeys(side.getAsJsonObject(), "sides", SIDE_KEYS); }
        }
    }

    @Nullable public static BoardRules parse(String id, JsonObject json) {
        check(json);
        JsonObject board = object(json, "board");
        int files = number(board, "files", 8);
        int ranks = number(board, "ranks", 8);
        if (files < 2 || ranks < 2 || files > MOST_SQUARES || ranks > MOST_SQUARES) {
            reject(id, "sets a board of {} by {}, and a board is 2 to 16 squares each way", files, ranks);
            return null;
        }
        List<BoardPiece> pieces = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object(json, "pieces").entrySet()) {
            BoardPiece piece = piece(id, entry.getKey(), entry.getValue());
            if (piece == null) { return null; }
            for (BoardPiece held : pieces) {
                if (held.letter == piece.letter) {
                    reject(id, "gives the letter '{}' to both {} and {}", piece.letter, held.name, piece.name);
                    return null;
                }
            }
            pieces.add(piece);
        }
        if (pieces.isEmpty() || pieces.size() > 250) {
            reject(id, "names {} piece(s), and a game has 1 to 250", pieces.size());
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
                    reject(id, "lets {} castle with {}, which is not one of its pieces", piece.name, piece.castlesWith);
                    return false;
                }
            }
            piece.promotes = new int[piece.promotesTo.size()];
            for (int at = 0; at < piece.promotes.length; at++) {
                piece.promotes[at] = index(pieces, piece.promotesTo.get(at));
                if (piece.promotes[at] < 0) {
                    reject(id, "promotes {} to {}, which is not one of its pieces", piece.name, piece.promotesTo.get(at));
                    return false;
                }
            }
        }
        return true;
    }

    private static void reject(String id, String what, Object... args) {
        Object[] all = new Object[args.length + 1];
        all[0] = id;
        System.arraycopy(args, 0, all, 1, args.length);
        ContentLog.LOGGER.error("Game file {} " + what + ", so it is left out", all);
    }

    public static int index(List<BoardPiece> pieces, String name) {
        for (int at = 0; at < pieces.size(); at++) {
            if (pieces.get(at).name.equals(name)) { return at; }
        }
        return -1;
    }

    @Nullable private static BoardPiece piece(String id, String name, JsonElement element) {
        if (!element.isJsonObject()) {
            reject(id, "gives the piece {} no object", name);
            return null;
        }
        if (name.isEmpty()) {
            reject(id, "has a piece with no name");
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        Json.unknownKeys(json, "pieces." + name, PIECE_KEYS);
        String letter = text(json, "letter", name.substring(0, 1)).toLowerCase(Locale.ROOT);
        if (letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            reject(id, "gives {} the letter '{}', and a letter is one letter", name, letter);
            return null;
        }
        List<String> mobs = new ArrayList<>(Json.strings(json, "mobs"));
        if (mobs.isEmpty() && json.has("mob")) { mobs.add(text(json, "mob", "")); }
        if (mobs.isEmpty() || mobs.contains("")) {
            reject(id, "names no mob for {}", name);
            return null;
        }
        List<BoardPiece.Steps> moves = new ArrayList<>();
        JsonArray list = GsonHelper.isArrayNode(json, "moves") ? json.getAsJsonArray("moves") : new JsonArray();
        for (JsonElement one : list) {
            BoardPiece.Steps steps = one.isJsonObject() ? steps(id, name, one.getAsJsonObject()) : null;
            if (steps == null) { return null; }
            moves.add(steps);
        }
        return new BoardPiece(name, letter.charAt(0), number(json, "value", 1), flag(json, "royal"), flag(json, "enPassant"),
                text(json, "castles", ""), Json.strings(json, "promotes"), moves, mobs);
    }

    @Nullable private static BoardPiece.Steps steps(String id, String name, JsonObject json) {
        Json.unknownKeys(json, "pieces." + name + ".moves", STEP_KEYS);
        String mode = text(json, "captures", "both");
        if (!BoardPiece.Steps.MODES.contains(mode)) {
            reject(id, "lets {} capture by '{}', which is not both, never, only or hop", name, mode);
            return null;
        }
        JsonArray list = GsonHelper.isArrayNode(json, "steps") ? json.getAsJsonArray("steps") : new JsonArray();
        int[][] steps = new int[list.size()][];
        for (int at = 0; at < steps.length; at++) {
            JsonElement step = list.get(at);
            if (!step.isJsonArray() || step.getAsJsonArray().size() != 2 || !step.getAsJsonArray().get(0).isJsonPrimitive() || !step.getAsJsonArray().get(1).isJsonPrimitive()) {
                reject(id, "gives {} a step that is not [files, ranks]", name);
                return null;
            }
            steps[at] = new int[] {step.getAsJsonArray().get(0).getAsInt(), step.getAsJsonArray().get(1).getAsInt()};
            if (steps[at][0] == 0 && steps[at][1] == 0) {
                reject(id, "gives {} the step [0, 0], which goes nowhere", name);
                return null;
            }
        }
        return new BoardPiece.Steps(steps, flag(json, "slides"), BoardPiece.Steps.MODES.indexOf(mode), Math.max(1, number(json, "firstRange", 1)));
    }

    @Nullable private static int[] setup(String id, JsonObject json, int files, int ranks, List<BoardPiece> pieces) {
        List<String> rows = Json.strings(json, "setup");
        if (rows.size() != ranks) {
            reject(id, "sets up {} rank(s) on a board of {}", rows.size(), ranks);
            return null;
        }
        int[] cells = new int[files * ranks];
        for (int rank = 0; rank < ranks; rank++) {
            String row = rows.get(rank);
            if (row.length() != files) {
                reject(id, "sets up rank {} as '{}', which is not {} squares", rank + 1, row, files);
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
                    reject(id, "sets up '{}' on rank {}, which is no piece's letter", letter, rank + 1);
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
}
