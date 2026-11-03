package mctmods.resourcedatapackloader.common.pack.definitions.containers;

import java.util.Collections;
import java.util.List;

public record ContainerUpgradeDef(List<Path> paths, boolean consume, boolean carryLoot) {
    public static final ContainerUpgradeDef PLAIN = new ContainerUpgradeDef(Collections.emptyList(), true, false);

    public enum Contents { CONTAINER, SLOTS, NBT }

    public record Path(String from, String to, Contents contents, boolean linkedHalf, boolean fit, boolean strict, boolean carryState) {}
}
