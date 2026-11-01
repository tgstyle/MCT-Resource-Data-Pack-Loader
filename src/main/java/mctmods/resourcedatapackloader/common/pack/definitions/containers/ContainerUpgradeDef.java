package mctmods.resourcedatapackloader.common.pack.definitions.containers;

import java.util.Collections;
import java.util.List;

public record ContainerUpgradeDef(List<Path> paths, boolean consume, boolean carryLoot) {
    public static final ContainerUpgradeDef PLAIN = new ContainerUpgradeDef(Collections.emptyList(), true, false);

    public record Path(String from, String to) {}
}
