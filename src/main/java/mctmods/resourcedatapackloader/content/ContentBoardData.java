package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.board.BoardGame;
import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentBoardData extends WorldSavedData {
    private static final String NAME = "rdpl_boards";
    private static final String TAG = "rdplBoards";
    final Map<String, BoardGame> boards = new LinkedHashMap<>();

    public ContentBoardData(String name) { super(name); }

    @Nullable static ContentBoardData get(MinecraftServer server) {
        MapStorage storage = server.getWorld(0).getMapStorage();
        return storage == null ? null : SavedData.get(storage, ContentBoardData.class, NAME, ContentBoardData::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        boards.clear();
        NBTTagCompound all = compound.getCompoundTag(TAG);
        for (String name : all.getKeySet()) {
            NBTTagCompound one = all.getCompoundTag(name);
            BoardGame game = new BoardGame(name, one.getString("game"), one.getString("dimension"), one.getInteger("x"), one.getInteger("y"), one.getInteger("z"));
            for (int move : one.getIntArray("moves")) { game.moves.add(move); }
            for (int side = 0; side < 2; side++) {
                game.owners[side] = one.getString("owner" + side);
                game.teams[side] = one.getBoolean("team" + side);
            }
            copy(one.getIntArray("levels"), game.levels);
            copy(one.getIntArray("clock"), game.clock);
            game.winner = one.hasKey("winner") ? one.getInteger("winner") : BoardGame.RUNNING;
            game.reason = one.getString("reason");
            game.offer = one.hasKey("offer") ? one.getInteger("offer") : -1;
            game.takeback = one.hasKey("takeback") ? one.getInteger("takeback") : -1;
            game.spawn = one.getLong("spawn");
            boards.put(name, game);
        }
    }

    private static void copy(int[] from, int[] into) { System.arraycopy(from, 0, into, 0, Math.min(from.length, into.length)); }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound all = new NBTTagCompound();
        for (BoardGame game : boards.values()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setString("game", game.game);
            one.setString("dimension", game.dimension);
            one.setInteger("x", game.x);
            one.setInteger("y", game.y);
            one.setInteger("z", game.z);
            int[] moves = new int[game.moves.size()];
            for (int at = 0; at < moves.length; at++) { moves[at] = game.moves.get(at); }
            one.setIntArray("moves", moves);
            for (int side = 0; side < 2; side++) {
                one.setString("owner" + side, game.owners[side]);
                one.setBoolean("team" + side, game.teams[side]);
            }
            one.setIntArray("levels", game.levels.clone());
            one.setIntArray("clock", game.clock.clone());
            one.setInteger("winner", game.winner);
            one.setString("reason", game.reason);
            one.setInteger("offer", game.offer);
            one.setInteger("takeback", game.takeback);
            one.setLong("spawn", game.spawn);
            all.setTag(game.name, one);
        }
        compound.setTag(TAG, all);
        return compound;
    }
}
