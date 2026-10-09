package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Held;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class ContentBlockEntities {
    private ContentBlockEntities() {}

    static <T extends BlockEntity> void typeFor(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper, Identifier id, Class<?> marker, BlockEntityType.BlockEntitySupplier<T> factory, Held<BlockEntityType<T>> held, String kind) {
        List<Block> blocks = new ArrayList<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (marker.isInstance(entry.block())) { blocks.add(entry.block()); }
        }
        if (blocks.isEmpty()) { return; }
        BlockEntityType<T> built = new BlockEntityType<>(factory, Set.copyOf(blocks));
        held.set(built);
        helper.register(id, built);
        ContentLog.LOGGER.info("Registered the {} block entity type for {} pack {} block(s)", kind, blocks.size(), kind);
    }
}
