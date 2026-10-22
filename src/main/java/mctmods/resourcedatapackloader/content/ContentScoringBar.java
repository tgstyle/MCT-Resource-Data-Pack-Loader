package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.BossBars;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.scores.Objective;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ContentScoringBar {
    private static final Map<String, ServerBossEvent> BARS = new HashMap<>();

    private ContentScoringBar() {}

    static void second(MinecraftServer server, Collection<ScoreDef> defs, Set<String> finished, int ticks) {
        for (ScoreDef def : defs) {
            if (def.bossBar() == null || finished.contains(def.name())) { continue; }
            ServerBossEvent bar = BARS.computeIfAbsent(def.name(), _ ->BossBars.create(def.bossBar(), Component.literal(def.displayName())));
            BossBars.progress(bar, progress(server, def, ticks));
            BossBars.sync(bar, server.getPlayerList().getPlayers());
        }
    }

    static void over(ScoreDef def) {
        ServerBossEvent bar = BARS.remove(def.name());
        if (bar != null) { BossBars.clear(bar); }
    }

    static void clear() {
        for (ServerBossEvent bar : BARS.values()) { BossBars.clear(bar); }
        BARS.clear();
    }

    private static float progress(MinecraftServer server, ScoreDef def, int ticks) {
        if (def.endsAfterMinutes() > 0) { return 1.0F - ticks / (float) (def.endsAfterMinutes() * ContentScoring.MINUTE_TICKS); }
        if (def.endsAtScore() <= 0) { return 1.0F; }
        Objective objective = Scores.objective(Scores.board(server), def.name());
        int best = 0;
        if (objective != null) {
            for (Scores.Row row : Scores.rows(Scores.board(server), objective)) { best = Math.max(best, row.value()); }
        }
        return best / (float) def.endsAtScore();
    }
}
