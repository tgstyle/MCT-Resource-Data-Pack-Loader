package mctmods.resourcedatapackloader.common.util.worldgen;

public interface IPieceLaid {
    void rdpl$laying(boolean laying);

    boolean rdpl$laid(int x, int y, int z);
}
