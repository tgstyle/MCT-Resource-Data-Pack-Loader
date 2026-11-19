package mctmods.resourcedatapackloader.common.syncdata;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public interface DataReader {
    @Nullable SyncValue get(String name);

    default boolean known() { return true; }

    static DataReader server(EntityPlayer player) { return name -> SyncData.read(player, name); }

    static DataReader client() { return SyncMirror.reader(); }
}
