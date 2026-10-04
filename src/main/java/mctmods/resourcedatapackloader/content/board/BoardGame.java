package mctmods.resourcedatapackloader.content.board;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class BoardGame {
    public static final int RUNNING = -1;
    public static final int DRAW = 2;
    public final String name;
    public final String game;
    public final String dimension;
    public final int x;
    public final int y;
    public final int z;
    public final List<Integer> moves = new ArrayList<>();
    public final String[] owners = {"", ""};
    public final boolean[] teams = new boolean[2];
    public final int[] levels = new int[2];
    public final int[] clock = new int[2];
    public int winner = RUNNING;
    public String reason = "";
    public int offer = -1;
    public int takeback = -1;
    public long spawn;
    @Nullable public BoardState state;
    public boolean thinking;
    public int wait;
    public int version;

    public BoardGame(String name, String game, String dimension, int x, int y, int z) {
        this.name = name;
        this.game = game;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean running() { return winner == RUNNING; }

    public boolean computer(int side) { return owners[side].isEmpty() && (levels[side] > 0 || !owners[1 - side].isEmpty()); }

    public int level(int side, BoardRules rules) { return levels[side] > 0 ? levels[side] : rules.ai; }

    public BoardState replay(BoardRules rules) {
        BoardState rebuilt = new BoardState(rules);
        int kept = 0;
        for (int move : moves) {
            if (!rebuilt.legal().contains(move)) { break; }
            rebuilt.play(move);
            kept++;
        }
        moves.subList(kept, moves.size()).clear();
        state = rebuilt;
        return rebuilt;
    }
}
