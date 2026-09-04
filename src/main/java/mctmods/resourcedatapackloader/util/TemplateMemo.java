package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;

import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class TemplateMemo<T> {
    @Nullable private WorldTemplateDef from;
    private boolean read;
    @Nullable private T held;

    public T get(Supplier<T> make) {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (read && active == from) { return held; }
        held = make.get();
        from = active;
        read = true;
        return held;
    }

    public void forget() { read = false; }
}
