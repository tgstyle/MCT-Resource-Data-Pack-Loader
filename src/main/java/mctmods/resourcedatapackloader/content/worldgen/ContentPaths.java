package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
        if (held.canPerformAction(ToolActions.SHOVEL_FLATTEN)) {
            if (!Config.content.shovelPaths()) { return; }
            if (player.isShiftKeyDown() && block == Blocks.DIRT_PATH) {
                apply(event, level, pos, face, player, held, named(Config.content.shovelPathReverts(), Blocks.DIRT), SoundEvents.SHOVEL_FLATTEN);
                return;
            }
            if (ContentRegistry.lacks(PATH, block) || blocked(level, pos)) { return; }
            apply(event, level, pos, face, player, held, named(Config.content.shovelPathBecomes(), Blocks.DIRT_PATH), SoundEvents.SHOVEL_FLATTEN);
            return;
        }
        if (!held.canPerformAction(ToolActions.HOE_TILL) || !Config.content.hoeTilling()) { return; }
        if (ContentRegistry.lacks(TILL, block) || blocked(level, pos)) { return; }
        apply(event, level, pos, face, player, held, named(Config.content.hoeTillsInto(), Blocks.FARMLAND), SoundEvents.HOE_TILL);
    }

    private static boolean blocked(Level level, BlockPos pos) { return !Config.tweaks.lenientPaths() && !level.isEmptyBlock(pos.above()); }

    private static void apply(PlayerInteractEvent.RightClickBlock event, Level level, BlockPos pos, Direction face, Player player, ItemStack held, @Nullable Block result, SoundEvent sound) {
        if (result == null) { return; }
        if (!player.mayUseItemAt(pos, face, held)) { return; }
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            level.setBlock(pos, result.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            held.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(event.getHand()));
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @Nullable private static Block named(String name, Block fallback) {
        if (name.isEmpty()) { return fallback; }
        ResourceLocation key = ResourceLocation.tryParse(name);
        Block block = Registered.find(ForgeRegistries.BLOCKS, key);
        return block == null ? fallback : block;
    }
}
