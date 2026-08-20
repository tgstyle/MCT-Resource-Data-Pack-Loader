package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Random;

public final class PickDef {
    public final String name;
    public final int weight;

    public PickDef(String name, int weight) {
        this.name = name;
        this.weight = Math.max(1, weight);
    }

    public static String pick(List<PickDef> choices, Random random, String fallback) {
        if (choices == null || choices.isEmpty()) { return fallback; }
        int total = 0;
        for (PickDef choice : choices) { total += choice.weight; }
        int roll = random.nextInt(total);
        for (PickDef choice : choices) {
            roll -= choice.weight;
            if (roll < 0) { return choice.name; }
        }
        return choices.get(0).name;
    }
}
