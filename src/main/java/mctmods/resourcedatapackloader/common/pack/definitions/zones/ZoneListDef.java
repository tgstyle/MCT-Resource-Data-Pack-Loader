package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class ZoneListDef {
    @Nullable private final ZoneNames allow;
    @Nullable private final ZoneNames deny;
    private final boolean denyAll;
    @Nullable private final ZoneNames on;
    public final boolean close;

    public ZoneListDef(@Nullable ZoneNames allow, @Nullable ZoneNames deny, boolean denyAll, @Nullable ZoneNames on, boolean close) {
        this.close = close;
        this.allow = allow;
        this.deny = deny;
        this.denyAll = denyAll;
        this.on = on;
    }

    public boolean empty() { return allow == null && deny == null && !denyAll && on == null; }

    public boolean refusesBlock(BlockState state) { return refuses(names -> names.hasBlock(state)); }

    public boolean refusesItem(ItemStack stack) { return refuses(names -> names.hasItem(stack)); }

    public boolean sitsOff(BlockState below) { return on != null && !on.hasBlock(below); }

    private boolean refuses(Predicate<ZoneNames> listed) { return denyAll || (allow != null && !listed.test(allow)) || (deny != null && listed.test(deny)); }
}
