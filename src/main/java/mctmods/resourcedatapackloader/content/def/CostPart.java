package mctmods.resourcedatapackloader.content.def;

public final class CostPart {
    public static final CostPart FREE = new CostPart("", 0, 0, 0, 0, false);
    public final String id;
    public final int amount;
    public final int perBlocks;
    public final int dimension;
    public final int max;
    public final boolean points;

    public CostPart(String id, int amount, int perBlocks, int dimension, int max, boolean points) {
        this.id = id;
        this.amount = amount;
        this.perBlocks = perBlocks;
        this.dimension = dimension;
        this.max = max;
        this.points = points;
    }
}
