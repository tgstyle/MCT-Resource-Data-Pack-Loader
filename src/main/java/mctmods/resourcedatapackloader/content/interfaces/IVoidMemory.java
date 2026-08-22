package mctmods.resourcedatapackloader.content.interfaces;

public interface IVoidMemory {
    boolean rdpl$voidRecorded();

    boolean rdpl$voidAppliesTo(int dimension);

    void rdpl$recordVoid(boolean enabled, int[] dimensions, boolean areBlacklist);
}
