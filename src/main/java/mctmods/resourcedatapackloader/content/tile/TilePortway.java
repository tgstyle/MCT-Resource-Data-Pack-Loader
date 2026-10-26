package mctmods.resourcedatapackloader.content.tile;

import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.BiomeNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TilePortway extends TileEntity {
    private static final long SALT = 0x504F52545741594CL;
    private static final String NAME = "Name";
    private static final String BIOME = "{biome}";
    private static final String GLOBAL = "Global";
    private static final String PLACER = "Placer";
    private static final String PLACED_AT = "PlacedAt";
    private String name = "";
    private boolean global;
    @Nullable private UUID placer;

    public void rename(String now) {
        name = now;
        markDirty();
    }

    public boolean global() { return global; }

    public void setGlobal(boolean now) {
        global = now;
        markDirty();
    }

    public void placedBy(UUID who) {
        placer = who;
        markDirty();
    }

    public boolean mayToggle(EntityPlayer player) { return player.canUseCommand(2, "") || placer != null && placer.equals(player.getUniqueID()); }

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
        global = tag.getBoolean(GLOBAL);
        placer = tag.hasUniqueId(PLACER) && tag.getLong(PLACED_AT) == pos.toLong() ? tag.getUniqueId(PLACER) : null;
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString(NAME, name);
        if (global) { tag.setBoolean(GLOBAL, true); }
        if (placer != null) {
            tag.setUniqueId(PLACER, placer);
            tag.setLong(PLACED_AT, pos.toLong());
        }
        return tag;
    }

    @Override public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState was, @Nonnull IBlockState is) { return was.getBlock() != is.getBlock(); }
}
