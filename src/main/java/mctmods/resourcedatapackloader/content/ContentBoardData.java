package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.board.BoardGame;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ContentBoardData extends SavedData {
    private static final String NAME = "rdpl_boards";
    private static final String TAG = "rdplBoards";
    private static final Factory<ContentBoardData> FACTORY = new Factory<>(ContentBoardData::new, (tag, lookup) -> read(tag));
    final Map<String, BoardGame> boards = new LinkedHashMap<>();

    static ContentBoardData get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static ContentBoardData read(CompoundTag compound) {
        ContentBoardData data = new ContentBoardData();
        CompoundTag all = compound.getCompound(TAG);
        for (String name : all.getAllKeys()) {
            CompoundTag one = all.getCompound(name);
            BoardGame game = new BoardGame(name, one.getString("game"), one.getString("dimension"), one.getInt("x"), one.getInt("y"), one.getInt("z"));
            for (int move : one.getIntArray("moves")) { game.moves.add(move); }
            for (int side = 0; side < 2; side++) {
                game.owners[side] = one.getString("owner" + side);
                game.teams[side] = one.getBoolean("team" + side);
            }
            copy(one.getIntArray("levels"), game.levels);
            copy(one.getIntArray("clock"), game.clock);
            game.winner = one.contains("winner") ? one.getInt("winner") : BoardGame.RUNNING;
            game.reason = one.getString("reason");
            game.offer = one.contains("offer") ? one.getInt("offer") : -1;
            game.takeback = one.contains("takeback") ? one.getInt("takeback") : -1;
            game.spawn = one.getLong("spawn");
            data.boards.put(name, game);
        }
        return data;
    }

    private static void copy(int[] from, int[] into) { System.arraycopy(from, 0, into, 0, Math.min(from.length, into.length)); }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound, @Nonnull HolderLookup.Provider lookup) {
        CompoundTag all = new CompoundTag();
        for (BoardGame game : boards.values()) {
            CompoundTag one = new CompoundTag();
            one.putString("game", game.game);
            one.putString("dimension", game.dimension);
            one.putInt("x", game.x);
            one.putInt("y", game.y);
            one.putInt("z", game.z);
            one.putIntArray("moves", game.moves);
            for (int side = 0; side < 2; side++) {
                one.putString("owner" + side, game.owners[side]);
                one.putBoolean("team" + side, game.teams[side]);
            }
            one.putIntArray("levels", game.levels.clone());
            one.putIntArray("clock", game.clock.clone());
            one.putInt("winner", game.winner);
            one.putString("reason", game.reason);
            one.putInt("offer", game.offer);
            one.putInt("takeback", game.takeback);
            one.putLong("spawn", game.spawn);
            all.put(game.name, one);
        }
        compound.put(TAG, all);
        return compound;
    }
}
