package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlockEntity;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentContainers {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "container");
    private static final Identifier STORAGE_ID = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "entity_storage");
    public static final ContainerDef FALLBACK = new ContainerDef(3, 9, "", false, null, null, 0, 0, "");
    @Nullable private static BlockEntityType<ContentContainerBlockEntity> type;
    @Nullable private static MenuType<ContentContainerMenu> menu;
    @Nullable private static MenuType<EntityStorageMenu> storageMenu;

    private ContentContainers() {}

    public static BlockEntityType<ContentContainerBlockEntity> type() {
        if (type == null) { throw new IllegalStateException("No pack container block entity type is registered"); }
        return type;
    }

    public static MenuType<ContentContainerMenu> menu() {
        if (menu == null) { throw new IllegalStateException("No pack container menu type is registered"); }
        return menu;
    }

    @Nullable public static BlockEntityType<ContentContainerBlockEntity> registeredType() { return type; }

    @Nullable public static MenuType<ContentContainerMenu> registeredMenu() { return menu; }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        List<Block> blocks = new ArrayList<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.block() instanceof IContentContainer) { blocks.add(entry.block()); }
        }
        if (blocks.isEmpty()) { return; }
        type = new BlockEntityType<>(ContentContainerBlockEntity::new, Set.copyOf(blocks));
        helper.register(ID, type);
        ContentLog.LOGGER.info("Registered the container block entity type for {} pack container block(s)", blocks.size());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (type != null) { event.registerBlockEntity(Capabilities.Item.BLOCK, type, (held, _) -> VanillaContainerWrapper.of(held)); }
    }

    public static void registerMenu(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        menu = IMenuTypeExtension.create(ContentContainerMenu::new);
        helper.register(ID, menu);
        storageMenu = IMenuTypeExtension.create(EntityStorageMenu::new);
        helper.register(STORAGE_ID, storageMenu);
    }

    public static MenuType<EntityStorageMenu> storageMenu() {
        if (storageMenu == null) { throw new IllegalStateException("No entity storage menu type is registered"); }
        return storageMenu;
    }

    @Nullable public static MenuType<EntityStorageMenu> registeredStorageMenu() { return storageMenu; }
}
