package mctmods.resourcedatapackloader.content.tile;

import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.BiomeNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import javax.annotation.Nonnull;

public class TilePortway extends TileEntity {
    private static final long SALT = 0x504F52545741594CL;
    private static final String NAME = "Name";
    private static final String BIOME = "{biome}";
    private String name = "";

    public void rename(String now) {
        name = now;
        markDirty();
    }

    public String named() { return name; }

    public String label(List<String> names) {
        if (!name.isEmpty()) { return name; }
        if (names.isEmpty()) { return ""; }
        String picked = names.get((int) Math.floorMod(MathUtil.mix(SALT, pos.getX(), pos.getY(), pos.getZ()), names.size()));
        return picked.contains(BIOME) ? picked.replace(BIOME, BiomeNames.title(world.getBiome(pos))) : picked;
    }

    @Override public void onLoad() {
        if (!world.isRemote) { Portways.register(world, pos); }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        super.readFromNBT(tag);
        name = tag.getString(NAME);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString(NAME, name);
        return tag;
    }

    @Override public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState was, @Nonnull IBlockState is) { return was.getBlock() != is.getBlock(); }
}
