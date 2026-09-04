package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record SaplingDef(List<String> soil, int stages, int chance, int light, String structure, String log, String leaves, int height, boolean vines) {
    public boolean usesStructure() { return !structure.isEmpty(); }
}
