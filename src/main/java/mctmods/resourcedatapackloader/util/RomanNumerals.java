package mctmods.resourcedatapackloader.util;

import java.util.TreeMap;

public final class RomanNumerals {
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();

    private RomanNumerals() {}

    static {
        NUMERALS.put(1000, "M");
        NUMERALS.put(900, "CM");
        NUMERALS.put(500, "D");
        NUMERALS.put(400, "CD");
        NUMERALS.put(100, "C");
        NUMERALS.put(90, "XC");
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");
        NUMERALS.put(0, "");
    }

    public static String of(int number) {
        if (number < 0) { return ""; }
        int key = NUMERALS.floorKey(number);
        if (number == key) { return NUMERALS.get(number); }
        return NUMERALS.get(key) + of(number - key);
    }
}
