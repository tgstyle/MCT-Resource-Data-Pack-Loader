package mctmods.resourcedatapackloader.common.command;

import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

final class RegistryDumpCommand {
    static final String FILE = "rdpl-registry.txt";

    private RegistryDumpCommand() {}

    static void run(MinecraftServer server, ICommandSender sender) throws CommandException {
        World world = server.getEntityWorld();
        Map<String, Block> blocks = new TreeMap<>();
        for (Map.Entry<ResourceLocation, Block> entry : ForgeRegistries.BLOCKS.getEntries()) { blocks.put(entry.getKey().toString(), entry.getValue()); }
        Map<String, Item> items = new TreeMap<>();
        for (Map.Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries()) { items.put(entry.getKey().toString(), entry.getValue()); }
        List<String> lines = new ArrayList<>();
        lines.add("# blocks " + blocks.size());
        for (Map.Entry<String, Block> entry : blocks.entrySet()) { block(lines, world, entry.getKey(), entry.getValue()); }
        lines.add("");
        lines.add("# items " + items.size());
        for (Map.Entry<String, Item> entry : items.entrySet()) { item(lines, entry.getKey(), entry.getValue()); }
        File file = new File(server.getDataDirectory(), FILE);
        try { Files.write(file.toPath(), lines, StandardCharsets.UTF_8); }
        catch (IOException e) { throw new CommandException(Lang.tr(sender, "rdpl.command.dumpfailed", file.getAbsolutePath(), String.valueOf(e.getMessage()))); }
        CommandShared.send(sender, TextFormatting.GREEN, Lang.tr(sender, "rdpl.command.dumpregistry", blocks.size(), items.size(), file.getAbsolutePath()));
    }

    private static void block(List<String> lines, World world, String id, Block block) {
        lines.add(id);
        Map<Integer, List<String>> byMeta = new TreeMap<>();
        for (IBlockState state : block.getBlockState().getValidStates()) {
            byMeta.computeIfAbsent(metaOf(block, state), key -> new ArrayList<>()).add(state.toString());
        }
        for (Map.Entry<Integer, List<String>> entry : byMeta.entrySet()) { lines.add("  meta " + entry.getKey() + ": " + String.join(" | ", entry.getValue())); }
        IBlockState base = block.getDefaultState();
        if (!block.hasTileEntity(base)) { return; }
        try {
            TileEntity tile = block.createTileEntity(world, base);
            if (tile == null) { return; }
            NBTTagCompound tag = tile.writeToNBT(new NBTTagCompound());
            lines.add("  tile nbt: " + tag);
        }
        catch (RuntimeException | LinkageError e) { lines.add("  tile nbt: unavailable (" + e.getClass().getSimpleName() + ")"); }
    }

    private static int metaOf(Block block, IBlockState state) {
        try { return block.getMetaFromState(state); }
        catch (RuntimeException e) { return -1; }
    }

    private static void item(List<String> lines, String id, Item item) {
        lines.add(id);
        NonNullList<ItemStack> stacks = NonNullList.create();
        try { item.getSubItems(CreativeTabs.SEARCH, stacks); }
        catch (RuntimeException | LinkageError e) { stacks.clear(); }
        if (stacks.isEmpty()) { stacks.add(new ItemStack(item)); }
        for (ItemStack stack : stacks) { stack(lines, item, stack); }
    }

    private static void stack(List<String> lines, Item item, ItemStack stack) {
        String names;
        try { names = item.getTranslationKey(stack) + " / " + stack.getDisplayName(); }
        catch (RuntimeException | LinkageError e) { names = "unavailable"; }
        lines.add("  meta " + stack.getMetadata() + ": " + names);
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) { lines.add("    nbt: " + tag); }
        NBTTagCompound shared = item.getNBTShareTag(stack);
        if (shared != null && !Objects.equals(shared, tag)) { lines.add("    share nbt: " + shared); }
    }
}
