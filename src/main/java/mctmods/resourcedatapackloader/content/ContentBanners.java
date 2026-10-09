package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBannerBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBanner;
import mctmods.resourcedatapackloader.util.Held;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegisterEvent;

public final class ContentBanners {
    public static final ResourceLocation ID = ResourceDataPackLoader.id("banner");
    public static final Held<BlockEntityType<ContentBannerBlockEntity>> TYPE = new Held<>("pack banner block entity type");

    private ContentBanners() {}

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) { ContentBlockEntities.typeFor(helper, ID, IContentBanner.class, ContentBannerBlockEntity::new, TYPE, "banner"); }
}
