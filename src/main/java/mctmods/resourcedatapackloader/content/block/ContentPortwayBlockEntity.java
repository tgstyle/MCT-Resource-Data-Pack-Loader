package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentPortways;
import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public boolean mayToggle(Player player) { return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) || placer != null && placer.equals(player.getUUID()); }

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

    @Override public void preRemoveSideEffects(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level instanceof ServerLevel server) { Portways.withdraw(server, pos); }
    }

    @Override protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        name = input.getStringOr(NAME, "");
        global = input.getBooleanOr(GLOBAL, false);
        placer = input.getLongOr(PLACED_AT, 0L) == worldPosition.asLong() ? input.read(PLACER, UUIDUtil.CODEC).orElse(null) : null;
    }

    @Override protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        if (!name.isEmpty()) { output.putString(NAME, name); }
        if (global) { output.putBoolean(GLOBAL, true); }
        if (placer != null) {
            output.store(PLACER, UUIDUtil.CODEC, placer);
            output.putLong(PLACED_AT, worldPosition.asLong());
        }
    }
}
