package mctmods.resourcedatapackloader.common.board;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public final class ContentBoardData extends SavedData {
    private static final String NAME = "rdpl_boards";
    private static final String TAG = "rdplBoards";
    private static final String GAME = "game";
    private static final String DIMENSION = "dimension";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String MOVES = "moves";
    private static final String OWNER = "owner";
    private static final String TEAM = "team";
    private static final String LEVELS = "levels";
    private static final String CLOCK = "clock";
    private static final String WINNER = "winner";
    private static final String REASON = "reason";
    private static final String OFFER = "offer";
    private static final String TAKEBACK = "takeback";
    private static final String SPAWN = "spawn";
    final Map<String, BoardGame> boards = new LinkedHashMap<>();

    static ContentBoardData get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(ContentBoardData::read, ContentBoardData::new, NAME); }

    private static ContentBoardData read(CompoundTag compound) {
        ContentBoardData data = new ContentBoardData();
        CompoundTag all = compound.getCompound(TAG);
        for (String name : all.getAllKeys()) {
            CompoundTag one = all.getCompound(name);
            BoardGame game = new BoardGame(name, one.getString(GAME), one.getString(DIMENSION), one.getInt(X), one.getInt(Y), one.getInt(Z));
            for (int move : one.getIntArray(MOVES)) { game.moves.add(move); }
            for (int side = 0; side < 2; side++) {
                game.owners[side] = one.getString(OWNER + side);
                game.teams[side] = one.getBoolean(TEAM + side);
            }
            copy(one.getIntArray(LEVELS), game.levels);
            copy(one.getIntArray(CLOCK), game.clock);
            game.winner = one.contains(WINNER) ? one.getInt(WINNER) : BoardGame.RUNNING;
            game.reason = one.getString(REASON);
            game.offer = one.contains(OFFER) ? one.getInt(OFFER) : -1;
            game.takeback = one.contains(TAKEBACK) ? one.getInt(TAKEBACK) : -1;
            game.spawn = one.getLong(SPAWN);
            data.boards.put(name, game);
        }
        return data;
    }

    private static void copy(int[] from, int[] into) { System.arraycopy(from, 0, into, 0, Math.min(from.length, into.length)); }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound) {
        CompoundTag all = new CompoundTag();
        for (BoardGame game : boards.values()) {
            CompoundTag one = new CompoundTag();
            one.putString(GAME, game.game);
            one.putString(DIMENSION, game.dimension);
            one.putInt(X, game.x);
            one.putInt(Y, game.y);
            one.putInt(Z, game.z);
            one.putIntArray(MOVES, game.moves);
            for (int side = 0; side < 2; side++) {
                one.putString(OWNER + side, game.owners[side]);
                one.putBoolean(TEAM + side, game.teams[side]);
            }
            one.putIntArray(LEVELS, game.levels.clone());
            one.putIntArray(CLOCK, game.clock.clone());
            one.putInt(WINNER, game.winner);
            one.putString(REASON, game.reason);
            one.putInt(OFFER, game.offer);
            one.putInt(TAKEBACK, game.takeback);
            one.putLong(SPAWN, game.spawn);
            all.put(game.name, one);
        }
        compound.put(TAG, all);
        return compound;
    }
}
