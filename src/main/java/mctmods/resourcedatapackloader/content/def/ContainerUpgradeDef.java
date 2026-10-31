package mctmods.resourcedatapackloader.content.def;

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

    public static final class Path {
        public final String from;
        public final String to;

        public Path(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
