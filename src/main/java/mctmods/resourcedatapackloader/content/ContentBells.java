package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.datafixers.DSL;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentBells {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "bell");
    public static final int RING_EVENT = 1;
    private static final String BODY = "_body";
    @Nullable private static BlockEntityType<ContentBellBlockEntity> type;

    private ContentBells() {}

    public static BlockEntityType<ContentBellBlockEntity> type() {
        if (type == null) { throw new IllegalStateException("No pack bell block entity type is registered"); }
        return type;
    }

    @Nullable public static BlockEntityType<ContentBellBlockEntity> registeredType() { return type; }

    public static ResourceLocation body(ResourceLocation id) { return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + BODY); }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        List<Block> blocks = new ArrayList<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentBell) { blocks.add(entry.block()); }
        }
        if (blocks.isEmpty()) { return; }
        type = BlockEntityType.Builder.of(ContentBellBlockEntity::new, blocks.toArray(Block[]::new)).build(DSL.remainderType());
        helper.register(ID, type);
        ContentLog.LOGGER.info("Registered the bell block entity type for {} pack bell block(s)", blocks.size());
    }
}
