package mctmods.resourcedatapackloader.content.def;

public record CarryDef(boolean mount, boolean leashed, boolean pets, boolean crossDimension) {
    public static final CarryDef NONE = new CarryDef(false, false, false, true);
}
