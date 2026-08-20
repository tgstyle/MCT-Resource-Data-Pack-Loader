package mctmods.resourcedatapackloader.util;


public class IntRange {
    private final int min, max;

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static IntRange single(int i) { return new IntRange(i, i); }

    public static IntRange of(int a, int b) { return new IntRange(Math.min(a, b), Math.max(a, b)); }

    public int getMin() { return min; }

    public int getMax() { return max; }

    @Override public String toString() {
        return "IntRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        IntRange intRange = (IntRange) o;
        if (min != intRange.min) { return false; }
        return max == intRange.max;
    }

    @Override public int hashCode() {
        int result = min;
        result = 31 * result + max;
        return result;
    }
}
