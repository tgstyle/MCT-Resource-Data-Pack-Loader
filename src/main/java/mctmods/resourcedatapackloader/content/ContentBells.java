package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBellBlockEntity;
import mctmods.resourcedatapackloader.content.interfaces.IContentBell;
import mctmods.resourcedatapackloader.util.Held;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ContentBells {
    public static final Identifier ID = ResourceDataPackLoader.id("bell");
    public static final int RING_EVENT = 1;
    private static final String BODY = "_body";
    public static final Held<BlockEntityType<ContentBellBlockEntity>> TYPE = new Held<>("pack bell block entity type");

    private ContentBells() {}

    public static Identifier body(Identifier id) { return Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + BODY); }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) { ContentBlockEntities.typeFor(helper, ID, IContentBell.class, ContentBellBlockEntity::new, TYPE, "bell"); }
}
