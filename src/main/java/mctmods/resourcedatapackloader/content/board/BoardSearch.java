package mctmods.resourcedatapackloader.content.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BoardSearch {
    private static final int MATE = 1_000_000;
    private static final long LIMIT_NANOS = 4_000_000_000L;
    private final long deadline;
    private boolean late;

    private BoardSearch() { deadline = System.nanoTime() + LIMIT_NANOS; }

    public static int best(BoardState root, int level, Random random) {
        BoardSearch search = new BoardSearch();
        List<Integer> moves = ordered(root.legal());
        if (moves.isEmpty()) { return -1; }
        int chosen = moves.get(random.nextInt(moves.size()));
        int noise = (BoardRules.MOST_LEVEL - level) * 60;
        for (int depth = 1; depth <= level; depth++) {
            int bestScore = Integer.MIN_VALUE;
            int bestMove = chosen;
            for (int move : moves) {
                int score = search.child(root, move, depth, -MATE * 2, MATE * 2, 1) + (noise > 0 ? random.nextInt(noise + 1) : 0);
                if (search.late) { return chosen; }
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
            chosen = bestMove;
        }
        return chosen;
    }

    private int child(BoardState state, int move, int depth, int alpha, int beta, int ply) {
        BoardState next = state.copy();
        next.play(move);
        if (next.turn == state.turn) { return search(next, depth, alpha, beta, ply + 1); }
        return -search(next, depth - 1, -beta, -alpha, ply + 1);
    }

    private int search(BoardState state, int depth, int alpha, int beta, int ply) {
        if (System.nanoTime() > deadline) {
            late = true;
            return 0;
        }
        List<Integer> moves = state.legal();
        if (moves.isEmpty()) { return state.rules.royal && !state.checked(state.turn) ? 0 : -MATE + ply; }
        if (state.drawnByRule()) { return 0; }
        if (depth <= 0) { return judge(state, state.turn); }
        int best = -MATE * 2;
        for (int move : ordered(moves)) {
            int score = child(state, move, depth, alpha, beta, ply);
            if (late) { return 0; }
            best = Math.max(best, score);
            alpha = Math.max(alpha, score);
            if (alpha >= beta) { break; }
        }
        return best;
    }

    private static List<Integer> ordered(List<Integer> moves) {
        List<Integer> sorted = new ArrayList<>();
        for (int move : moves) {
            if (BoardState.captures(move) || BoardState.promotion(move) >= 0) { sorted.add(move); }
        }
        for (int move : moves) {
            if (!BoardState.captures(move) && BoardState.promotion(move) < 0) { sorted.add(move); }
        }
        return sorted;
    }

    public static int judge(BoardState state, int side) {
        int total = 0;
        BoardRules rules = state.rules;
        for (int square = 0; square < state.size(); square++) {
            int held = state.at(square);
            if (held == 0) { continue; }
            BoardPiece piece = rules.pieces.get(BoardState.kind(held));
            int value = piece.value * 100;
            if (piece.promotes.length > 0) {
                int rank = square / rules.files;
                value += (BoardState.side(held) == 0 ? rank : rules.ranks - 1 - rank) * 8;
            }
            total += BoardState.side(held) == side ? value : -value;
        }
        return total;
    }
}
