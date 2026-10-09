package mctmods.resourcedatapackloader.util;

import javax.annotation.Nullable;

public final class Held<T> {
    private final String what;
    @Nullable private T value;

    public Held(String what) { this.what = what; }

    public T get() {
        if (value == null) { throw new IllegalStateException("No " + what + " is registered"); }
        return value;
    }

    @Nullable public T registeredType() { return value; }

    public void set(T held) { value = held; }
}
