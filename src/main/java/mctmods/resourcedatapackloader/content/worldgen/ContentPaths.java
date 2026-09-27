package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import javax.annotation.Nullable;

public final class ContentPaths {
    public static final String PATH = "path";
    public static final String TILL = "till";

    private ContentPaths() {}

    public static boolean enabled() { return Config.content.shovelPaths() || Config.content.hoeTilling(); }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }
        Direction face = event.getFace();
        if (face == null || face == Direction.DOWN) { return; }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        Block block = level.getBlockState(pos).getBlock();
        if (LineCompat.flattens(held)) {
            if (!Config.content.shovelPaths()) { return; }
            if (player.isShiftKeyDown() && block == Blocks.DIRT_PATH) {
                apply(event, level, pos, face, player, held, named(Config.content.shovelPathReverts(), Blocks.DIRT), LineCompat.flattenSound());
                return;
            }
            if (ContentRegistry.lacks(PATH, block) || blocked(level, pos)) { return; }
            apply(event, level, pos, face, player, held, named(Config.content.shovelPathBecomes(), Blocks.DIRT_PATH), LineCompat.flattenSound());
            return;
        }
        if (LineCompat.tills(held) && Config.content.hoeTilling() && !ContentRegistry.lacks(TILL, block) && !blocked(level, pos)) { apply(event, level, pos, face, player, held, named(Config.content.hoeTillsInto(), Blocks.FARMLAND), LineCompat.tillSound()); }
    }

    private static boolean blocked(Level level, BlockPos pos) { return !Config.tweaks.lenientPaths() && !level.isEmptyBlock(pos.above()); }

    private static void apply(PlayerInteractEvent.RightClickBlock event, Level level, BlockPos pos, Direction face, Player player, ItemStack held, @Nullable Block result, SoundEvent sound) {
        if (result == null) { return; }
        if (!player.mayUseItemAt(pos, face, held)) { return; }
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            level.setBlock(pos, Block.updateFromNeighbourShapes(result.defaultBlockState(), level, pos), Block.UPDATE_ALL_IMMEDIATE);
            held.hurtAndBreak(1, player, event.getHand());
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @Nullable private static Block named(String name, Block fallback) {
        if (name.isEmpty()) { return fallback; }
        Identifier key = Identifier.tryParse(name);
        Block block = Registered.find(BuiltInRegistries.BLOCK, key);
        return block == null ? fallback : block;
    }
}
