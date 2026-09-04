package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;

import java.util.function.ToIntFunction;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class WeightedPicks {
    public static final String EMPTY = "empty";

    public static final class Pick {
        public final String name;
        public final String tail;
        Pick(String name, String tail) {
            this.name = name;
            this.tail = tail;
        }
    }

    private final String setting;
    private final List<String> names = new ArrayList<>();
    private final List<String> tails = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    @Nullable private WorldTemplateDef listedFrom;
    private boolean listed;
    private int total;

    public WeightedPicks(String setting) { this.setting = setting; }

    public boolean stale() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (listed && active == listedFrom) { return false; }
        listedFrom = active;
        listed = true;
        return true;
    }

    public boolean isEmpty() { return names.isEmpty(); }

    public int size() { return names.size(); }

    public List<String> names() { return names; }

    public void load(String[] entries) {
        names.clear();
        tails.clear();
        weights.clear();
        total = 0;
        for (String entry : entries) {
            String[] parts = Settings.pair(entry, setting, "name=weight");
            if (parts == null) { continue; }
            String said = parts[1];
            String tail = "";
            int split = said.indexOf(',');
            if (split >= 0) {
                tail = said.substring(split + 1).trim();
                said = said.substring(0, split).trim();
            }
            int weight = weightOf(said, entry);
            if (weight < 1) { continue; }
            names.add(parts[0]);
            tails.add(tail);
            weights.add(weight);
            total += weight;
        }
    }

    @Nullable public static <T> T pick(Collection<T> choices, ToIntFunction<T> weight, Random random) {
        int total = 0;
        for (T choice : choices) { total += Math.max(0, weight.applyAsInt(choice)); }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (T choice : choices) {
            roll -= Math.max(0, weight.applyAsInt(choice));
            if (roll < 0) { return choice; }
        }
        return null;
    }

    @Nullable public Pick pick(Random random) {
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (int i = 0; i < names.size(); i++) {
            roll -= weights.get(i);
            if (roll < 0) { return new Pick(names.get(i), tails.get(i)); }
        }
        return null;
    }

    private int weightOf(String said, String entry) {
        int asked;
        try { asked = Integer.parseInt(said); }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("{} entry '{}' gives a weight of '{}', which is not a whole number, ignoring the entry", setting, entry, said);
            return -1;
        }
        if (asked >= 1) { return asked; }
        ContentLog.LOGGER.error("{} entry '{}' asks for a weight of {}, which is below 1, ignoring the entry", setting, entry, asked);
        return -1;
    }
}
