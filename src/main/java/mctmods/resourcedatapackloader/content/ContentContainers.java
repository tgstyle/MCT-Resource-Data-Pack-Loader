package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;
import mctmods.resourcedatapackloader.util.Held;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ContentContainers {
    public static final ResourceLocation ID = ResourceDataPackLoader.id("container");
    private static final ResourceLocation STORAGE_ID = ResourceDataPackLoader.id("entity_storage");
    public static final ContainerDef FALLBACK = new ContainerDef(3, 9, "", false, null, null, 0, 0, "");
    public static final Held<BlockEntityType<ContentContainerBlockEntity>> TYPE = new Held<>("pack container block entity type");
    public static final Held<MenuType<ContentContainerMenu>> MENU = new Held<>("pack container menu type");
    public static final Held<MenuType<EntityStorageMenu>> STORAGE_MENU = new Held<>("entity storage menu type");

    private ContentContainers() {}

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) { ContentBlockEntities.typeFor(helper, ID, IContentContainer.class, ContentContainerBlockEntity::new, TYPE, "container"); }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BlockEntityType<ContentContainerBlockEntity> registered = TYPE.registeredType();
        if (registered != null) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, registered, (held, side) -> new InvWrapper(held)); }
    }

    public static void registerMenu(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        MenuType<ContentContainerMenu> chest = IMenuTypeExtension.create(ContentContainerMenu::new);
        MENU.set(chest);
        helper.register(ID, chest);
        MenuType<EntityStorageMenu> storage = IMenuTypeExtension.create(EntityStorageMenu::new);
        STORAGE_MENU.set(storage);
        helper.register(STORAGE_ID, storage);
    }
}
