package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;

public final class ZoneListDef<T> {
    @Nullable private final Predicate<T> allow;
    @Nullable private final Predicate<T> deny;
    private final boolean denyAll;
    @Nullable private final Predicate<BlockState> on;
    public final boolean close;

    public ZoneListDef(@Nullable Predicate<T> allow, @Nullable Predicate<T> deny, boolean denyAll, @Nullable Predicate<BlockState> on, boolean close) {
        this.close = close;
        this.allow = allow;
        this.deny = deny;
        this.denyAll = denyAll;
        this.on = on;
    }

    public boolean empty() { return allow == null && deny == null && !denyAll && on == null; }

    public boolean refuses(T value) { return denyAll || (allow != null && !allow.test(value)) || (deny != null && deny.test(value)); }

    public boolean sitsOff(BlockState below) { return on != null && !on.test(below); }
}
