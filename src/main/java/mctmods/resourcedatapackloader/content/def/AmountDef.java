package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.RandomSource;
import javax.annotation.Nonnull;

public record AmountDef(int least, int most) {
    public AmountDef {
        int low = Math.min(least, most);
        int high = Math.max(least, most);
        least = low;
        most = high;
    }

    public static AmountDef of(int value) { return new AmountDef(value, value); }

    public boolean fixed() { return least == most; }

    public int pick(RandomSource random) { return least >= most ? least : least + random.nextInt(most - least + 1); }

    @Override @Nonnull public String toString() { return fixed() ? String.valueOf(least) : least + ".." + most; }
}
