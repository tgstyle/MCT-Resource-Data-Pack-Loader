package mctmods.resourcedatapackloader.content.def;


public final class SpawnEntryDef {
    public final String creatureType;
    public final String entity;
    public final int weight;
    public final int min;
    public final int max;

    public SpawnEntryDef(String creatureType, String entity, int weight, int min, int max) {
        this.creatureType = creatureType;
        this.entity = entity;
        this.weight = weight;
        this.min = min;
        this.max = max;
    }
}
