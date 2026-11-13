package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

public final class CaveTerrainDef {
    public final String fillBlock;
    public final int seaLevel;
    public final String seaFluid;

    public CaveTerrainDef(String fillBlock, int seaLevel, String seaFluid) {
        this.fillBlock = fillBlock;
        this.seaLevel = seaLevel;
        this.seaFluid = seaFluid;
    }
}
