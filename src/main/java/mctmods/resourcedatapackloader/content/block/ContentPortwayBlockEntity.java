package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentPortways;
import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.List;
import javax.annotation.Nonnull;

public final class ContentPortwayBlockEntity extends BlockEntity {
    private static final long SALT = 0x706F727477L;
    private static final String NAME = "Name";
    private static final String BIOME = "{biome}";
    private String name = "";

    public ContentPortwayBlockEntity(BlockPos pos, BlockState state) { super(ContentPortways.TYPE.get(), pos, state); }

    public void rename(String named) {
        name = named;
        setChanged();
    }

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
        if (level instanceof ServerLevel server && getBlockState().getBlock() instanceof ContentPortwayBlock block && block.settings().global()) { Portways.publish(server, worldPosition); }
    }

    @Override public void preRemoveSideEffects(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level instanceof ServerLevel server) { Portways.withdraw(server, pos); }
    }

    @Override protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        name = input.getStringOr(NAME, "");
    }

    @Override protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        if (!name.isEmpty()) { output.putString(NAME, name); }
    }
}
