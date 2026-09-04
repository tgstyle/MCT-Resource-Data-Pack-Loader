package mctmods.resourcedatapackloader.content.rubic.world.cube;

import mctmods.resourcedatapackloader.content.rubic.world.BlankEntityContainer;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.ticket.TicketList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;

public class BlankCube extends Cube {
    public BlankCube(Chunk column) {
        super(new TicketList(null), column.getWorld(), column, new CubePos(0, 0, 0), new BlankEntityContainer(), new HashMap<>(), new ConcurrentLinkedQueue<>());
    }

    @Override public boolean isEmpty() { return true; }

    @Override public IBlockState getBlockState(BlockPos pos) { return Blocks.AIR.getDefaultState(); }

    @Override public IBlockState getBlockState(int blockX, int localOrBlockY, int blockZ) { return Blocks.AIR.getDefaultState(); }

    @Nullable @Override public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationType) { return null; }

    @Override public void onLoad() {
    }

    @Override public void onUnload() {
    }

    @Override public boolean needsSaving() { return false; }

    @Override public void markSaved() {
    }

    @Override public int getLightFor(EnumSkyBlock lightType, BlockPos pos) { return lightType.defaultLightValue; }

    @Override public void setLightFor(EnumSkyBlock lightType, BlockPos pos, int light) {
    }

    @Override public void markForRenderUpdate() {
    }
}
