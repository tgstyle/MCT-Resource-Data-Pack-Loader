package mctmods.resourcedatapackloader.content.def;

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

    @Override @Nonnull public String toString() { return fixed() ? String.valueOf(least) : least + ".." + most; }
}
