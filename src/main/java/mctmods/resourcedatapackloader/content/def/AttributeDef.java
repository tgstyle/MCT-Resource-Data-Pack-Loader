package mctmods.resourcedatapackloader.content.def;


public final class AttributeDef {
    public final String attribute;
    public final String uuid;
    public final double amount;
    public final int operation;

    public AttributeDef(String attribute, String uuid, double amount, int operation) {
        this.attribute = attribute;
        this.uuid = uuid;
        this.amount = amount;
        this.operation = operation;
    }
}
