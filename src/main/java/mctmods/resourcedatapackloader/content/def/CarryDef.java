package mctmods.resourcedatapackloader.content.def;

public final class CarryDef {
    public static final CarryDef NONE = new CarryDef(false, false, false, true);
    public final boolean mount;
    public final boolean leashed;
    public final boolean pets;
    public final boolean crossDimension;

    public CarryDef(boolean mount, boolean leashed, boolean pets, boolean crossDimension) {
        this.mount = mount;
        this.leashed = leashed;
        this.pets = pets;
        this.crossDimension = crossDimension;
    }
}
