package mctmods.resourcedatapackloader.common.worldgen.structure.city;

public interface IVillageBlock {
    static int rdpl$blockOf(Object piece) { return piece instanceof IVillageBlock ? ((IVillageBlock) piece).rdpl$block() : 0; }

    int rdpl$block();

    void rdpl$block(int size);

    default void rdpl$inherit(Object start) { rdpl$block(rdpl$blockOf(start)); }
}
