package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentContainerBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private static final String STOCKED = "RdplStocked";
    private final ChestLidController lid = new ChestLidController();
    private NonNullList<ItemStack> items;
    private int openers;
    private boolean stocked;

    public ContentContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ContentContainers.type(), pos, state);
        this.items = NonNullList.withSize(def(state).size(), ItemStack.EMPTY);
    }

    private static ContainerDef def(BlockState state) {
        return state.getBlock() instanceof IContentContainer held ? held.container() : ContentContainers.FALLBACK;
    }

    public ContainerDef def() { return def(getBlockState()); }

    @Override public int getContainerSize() { return items.size(); }

    @Override @Nonnull protected NonNullList<ItemStack> getItems() { return items; }

    @Override protected void setItems(@Nonnull NonNullList<ItemStack> held) { this.items = held; }

    @Override @Nonnull protected Component getDefaultName() { return getBlockState().getBlock().getName(); }

    @Override @Nonnull protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory) {
        return new ContentContainerMenu(id, inventory, this, def());
    }

    @Override public void startOpen(@Nonnull Player player) {
        if (player.isSpectator()) { return; }
        openers++;
        onOpenerCountChanged();
    }

    @Override public void stopOpen(@Nonnull Player player) {
        if (player.isSpectator()) { return; }
        openers = Math.max(0, openers - 1);
        onOpenerCountChanged();
    }

    private void onOpenerCountChanged() {
        if (level == null) { return; }
        if (openers == 1) { sound(SoundEvents.CHEST_OPEN); }
        if (openers == 0) { sound(SoundEvents.CHEST_CLOSE); }
        level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, openers);
    }

    private void sound(SoundEvent event) {
        if (level == null || !def().chestModel()) { return; }
        BlockPos at = getBlockPos();
        level.playSound(null, at.getX() + 0.5D, at.getY() + 0.5D, at.getZ() + 0.5D, event, SoundSource.BLOCKS,
                0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override public float getOpenNess(float partial) { return lid.getOpenness(partial); }

    public static void lidTick(Level ignoredLevel, BlockPos ignoredPos, BlockState ignoredState, ContentContainerBlockEntity held) { held.lid.tickLid(); }

    @Override public boolean triggerEvent(int id, int value) {
        if (id != 1) { return super.triggerEvent(id, value); }
        openers = value;
        lid.shouldBeOpen(value > 0);
        return true;
    }

    @Override public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(def().size(), ItemStack.EMPTY);
        this.stocked = tag.getBoolean(STOCKED);
        if (!tryLoadLootTable(tag)) { ContainerHelper.loadAllItems(tag, this.items); }
    }

    @Override protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(STOCKED, stocked);
        if (!trySaveLootTable(tag)) { ContainerHelper.saveAllItems(tag, this.items); }
    }

    public void stock() {
        ContainerDef held = def();
        if (stocked || held.lootTable().isEmpty() || this.lootTable != null || level == null) { return; }
        ResourceLocation table = ResourceLocation.tryParse(held.lootTable());
        if (table == null) {
            ContentLog.LOGGER.error("The container at {} names the loot table '{}', which is not a valid id, so it starts empty", getBlockPos(), held.lootTable());
            return;
        }
        setLootTable(table, level.getRandom().nextLong());
        stocked = true;
    }

    @Override public void unpackLootTable(@Nullable Player player) {
        if (this.lootTable != null) { stocked = true; }
        super.unpackLootTable(player);
    }

    public static int comparatorOutput(BlockEntity held) {
        return held instanceof Container container ? AbstractContainerMenu.getRedstoneSignalFromContainer(container) : 0;
    }
}
