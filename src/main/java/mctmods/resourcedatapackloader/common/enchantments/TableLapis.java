package mctmods.resourcedatapackloader.common.enchantments;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public final class TableLapis {
    private static final String LAPIS = "Lapis";
    private static final int SLOT = 1;
    private static final AttachmentType<ItemStack> TYPE = AttachmentType.builder(() -> ItemStack.EMPTY).serialize(ItemStack.OPTIONAL_CODEC.fieldOf(LAPIS), stack -> !stack.isEmpty()).build();

    private TableLapis() {}

    public static AttachmentType<ItemStack> type() { return TYPE; }

    public static void onOpen(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof EnchantmentMenu menu)) { return; }
        menu.access.execute((level, pos) -> {
            if (!(level.getBlockEntity(pos) instanceof EnchantingTableBlockEntity table) || !table.hasData(TYPE)) { return; }
            ItemStack lapis = table.removeData(TYPE);
            if (lapis != null && !lapis.isEmpty()) { menu.getSlot(SLOT).set(lapis); }
        });
    }

    public static void closing(EnchantmentMenu menu) {
        if (!ContentEnchanting.keepLapis()) { return; }
        menu.access.execute((level, pos) -> {
            if (!(level.getBlockEntity(pos) instanceof EnchantingTableBlockEntity table)) { return; }
            Slot slot = menu.getSlot(SLOT);
            ItemStack put = slot.getItem();
            ItemStack kept = table.getExistingDataOrNull(TYPE);
            int had = kept == null ? 0 : kept.getCount();
            if (put.isEmpty() || had > 0 && !ItemStack.isSameItemSameComponents(kept, put)) { return; }
            int moved = Math.min(put.getCount(), put.getMaxStackSize() - had);
            if (moved <= 0) { return; }
            table.setData(TYPE, put.copyWithCount(had + moved));
            slot.remove(moved);
        });
    }

    public static void broken(BlockEntity entity, BlockPos pos) {
        if (!(entity instanceof EnchantingTableBlockEntity table) || table.getLevel() == null || !table.hasData(TYPE)) { return; }
        ItemStack lapis = table.removeData(TYPE);
        if (lapis != null && !lapis.isEmpty()) { Containers.dropItemStack(table.getLevel(), pos.getX(), pos.getY(), pos.getZ(), lapis); }
    }
}
