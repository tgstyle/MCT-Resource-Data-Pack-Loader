package mctmods.resourcedatapackloader.content.def;

public record CostPart(int amount, int perBlocks, int dimension, int max) {
    public static final CostPart NONE = new CostPart(0, 0, 0, 0);

    public int of(double distance, boolean crossDimension) {
        int total = amount + (perBlocks > 0 ? (int) Math.ceil(distance / perBlocks) : 0) + (crossDimension ? dimension : 0);
        return max > 0 ? Math.min(total, max) : total;
    }
}
