package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBanner;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.datafixers.DSL;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentBanners {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "banner");
    @Nullable private static BlockEntityType<ContentBannerBlockEntity> type;

    private ContentBanners() {}

    public static BlockEntityType<ContentBannerBlockEntity> type() {
        if (type == null) { throw new IllegalStateException("No pack banner block entity type is registered"); }
        return type;
    }

    @Nullable public static BlockEntityType<ContentBannerBlockEntity> registered() { return type; }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        List<Block> blocks = new ArrayList<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentBanner) { blocks.add(entry.block()); }
        }
        if (blocks.isEmpty()) { return; }
        type = BlockEntityType.Builder.of(ContentBannerBlockEntity::new, blocks.toArray(Block[]::new)).build(DSL.remainderType());
        helper.register(ID, type);
        ContentLog.LOGGER.info("Registered the banner block entity type for {} pack banner block(s)", blocks.size());
    }
}
