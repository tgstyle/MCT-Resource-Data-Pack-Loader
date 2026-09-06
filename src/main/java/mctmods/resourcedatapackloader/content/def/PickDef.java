package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.RandomSource;
import java.util.List;
import javax.annotation.Nullable;

public record PickDef(String name, int weight) {
    @Nullable public static String pick(List<PickDef> choices, RandomSource random) {
        if (choices.isEmpty()) { return null; }
        int total = 0;
        for (PickDef choice : choices) { total += Math.max(1, choice.weight()); }
        int roll = random.nextInt(total);
        for (PickDef choice : choices) {
            roll -= Math.max(1, choice.weight());
            if (roll < 0) { return choice.name(); }
        }
        return choices.get(0).name();
    }
}
