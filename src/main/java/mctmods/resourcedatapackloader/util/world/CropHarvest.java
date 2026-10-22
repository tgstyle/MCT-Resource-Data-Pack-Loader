package mctmods.resourcedatapackloader.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Collections;
import javax.annotation.Nullable;

public final class CropHarvest {
    private static final String AGE = "age";

    private CropHarvest() {}

    @Nullable private static IntegerProperty ageOf(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty integer && AGE.equals(property.getName())) { return integer; }
        }
        return null;
    }

    public static boolean ripe(BlockState state, int age) {
        IntegerProperty property = ageOf(state);
        if (property == null) { return false; }
        return state.getValue(property) >= (age >= 0 ? age : Collections.max(property.getPossibleValues()));
    }

    public static void harvest(ServerLevel level, BlockPos pos, BlockState state, Player player, boolean replant) {
        IntegerProperty property = ageOf(state);
        ItemStack seed = replant && property != null ? state.getCloneItemStack(level, pos, false) : ItemStack.EMPTY;
        boolean seedTaken = seed.isEmpty();
        for (ItemStack drop : Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, player.getMainHandItem())) {
            ItemStack kept = drop.copy();
            if (!seedTaken && ItemStack.isSameItem(kept, seed)) {
                kept.shrink(1);
                seedTaken = true;
            }
            if (!kept.isEmpty()) { Block.popResource(level, pos, kept); }
        }
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        if (replant && property != null) { level.setBlock(pos, state.setValue(property, Collections.min(property.getPossibleValues())), Block.UPDATE_ALL); }
        else { level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); }
    }
}
