package mctmods.resourcedatapackloader.common.command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

final class RegistryDumpCommand {
    static final String FILE = "rdpl-registry.txt";

    private RegistryDumpCommand() {}

    static int run(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        Map<String, Block> blocks = new TreeMap<>();
        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) { blocks.put(entry.getKey().location().toString(), entry.getValue()); }
        Map<String, Item> items = new TreeMap<>();
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) { items.put(entry.getKey().location().toString(), entry.getValue()); }
        List<String> lines = new ArrayList<>();
        lines.add("# blocks " + blocks.size());
        for (Map.Entry<String, Block> entry : blocks.entrySet()) { block(lines, server.registryAccess(), entry.getKey(), entry.getValue()); }
        lines.add("");
        lines.add("# items " + items.size());
        for (Map.Entry<String, Item> entry : items.entrySet()) { item(lines, entry.getKey(), entry.getValue()); }
        Path file = server.getServerDirectory().resolve(FILE).toAbsolutePath();
        try { Files.write(file, lines, StandardCharsets.UTF_8); }
        catch (IOException e) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.dumpfailed", file.toString(), String.valueOf(e.getMessage()))); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.dumpregistry", blocks.size(), items.size(), file.toString()));
        return 1;
    }

    private static void block(List<String> lines, HolderLookup.Provider registries, String id, Block block) {
        lines.add(id);
        for (BlockState state : block.getStateDefinition().getPossibleStates()) { lines.add("  state: " + state); }
        if (!(block instanceof EntityBlock entityBlock)) { return; }
        try {
            BlockEntity tile = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
            if (tile == null) { return; }
            CompoundTag tag = tile.saveWithoutMetadata(registries);
            lines.add("  tile nbt: " + tag);
        }
        catch (RuntimeException | LinkageError e) { lines.add("  tile nbt: unavailable (" + e.getClass().getSimpleName() + ")"); }
    }

    private static void item(List<String> lines, String id, Item item) {
        lines.add(id);
        ItemStack stack;
        try { stack = item.getDefaultInstance(); }
        catch (RuntimeException | LinkageError e) {
            lines.add("  default: unavailable");
            return;
        }
        String names;
        try { names = item.getDescriptionId(stack) + " / " + stack.getHoverName().getString(); }
        catch (RuntimeException | LinkageError e) { names = "unavailable"; }
        lines.add("  default: " + names);
        try { lines.add("    components: " + (stack.getComponentsPatch().isEmpty() ? item.components() : stack.getComponentsPatch())); }
        catch (RuntimeException | LinkageError e) { lines.add("    components: unavailable"); }
    }
}
