package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.board.BoardGame;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ContentBoardData extends SavedData {
    private static final String NAME = "rdpl_boards";
    private static final String TAG = "rdplBoards";
    private static final SavedDataType<ContentBoardData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, NAME), ContentBoardData::new, CompoundTag.CODEC.xmap(ContentBoardData::read, ContentBoardData::write));
    final Map<String, BoardGame> boards = new LinkedHashMap<>();

    static ContentBoardData get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static ContentBoardData read(CompoundTag compound) {
        ContentBoardData data = new ContentBoardData();
        CompoundTag all = compound.getCompoundOrEmpty(TAG);
        for (String name : all.keySet()) {
            CompoundTag one = all.getCompoundOrEmpty(name);
            BoardGame game = new BoardGame(name, one.getStringOr("game", ""), one.getStringOr("dimension", ""), one.getIntOr("x", 0), one.getIntOr("y", 0), one.getIntOr("z", 0));
            for (int move : one.getIntArray("moves").orElse(new int[0])) { game.moves.add(move); }
            for (int side = 0; side < 2; side++) {
                game.owners[side] = one.getStringOr("owner" + side, "");
                game.teams[side] = one.getBooleanOr("team" + side, false);
            }
            copy(one.getIntArray("levels").orElse(new int[0]), game.levels);
            copy(one.getIntArray("clock").orElse(new int[0]), game.clock);
            game.winner = one.getIntOr("winner", BoardGame.RUNNING);
            game.reason = one.getStringOr("reason", "");
            game.offer = one.getIntOr("offer", -1);
            game.takeback = one.getIntOr("takeback", -1);
            game.spawn = one.getLongOr("spawn", 0L);
            data.boards.put(name, game);
        }
        return data;
    }

    private static void copy(int[] from, int[] into) { System.arraycopy(from, 0, into, 0, Math.min(from.length, into.length)); }

    private CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        CompoundTag all = new CompoundTag();
        for (BoardGame game : boards.values()) {
            CompoundTag one = new CompoundTag();
            one.putString("game", game.game);
            one.putString("dimension", game.dimension);
            one.putInt("x", game.x);
            one.putInt("y", game.y);
            one.putInt("z", game.z);
            one.putIntArray("moves", game.moves.stream().mapToInt(Integer::intValue).toArray());
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
