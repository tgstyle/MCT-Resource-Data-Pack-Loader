package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record ContainerUpgradeDef(List<Path> paths, boolean consume, boolean carryLoot) {
    public static final ContainerUpgradeDef PLAIN = new ContainerUpgradeDef(List.of(), true, false);

    public record Path(String from, String to) {}
}
