package mctmods.resourcedatapackloader.common.board;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

public final class ContentBoardData extends WorldSavedData {
    private static final String NAME = "rdpl_boards";
    private static final String TAG = "rdplBoards";
    private static final String GAME = "game";
    private static final String DIMENSION = "dimension";
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

    public ContentBoardData(String name) { super(name); }

    @Nullable static ContentBoardData get(@Nullable MinecraftServer server) {
        return SavedData.overworld(server, ContentBoardData.class, NAME, ContentBoardData::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        boards.clear();
        NBTTagCompound all = compound.getCompoundTag(TAG);
        for (String name : all.getKeySet()) {
            NBTTagCompound one = all.getCompoundTag(name);
            BlockPos at = Coords.readPos(one);
            BoardGame game = new BoardGame(name, one.getString(GAME), one.getString(DIMENSION), at.getX(), at.getY(), at.getZ());
            for (int move : one.getIntArray(MOVES)) { game.moves.add(move); }
            for (int side = 0; side < 2; side++) {
                game.owners[side] = one.getString(OWNER + side);
                game.teams[side] = one.getBoolean(TEAM + side);
            }
            copy(one.getIntArray(LEVELS), game.levels);
            copy(one.getIntArray(CLOCK), game.clock);
            game.winner = one.hasKey(WINNER) ? one.getInteger(WINNER) : BoardGame.RUNNING;
            game.reason = one.getString(REASON);
            game.offer = one.hasKey(OFFER) ? one.getInteger(OFFER) : -1;
            game.takeback = one.hasKey(TAKEBACK) ? one.getInteger(TAKEBACK) : -1;
            game.spawn = one.getLong(SPAWN);
            boards.put(name, game);
        }
    }

    private static void copy(int[] from, int[] into) { System.arraycopy(from, 0, into, 0, Math.min(from.length, into.length)); }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound all = new NBTTagCompound();
        for (BoardGame game : boards.values()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setString(GAME, game.game);
            one.setString(DIMENSION, game.dimension);
            Coords.writePos(one, game.x, game.y, game.z);
            int[] moves = new int[game.moves.size()];
            for (int at = 0; at < moves.length; at++) { moves[at] = game.moves.get(at); }
            one.setIntArray(MOVES, moves);
            for (int side = 0; side < 2; side++) {
                one.setString(OWNER + side, game.owners[side]);
                one.setBoolean(TEAM + side, game.teams[side]);
            }
            one.setIntArray(LEVELS, game.levels.clone());
            one.setIntArray(CLOCK, game.clock.clone());
            one.setInteger(WINNER, game.winner);
            one.setString(REASON, game.reason);
            one.setInteger(OFFER, game.offer);
            one.setInteger(TAKEBACK, game.takeback);
            one.setLong(SPAWN, game.spawn);
            all.setTag(game.name, one);
        }
        compound.setTag(TAG, all);
        return compound;
    }
}
