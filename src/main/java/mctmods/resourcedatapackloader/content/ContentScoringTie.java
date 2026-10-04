package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentScoringTie {
    private ContentScoringTie() {}

    public static List<Scores.Row> top(Collection<Scores.Row> rows) {
        List<Scores.Row> top = new ArrayList<>();
        for (Scores.Row one : rows) {
            if (!top.isEmpty() && one.value() < top.getFirst().value()) { continue; }
            if (!top.isEmpty() && one.value() > top.getFirst().value()) { top.clear(); }
            top.add(one);
        }
        return top;
    }

    public static List<String> names(List<Scores.Row> rows) {
        List<String> names = new ArrayList<>();
        for (Scores.Row one : rows) { names.add(one.owner()); }
        return names;
    }

    @Nullable static Scores.Row best(MinecraftServer server, ScoreDef def, Collection<Scores.Row> rows) {
        List<Scores.Row> top = top(rows);
        if (top.size() < 2) { return top.isEmpty() ? null : top.getFirst(); }
        if (!def.tiebreak()) { return null; }
        Scores.Row drawn = top.get(server.overworld().getRandom().nextInt(top.size()));
        ContentLog.LOGGER.info("The {} round ended level at {} between {}, so the tiebreak drew {}", def.displayName(), drawn.value(), names(top), drawn.owner());
        return drawn;
    }
}
