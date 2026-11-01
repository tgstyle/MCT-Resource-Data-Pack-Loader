package mctmods.resourcedatapackloader.common.blocks.portal.logic;

import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.portways.ContentPortways;
import mctmods.resourcedatapackloader.common.portways.Portways;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentPortwayBlockEntity extends BlockEntity {
    private static final long SALT = 0x706F727477L;
    private static final String NAME = "Name";
    private static final String BIOME = "{biome}";
    private static final String GLOBAL = "Global";
    private static final String PLACER = "Placer";
    private static final String PLACED_AT = "PlacedAt";
    private String name = "";
    private boolean global;
    @Nullable private UUID placer;

    public ContentPortwayBlockEntity(BlockPos pos, BlockState state) { super(ContentPortways.TYPE.get(), pos, state); }

    public void rename(String named) {
        name = named;
        setChanged();
    }

    public boolean global() { return global; }

    public void setGlobal(boolean now) {
        global = now;
        setChanged();
    }

    public void placedBy(UUID who) {
        placer = who;
        setChanged();
    }

    public boolean mayToggle(Player player) { return player.hasPermissions(2) || placer != null && placer.equals(player.getUUID()); }

    public String stored() { return name; }

    public String title() {
        String picked = name.isEmpty() ? pick() : name;
        return level != null && picked.contains(BIOME) ? picked.replace(BIOME, BiomeNames.title(level.getBiome(worldPosition))) : picked;
    }

    private String pick() {
        if (!(getBlockState().getBlock() instanceof ContentPortwayBlock block)) { return ""; }
        List<String> names = block.settings().names();
        return names.isEmpty() ? "" : names.get(Math.floorMod(Hashes.mix(SALT, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), names.size()));
    }

    @Override public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel server) { Portways.publish(server, worldPosition); }
    }

    @Override public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        name = tag.getString(NAME);
        global = tag.getBoolean(GLOBAL);
        placer = tag.hasUUID(PLACER) && tag.getLong(PLACED_AT) == worldPosition.asLong() ? tag.getUUID(PLACER) : null;
    }

    @Override protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!name.isEmpty()) { tag.putString(NAME, name); }
        if (global) { tag.putBoolean(GLOBAL, true); }
        if (placer != null) {
            tag.putUUID(PLACER, placer);
            tag.putLong(PLACED_AT, worldPosition.asLong());
        }
    }
}
