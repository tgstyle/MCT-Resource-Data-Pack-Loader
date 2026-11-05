package mctmods.resourcedatapackloader.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

final class RegistryDumpCommand {
    static final String FILE = "rdpl-registry.txt";

    private RegistryDumpCommand() {}

    static LiteralArgumentBuilder<CommandSourceStack> dump(String name) {
        return Commands.literal("dumpregistry").requires(source -> source.hasPermission(CommandShared.OPERATOR)).executes(context -> run(context, name));
    }

    private static int run(CommandContext<CommandSourceStack> context, String name) {
        CommandSourceStack source = context.getSource();
        CommandShared.ran(source, name, "dumpregistry");
        Map<String, Block> blocks = new TreeMap<>();
        for (Map.Entry<ResourceKey<Block>, Block> entry : ForgeRegistries.BLOCKS.getEntries()) { blocks.put(entry.getKey().location().toString(), entry.getValue()); }
        Map<String, Item> items = new TreeMap<>();
        for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) { items.put(entry.getKey().location().toString(), entry.getValue()); }
        List<String> lines = new ArrayList<>();
        lines.add("# blocks " + blocks.size());
        for (Map.Entry<String, Block> entry : blocks.entrySet()) { block(lines, entry.getKey(), entry.getValue()); }
        lines.add("");
        lines.add("# items " + items.size());
        for (Map.Entry<String, Item> entry : items.entrySet()) { item(lines, entry.getKey(), entry.getValue()); }
        File file = new File(source.getServer().getServerDirectory(), FILE);
        try { Files.write(file.toPath(), lines, StandardCharsets.UTF_8); }
        catch (IOException e) { return CommandShared.refuse(source, CommandShared.tr("rdpl.command.dumpfailed", file.getAbsolutePath(), String.valueOf(e.getMessage()))); }
        CommandShared.send(source, ChatFormatting.GREEN, CommandShared.tr("rdpl.command.dumpregistry", blocks.size(), items.size(), file.getAbsolutePath()));
        return 1;
    }

    private static void block(List<String> lines, String id, Block block) {
        lines.add(id);
        for (BlockState state : block.getStateDefinition().getPossibleStates()) { lines.add("  state: " + state); }
        BlockState base = block.defaultBlockState();
        if (!(block instanceof EntityBlock entityBlock) || !base.hasBlockEntity()) { return; }
        try {
            BlockEntity entity = entityBlock.newBlockEntity(BlockPos.ZERO, base);
            if (entity == null) { return; }
            lines.add("  tile nbt: " + entity.saveWithoutMetadata());
        }
        catch (RuntimeException | LinkageError e) { lines.add("  tile nbt: unavailable (" + e.getClass().getSimpleName() + ")"); }
    }

    private static void item(List<String> lines, String id, Item item) {
        lines.add(id);
        String names;
        ItemStack stack = ItemStack.EMPTY;
        try {
            stack = item.getDefaultInstance();
            names = item.getDescriptionId(stack) + " / " + stack.getHoverName().getString();
        }
        catch (RuntimeException | LinkageError e) { names = "unavailable"; }
        lines.add("  default: " + names);
        CompoundTag tag = stack.getTag();
        if (tag != null) { lines.add("    nbt: " + tag); }
        CompoundTag shared = item.getShareTag(stack);
        if (shared != null && !Objects.equals(shared, tag)) { lines.add("    share nbt: " + shared); }
    }
}
