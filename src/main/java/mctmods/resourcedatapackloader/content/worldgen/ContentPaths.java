package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public final class ContentPaths {
    private ContentPaths() {}

    public static boolean enabled() { return Config.content.shovelPaths || Config.content.hoeTilling; }

    @SubscribeEvent public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }

        EnumFacing face = event.getFace();
        if (face == null || face == EnumFacing.DOWN) { return; }

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getEntityPlayer();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (shovel(held)) {
            if (!Config.content.shovelPaths) { return; }
            if (player.isSneaking() && block == Blocks.GRASS_PATH) {
                apply(event, world, pos, face, player, held, named(Config.content.shovelPathReverts, Blocks.DIRT), SoundEvents.ITEM_SHOVEL_FLATTEN);
                return;
            }
            if (!ContentSpawning.does("path", block) || blocked(world, pos)) { return; }

            apply(event, world, pos, face, player, held, named(Config.content.shovelPathBecomes, Blocks.GRASS_PATH), SoundEvents.ITEM_SHOVEL_FLATTEN);
            return;
        }

        if (!hoe(held) || !Config.content.hoeTilling) { return; }
        if (!ContentSpawning.does("till", block) || blocked(world, pos)) { return; }

        apply(event, world, pos, face, player, held, named(Config.content.hoeTillsInto, Blocks.FARMLAND), SoundEvents.ITEM_HOE_TILL);
    }

    private static boolean blocked(World world, BlockPos pos) { return !Config.tweaks.lenientPaths && !world.isAirBlock(pos.up()); }

    private static boolean shovel(ItemStack held) { return held.getItem() instanceof ItemSpade || held.getItem().getToolClasses(held).contains("shovel"); }

    private static boolean hoe(ItemStack held) { return held.getItem() instanceof ItemHoe || held.getItem().getToolClasses(held).contains("hoe"); }

    private static void apply(PlayerInteractEvent.RightClickBlock event, World world, BlockPos pos, EnumFacing face, EntityPlayer player, ItemStack held, @Nullable Block result, SoundEvent sound) {
        if (result == null) { return; }
        if (!player.canPlayerEdit(pos, face, held)) { return; }

        world.playSound(player, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!world.isRemote) {
            world.setBlockState(pos, result.getDefaultState(), 11);
            held.damageItem(1, player);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    @Nullable private static Block named(String name, @Nullable Block fallback) {
        if (name.isEmpty()) { return fallback; }

        ResourceLocation key = new ResourceLocation(name);
        return ForgeRegistries.BLOCKS.containsKey(key) ? ForgeRegistries.BLOCKS.getValue(key) : fallback;
    }
}
