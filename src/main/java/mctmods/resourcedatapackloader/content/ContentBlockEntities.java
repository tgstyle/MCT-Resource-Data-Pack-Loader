package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Held;

import com.mojang.datafixers.DSL;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;

final class ContentBlockEntities {
    private ContentBlockEntities() {}

    static <T extends BlockEntity> void typeFor(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper, ResourceLocation id, Class<?> marker, BlockEntityType.BlockEntitySupplier<T> factory, Held<BlockEntityType<T>> held, String kind) {
        List<Block> blocks = new ArrayList<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (marker.isInstance(entry.block())) { blocks.add(entry.block()); }
        }
        if (blocks.isEmpty()) { return; }
        BlockEntityType<T> built = BlockEntityType.Builder.of(factory, blocks.toArray(Block[]::new)).build(DSL.remainderType());
        held.set(built);
        helper.register(id, built);
        ContentLog.LOGGER.info("Registered the {} block entity type for {} pack {} block(s)", kind, blocks.size(), kind);
    }
}
