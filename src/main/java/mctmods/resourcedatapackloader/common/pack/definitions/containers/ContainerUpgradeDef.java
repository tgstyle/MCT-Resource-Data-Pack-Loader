package mctmods.resourcedatapackloader.common.pack.definitions.containers;

import java.util.Collections;
import java.util.List;

public final class ContainerUpgradeDef {
    public static final ContainerUpgradeDef PLAIN = new ContainerUpgradeDef(Collections.emptyList(), true, false);
    public final List<Path> paths;
    public final boolean consume;
    public final boolean carryLoot;

    public ContainerUpgradeDef(List<Path> paths, boolean consume, boolean carryLoot) {
        this.paths = paths;
        this.consume = consume;
        this.carryLoot = carryLoot;
    }

    public enum Contents { CONTAINER, SLOTS, NBT }

    public static final class Path {
        public final String from;
        public final String to;
        public final Contents contents;
        public final boolean linkedHalf;
        public final boolean fit;
        public final boolean strict;
        public final boolean carryState;

        public Path(String from, String to, Contents contents, boolean linkedHalf, boolean fit, boolean strict, boolean carryState) {
            this.from = from;
            this.to = to;
            this.contents = contents;
            this.linkedHalf = linkedHalf;
            this.fit = fit;
            this.strict = strict;
            this.carryState = carryState;
        }
    }
}
