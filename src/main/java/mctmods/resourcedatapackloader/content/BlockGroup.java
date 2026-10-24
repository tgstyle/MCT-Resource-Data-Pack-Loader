package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class BlockGroup {
    private final Set<Block> whole = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<BlockState> exact = Collections.newSetFromMap(new IdentityHashMap<>());

    public int add(BlockMatchDef match, Object owner, String part) { return ContentStates.bind(match, owner, part, whole::add, exact::add); }

    public boolean isEmpty() { return whole.isEmpty() && exact.isEmpty(); }

    public boolean contains(BlockState state) { return whole.contains(state.getBlock()) || exact.contains(state); }
}
