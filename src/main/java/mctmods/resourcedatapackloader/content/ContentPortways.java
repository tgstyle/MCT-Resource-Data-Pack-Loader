package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentPortwayBlock;
import mctmods.resourcedatapackloader.content.block.ContentPortwayBlockEntity;
import mctmods.resourcedatapackloader.util.Held;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ContentPortways {
    public static final Identifier ID = ResourceDataPackLoader.id("portway");
    public static final Held<BlockEntityType<ContentPortwayBlockEntity>> TYPE = new Held<>("pack portway block entity type");

    private ContentPortways() {}

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) { ContentBlockEntities.typeFor(helper, ID, ContentPortwayBlock.class, ContentPortwayBlockEntity::new, TYPE, "portway"); }
}
