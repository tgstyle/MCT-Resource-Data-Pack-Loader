package mctmods.resourcedatapackloader.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

final class RegistryDumpCommand {
    static final String FILE = "rdpl-registry.txt";

    private RegistryDumpCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> dump(String name) {
        return Commands.literal("dumpregistry").requires(source -> CommandShared.allowed(source, CommandShared.OPERATOR)).executes(context -> {
            CommandSourceStack source = context.getSource();
            CommandShared.ran(source, name, "dumpregistry");
            return run(source);
        });
    }

    private static int run(CommandSourceStack source) {
        HolderLookup.Provider registries = source.getServer().registryAccess();
        Map<String, Block> blocks = new TreeMap<>();
        for (Identifier id : BuiltInRegistries.BLOCK.keySet()) { blocks.put(id.toString(), BuiltInRegistries.BLOCK.getValue(id)); }
        Map<String, Item> items = new TreeMap<>();
        for (Identifier id : BuiltInRegistries.ITEM.keySet()) { items.put(id.toString(), BuiltInRegistries.ITEM.getValue(id)); }
        List<String> lines = new ArrayList<>();
        lines.add("# blocks " + blocks.size());
        for (Map.Entry<String, Block> entry : blocks.entrySet()) { block(lines, registries, entry.getKey(), entry.getValue()); }
        lines.add("");
        lines.add("# items " + items.size());
        for (Map.Entry<String, Item> entry : items.entrySet()) { item(lines, entry.getKey(), entry.getValue()); }
        Path file = source.getServer().getServerDirectory().resolve(FILE).toAbsolutePath();
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
            CompoundTag tag = tile.saveWithFullMetadata(registries);
            lines.add("  tile nbt: " + tag);
        }
        catch (RuntimeException | LinkageError e) { lines.add("  tile nbt: unavailable (" + e.getClass().getSimpleName() + ")"); }
    }

    private static void item(List<String> lines, String id, Item item) {
        lines.add(id);
        String names;
        String components;
        try {
            ItemStack stack = item.getDefaultInstance();
            names = item.getDescriptionId() + " / " + stack.getHoverName().getString();
            components = String.valueOf(stack.getComponents());
        }
        catch (RuntimeException | LinkageError e) {
            names = "unavailable";
            components = null;
        }
        lines.add("  default: " + names);
        if (components != null) { lines.add("    components: " + components); }
    }
}
