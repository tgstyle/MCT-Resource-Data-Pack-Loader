package mctmods.resourcedatapackloader.content.def;

public final class ItemGiveDef {
    public final String item;
    public final int count;
    public final boolean unbreakable;

    public ItemGiveDef(String item, int count, boolean unbreakable) {
        this.item = item;
        this.count = count;
        this.unbreakable = unbreakable;
    }
}
