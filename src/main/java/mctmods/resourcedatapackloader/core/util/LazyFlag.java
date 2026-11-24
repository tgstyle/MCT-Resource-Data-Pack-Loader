package mctmods.resourcedatapackloader.core.util;

import java.util.function.BooleanSupplier;

public final class LazyFlag {
    private final BooleanSupplier probe;
    private Boolean value;

    public LazyFlag(BooleanSupplier probe) { this.probe = probe; }

    public boolean get() {
        if (value == null) { value = probe.getAsBoolean(); }
        return value;
    }
}
