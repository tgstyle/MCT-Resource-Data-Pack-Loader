package mctmods.resourcedatapackloader.content.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BoardState {
    public static final int CAPTURE = 1;
    public static final int HOP = 2;
    public static final int PASSANT = 4;
    public static final int CASTLE = 8;
    public static final int RUNNING = 0;
    public static final int WON = 1;
    public static final int DRAWN = 2;
    private static final int MOVED = 1 << 9;
    final BoardRules rules;
    final int[] cells;
    public int turn;
    int passTarget = -1;
    int passVictim = -1;
    int chain = -1;
    int quiet;
    public int plies;
    public final List<List<Integer>> taken = Arrays.asList(new ArrayList<>(), new ArrayList<>());
    private Seen seen;

    public BoardState(BoardRules rules) {
        this.rules = rules;
        cells = rules.setup.clone();
        seen = new Seen(key(), null);
    }

    private BoardState(BoardState from) {
        rules = from.rules;
        cells = from.cells.clone();
        turn = from.turn;
        passTarget = from.passTarget;
        passVictim = from.passVictim;
        chain = from.chain;
        quiet = from.quiet;
        plies = from.plies;
        seen = from.seen;
        for (int side = 0; side < 2; side++) { taken.get(side).addAll(from.taken.get(side)); }
    }

    public BoardState copy() { return new BoardState(this); }

    static int cell(int kind, int side, boolean moved) { return kind + 1 | side << 8 | (moved ? MOVED : 0); }

    public static int kind(int cell) { return (cell & 0xFF) - 1; }

    public static int side(int cell) { return cell >> 8 & 1; }

    private static boolean moved(int cell) { return (cell & MOVED) != 0; }

    public int at(int square) { return cells[square]; }

    public int size() { return cells.length; }

    public static int from(int move) { return move & 0xFF; }

    public static int to(int move) { return move >> 8 & 0xFF; }

    public static int promotion(int move) { return (move >> 16 & 0xFF) - 1; }

    private static int flags(int move) { return move >>> 24; }

    private static int move(int from, int to, int promotion, int flags) { return from | to << 8 | promotion + 1 << 16 | flags << 24; }

    public static boolean captures(int move) { return (flags(move) & CAPTURE) != 0; }

    public List<Integer> legal() {
        List<Integer> found = new ArrayList<>();
        for (int move : pseudo(turn)) {
            if (!rules.royal || !exposes(move)) { found.add(move); }
        }
        if (!rules.mustCapture && chain < 0) { return found; }
        List<Integer> forced = new ArrayList<>();
        for (int move : found) {
            if (captures(move)) { forced.add(move); }
        }
        return forced.isEmpty() ? found : forced;
    }

    private boolean exposes(int move) {
        BoardState after = copy();
        after.play(move);
        return after.checked(turn);
    }

    public boolean checked(int side) {
        for (int square = 0; square < cells.length; square++) {
            int held = cells[square];
            if (held != 0 && side(held) == side && rules.pieces.get(kind(held)).royal && attacked(square, 1 - side)) { return true; }
        }
        return false;
    }

    private int forward(int side) { return side == 0 ? 1 : -1; }

    private int square(int file, int rank) { return file < 0 || rank < 0 || file >= rules.files || rank >= rules.ranks ? -1 : rank * rules.files + file; }

    private List<Integer> pseudo(int side) {
        List<Integer> found = new ArrayList<>();
        for (int from = 0; from < cells.length; from++) {
            int held = cells[from];
            if (held == 0 || side(held) != side || chain >= 0 && from != chain) { continue; }
            BoardPiece piece = rules.pieces.get(kind(held));
            for (BoardPiece.Steps steps : piece.moves) {
                if (chain >= 0 && steps.mode() != BoardPiece.Steps.HOP) { continue; }
                for (int[] step : steps.steps()) { reach(found, from, held, piece, steps, step[0], step[1] * forward(side)); }
            }
            if (chain < 0 && piece.castles >= 0 && !moved(held)) { castles(found, from, side, piece.castles); }
        }
        return found;
    }

    private void reach(List<Integer> found, int from, int held, BoardPiece piece, BoardPiece.Steps steps, int dx, int dy) {
        int file = from % rules.files;
        int rank = from / rules.files;
        int side = side(held);
        if (steps.mode() == BoardPiece.Steps.HOP) {
            int over = square(file + dx, rank + dy);
            int land = square(file + 2 * dx, rank + 2 * dy);
            if (over >= 0 && land >= 0 && cells[over] != 0 && side(cells[over]) != side && cells[land] == 0) { add(found, from, land, held, piece, CAPTURE | HOP); }
            return;
        }
        int range = steps.slides() ? Math.max(rules.files, rules.ranks) : moved(held) ? 1 : steps.firstRange();
        for (int step = 1; step <= range; step++) {
            int to = square(file + step * dx, rank + step * dy);
            if (to < 0) { return; }
            int there = cells[to];
            if (there == 0) {
                if (steps.mode() != BoardPiece.Steps.ONLY) { add(found, from, to, held, piece, 0); }
                else if (piece.enPassant && to == passTarget && step == 1) { add(found, from, to, held, piece, CAPTURE | PASSANT); }
                continue;
            }
            if (side(there) != side && steps.mode() != BoardPiece.Steps.NEVER) { add(found, from, to, held, piece, CAPTURE); }
            return;
        }
    }

    private void add(List<Integer> found, int from, int to, int held, BoardPiece piece, int flags) {
        int rank = to / rules.files;
        boolean last = side(held) == 0 ? rank == rules.ranks - 1 : rank == 0;
        if (!last || piece.promotes.length == 0) {
            found.add(move(from, to, -1, flags));
            return;
        }
        for (int kind : piece.promotes) { found.add(move(from, to, kind, flags)); }
    }

    private void castles(List<Integer> found, int from, int side, int partner) {
        int file = from % rules.files;
        int rank = from / rules.files;
        if (attacked(from, 1 - side)) { return; }
        for (int dir = -1; dir <= 1; dir += 2) {
            int land = square(file + 2 * dir, rank);
            if (land < 0) { continue; }
            for (int look = file + dir; look >= 0 && look < rules.files; look += dir) {
                int there = cells[rank * rules.files + look];
                if (there == 0) { continue; }
                if (side(there) == side && kind(there) == partner && !moved(there) && Math.abs(look - file) > 2 && !attacked(square(file + dir, rank), 1 - side)) {
                    found.add(move(from, land, -1, CASTLE));
                }
                break;
            }
        }
    }

    boolean attacked(int square, int by) {
        for (int from = 0; from < cells.length; from++) {
            int held = cells[from];
            if (held == 0 || side(held) != by) { continue; }
            int file = from % rules.files;
            int rank = from / rules.files;
            for (BoardPiece.Steps steps : rules.pieces.get(kind(held)).moves) {
                if (steps.mode() == BoardPiece.Steps.NEVER) { continue; }
                for (int[] step : steps.steps()) {
                    int dy = step[1] * forward(by);
                    if (steps.mode() == BoardPiece.Steps.HOP) {
                        int land = square(file + 2 * step[0], rank + 2 * dy);
                        if (square(file + step[0], rank + dy) == square && land >= 0 && cells[land] == 0) { return true; }
                        continue;
                    }
                    int range = steps.slides() ? Math.max(rules.files, rules.ranks) : 1;
                    for (int at = 1; at <= range; at++) {
                        int to = square(file + at * step[0], rank + at * dy);
                        if (to < 0) { break; }
                        if (to == square) { return true; }
                        if (cells[to] != 0) { break; }
                    }
                }
            }
        }
        return false;
    }

    public int victim(int move) {
        int from = from(move);
        int to = to(move);
        int flags = flags(move);
        int victim = (flags & PASSANT) != 0 ? passVictim : (flags & HOP) != 0 ? square((from % rules.files + to % rules.files) / 2, (from / rules.files + to / rules.files) / 2) : to;
        return (flags & CAPTURE) != 0 && victim >= 0 && cells[victim] != 0 ? victim : -1;
    }

    public void play(int move) {
        int from = from(move);
        int to = to(move);
        int flags = flags(move);
        int held = cells[from];
        int side = side(held);
        BoardPiece piece = rules.pieces.get(kind(held));
        int victim = victim(move);
        if (victim >= 0) {
            taken.get(side).add(kind(cells[victim]));
            cells[victim] = 0;
        }
        int promoted = promotion(move);
        cells[to] = cell(promoted >= 0 ? promoted : kind(held), side, true);
        cells[from] = 0;
        if ((flags & CASTLE) != 0) { castle(from, to); }
        boolean doubled = piece.doubles() && from % rules.files == to % rules.files && Math.abs(to / rules.files - from / rules.files) == 2;
        passTarget = doubled ? (from + to) / 2 : -1;
        passVictim = doubled ? to : -1;
        quiet = (flags & CAPTURE) != 0 || piece.promotes.length > 0 ? 0 : quiet + 1;
        plies++;
        chain = -1;
        if ((flags & HOP) != 0 && rules.chainCaptures && promoted < 0) {
            chain = to;
            for (int next : pseudo(side)) {
                if ((flags(next) & HOP) != 0) { return; }
            }
            chain = -1;
        }
        turn = 1 - turn;
        seen = new Seen(key(), seen);
    }

    private long key() {
        long key = turn * 31L + passTarget;
        for (int cell : cells) { key = key * 1_000_003L + (cell == 0 || rules.keepsMoved(kind(cell)) ? cell : cell & ~MOVED); }
        return key;
    }

    private int repeats() {
        int count = 0;
        Seen at = seen;
        for (int left = quiet; at != null && left >= 0; left--) {
            if (at.key() == seen.key()) { count++; }
            at = at.next();
        }
        return count;
    }

    private boolean quietOut() { return rules.quietDraw > 0 && quiet >= rules.quietDraw; }

    private boolean repeated() { return rules.repeatDraw > 0 && repeats() >= rules.repeatDraw; }

    boolean drawnByRule() { return quietOut() || repeated(); }

    private void castle(int from, int to) {
        int dir = Integer.signum(to - from);
        for (int look = to + dir; look >= 0 && look < cells.length && look / rules.files == from / rules.files; look += dir) {
            if (cells[look] == 0) { continue; }
            cells[from + dir] = cells[look] | MOVED;
            cells[look] = 0;
            return;
        }
    }

    public int status() {
        if (drawnByRule()) { return DRAWN; }
        if (!legal().isEmpty()) { return RUNNING; }
        return rules.royal && !checked(turn) ? DRAWN : WON;
    }

    public String reason() {
        if (quietOut()) { return "quiet"; }
        if (repeated()) { return "repeat"; }
        if (!rules.royal) { return "nomoves"; }
        return checked(turn) ? "checkmate" : "stalemate";
    }

    public int find(int from, int to, int promotion) {
        int fallback = -1;
        for (int move : legal()) {
            if (from(move) != from || to(move) != to) { continue; }
            if (promotion(move) == promotion) { return move; }
            if (fallback == -1) { fallback = move; }
        }
        return promotion < 0 ? fallback : -1;
    }

    public List<Integer> targets(int from) {
        List<Integer> found = new ArrayList<>();
        for (int move : legal()) {
            if (from(move) == from && !found.contains(to(move))) { found.add(to(move)); }
        }
        return found;
    }

    private record Seen(long key, Seen next) {}
}
