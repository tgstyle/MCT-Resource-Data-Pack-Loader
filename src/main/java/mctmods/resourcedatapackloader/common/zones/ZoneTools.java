package mctmods.resourcedatapackloader.common.zones;

import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneDef;
import mctmods.resourcedatapackloader.common.pack.definitions.zones.ZoneTool;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public final class ZoneTools {
    private ZoneTools() {}

    @Nullable static ZoneDef denying(ServerPlayer player, BlockPos pos, ZoneTool tool) { return ZoneRules.none(player.level()) ? null : ZoneRules.covering(player, pos, held -> held.denyTools().contains(tool)); }
}
