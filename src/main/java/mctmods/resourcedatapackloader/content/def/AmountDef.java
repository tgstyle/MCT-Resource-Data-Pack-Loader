package mctmods.resourcedatapackloader.content.def;

import java.util.Random;

public final class AmountDef {
    public final int least;
    public final int most;

    public AmountDef(int least, int most) {
        this.least = Math.min(least, most);
        this.most = Math.max(least, most);
    }

    public static AmountDef of(int value) { return new AmountDef(value, value); }

    public int pick(Random random) { return least >= most ? least : least + random.nextInt(most - least + 1); }

    @Override public String toString() { return least == most ? String.valueOf(least) : least + ".." + most; }
}
