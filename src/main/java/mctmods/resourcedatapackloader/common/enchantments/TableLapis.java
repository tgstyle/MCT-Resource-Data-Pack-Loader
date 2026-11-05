package mctmods.resourcedatapackloader.common.enchantments;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public final class TableLapis {
    public static final AttachmentType<ItemStack> TYPE = AttachmentType.builder(() -> ItemStack.EMPTY).serialize(ItemStack.OPTIONAL_CODEC.fieldOf("Lapis").codec(), stack -> !stack.isEmpty()).build();
    private static final int SLOT = 1;

    private TableLapis() {}

    @Nullable private static EnchantingTableBlockEntity at(Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof EnchantingTableBlockEntity table ? table : null; }

    public static void onOpen(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof EnchantmentMenu menu)) { return; }
        menu.access.execute((level, pos) -> {
            EnchantingTableBlockEntity table = at(level, pos);
            if (table == null) { return; }
            ItemStack lapis = table.getExistingDataOrNull(TYPE);
            if (lapis == null || lapis.isEmpty()) { return; }
            table.setData(TYPE, ItemStack.EMPTY);
            menu.getSlot(SLOT).set(lapis);
        });
    }

    public static void closing(ContainerLevelAccess access, Container slots) {
        if (!ContentEnchanting.keepLapis()) { return; }
        access.execute((level, pos) -> {
            EnchantingTableBlockEntity table = at(level, pos);
            if (table == null) { return; }
            ItemStack held = table.getData(TYPE);
            ItemStack placed = slots.getItem(SLOT);
            if (held.isEmpty()) { table.setData(TYPE, slots.removeItemNoUpdate(SLOT)); }
            else if (ItemStack.isSameItemSameComponents(held, placed)) {
                int moved = Math.min(placed.getCount(), held.getMaxStackSize() - held.getCount());
                table.setData(TYPE, held.copyWithCount(held.getCount() + moved));
                slots.removeItem(SLOT, moved);
            }
        });
    }

    public static void broken(Level level, BlockPos pos) {
        EnchantingTableBlockEntity table = at(level, pos);
        if (table == null) { return; }
        ItemStack lapis = table.getExistingDataOrNull(TYPE);
        if (lapis == null || lapis.isEmpty()) { return; }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), lapis);
        table.setData(TYPE, ItemStack.EMPTY);
    }
}
