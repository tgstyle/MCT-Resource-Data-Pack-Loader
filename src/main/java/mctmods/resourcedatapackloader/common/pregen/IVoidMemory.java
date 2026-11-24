package mctmods.resourcedatapackloader.common.pregen;

public interface IVoidMemory {
    String VOID_ENABLED = "enabled";
    String VOID_DIMENSIONS = "dimensions";
    String VOID_ARE_BLACKLIST = "areBlacklist";

    boolean rdpl$voidRecorded();

    boolean rdpl$voidAppliesTo(int dimension);

    void rdpl$recordVoid(boolean enabled, int[] dimensions, boolean areBlacklist);
}
