package mctmods.resourcedatapackloader.content.board;

import java.util.List;

public final class BoardPiece {
    public final String name;
    public final char letter;
    public final int value;
    public final boolean royal;
    public final boolean enPassant;
    final String castlesWith;
    int castles = -1;
    final List<String> promotesTo;
    int[] promotes = new int[0];
    final List<Steps> moves;
    public final List<String> mobs;

    BoardPiece(String name, char letter, int value, boolean royal, boolean enPassant, String castlesWith, List<String> promotesTo, List<Steps> moves, List<String> mobs) {
        this.name = name;
        this.letter = letter;
        this.value = value;
        this.royal = royal;
        this.enPassant = enPassant;
        this.castlesWith = castlesWith;
        this.promotesTo = promotesTo;
        this.moves = moves;
        this.mobs = mobs;
    }

    boolean doubles() {
        for (Steps steps : moves) {
            if (steps.firstRange() > 1) { return true; }
        }
        return false;
    }

    public String mob(int side) { return mobs.get(Math.min(side, mobs.size() - 1)); }

    record Steps(int[][] steps, boolean slides, int mode, int firstRange) {
        static final int NEVER = 1;
        static final int ONLY = 2;
        static final int HOP = 3;
    }
}
