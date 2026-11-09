package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import javax.annotation.Nullable;

public enum SkyFlip {
    NONE, HORIZONTAL, VERTICAL, BOTH;

    public boolean horizontal() { return this == HORIZONTAL || this == BOTH; }

    public boolean vertical() { return this == VERTICAL || this == BOTH; }

    public SkyFlip inverse() { return values()[BOTH.ordinal() - ordinal()]; }

    @Nullable public static SkyFlip parse(String value) {
        for (SkyFlip flip : values()) {
            if (flip != NONE && flip.name().equalsIgnoreCase(value)) { return flip; }
        }
        return null;
    }
}
