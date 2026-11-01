package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public final class BlockGroup {
    private final Set<Block> whole = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<IBlockState> exact = Collections.newSetFromMap(new IdentityHashMap<>());

    public int add(BlockMatchDef match, Object owner, String part) { return ContentStates.bind(match, owner, part, whole::add, exact::add); }

    public boolean isEmpty() { return whole.isEmpty() && exact.isEmpty(); }

    public boolean contains(IBlockState state) { return whole.contains(state.getBlock()) || exact.contains(state); }
}
