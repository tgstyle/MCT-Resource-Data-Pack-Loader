package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentContainer;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentContainerBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private static final String STOCKED = "RdplStocked";
    private static final String ROWS = "RdplRows";
    private static final String COLUMNS = "RdplColumns";
    private final ChestLidController lid = new ChestLidController();
    private NonNullList<ItemStack> items;
    private int openers;
    private int rows;
    private int columns;
    private boolean stocked;

    public ContentContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ContentContainers.type(), pos, state);
        ContainerDef def = def(state);
        this.rows = def.rows();
        this.columns = def.columns();
        this.items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
    }

    private static ContainerDef def(BlockState state) {
        return state.getBlock() instanceof IContentContainer held ? held.container() : ContentContainers.FALLBACK;
    }

    public ContainerDef def() { return def(getBlockState()); }

    public ContainerDef shape() { return def().sized(rows, columns); }

    @Override public int getContainerSize() { return items.size(); }

    @Override @Nonnull protected NonNullList<ItemStack> getItems() { return items; }

    @Override protected void setItems(@Nonnull NonNullList<ItemStack> held) { this.items = held; }

    @Override @Nonnull protected Component getDefaultName() { return getBlockState().getBlock().getName(); }

    @Override @Nonnull protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory) {
        return new ContentContainerMenu(id, inventory, this, shape());
    }

    @Override public void startOpen(@Nonnull ContainerUser user) {
        if (user.getLivingEntity().isSpectator()) { return; }
        openers++;
        onOpenerCountChanged();
    }

    @Override public void stopOpen(@Nonnull ContainerUser user) {
        if (user.getLivingEntity().isSpectator()) { return; }
        openers = Math.max(0, openers - 1);
        onOpenerCountChanged();
    }

    private void onOpenerCountChanged() {
        if (level == null) { return; }
        if (openers == 1) { sound(SoundEvents.CHEST_OPEN); }
        if (openers == 0) { sound(SoundEvents.CHEST_CLOSE); }
        level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, openers);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
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

    @Override protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        ContainerDef def = def();
        this.rows = input.getInt(ROWS).map(held -> Math.max(1, held)).orElse(def.rows());
        this.columns = input.getInt(COLUMNS).map(held -> Math.max(1, held)).orElse(def.columns());
        this.items = NonNullList.withSize(rows * columns, ItemStack.EMPTY);
        this.stocked = input.getBooleanOr(STOCKED, false);
        if (!tryLoadLootTable(input)) { ContainerHelper.loadAllItems(input, this.items); }
    }

    @Override protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(STOCKED, stocked);
        output.putInt(ROWS, rows);
        output.putInt(COLUMNS, columns);
        if (!trySaveLootTable(output)) { ContainerHelper.saveAllItems(output, this.items); }
    }

    public void placed() {
        stocked = true;
        setChanged();
    }

    @Override public boolean canOpen(@Nonnull Player player) { return true; }

    @Override public boolean stillValid(@Nonnull Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) { return false; }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override protected void applyImplicitComponents(@Nonnull DataComponentGetter input) {
        super.applyImplicitComponents(new DataComponentGetter() {
            @Override @Nullable public <T> T get(@Nonnull DataComponentType<? extends T> type) {
                T value = input.get(type);
                return type == DataComponents.CUSTOM_NAME ? null : value;
            }

            @Override @Nonnull public <T> T getOrDefault(@Nonnull DataComponentType<? extends T> type, @Nonnull T fallback) { return input.getOrDefault(type, fallback); }
        });
    }

    @Override public void unpackLootTable(@Nullable Player player) {
        if (!stocked && level != null && !level.isClientSide()) { stock(level.getRandom().nextLong()); }
        super.unpackLootTable(player);
    }

    private void stock(long seed) {
        stocked = true;
        setChanged();
        String named = def().lootTable();
        if (named.isEmpty() || this.lootTable != null) { return; }
        Identifier table = Identifier.tryParse(named);
        if (table == null) {
            ContentLog.LOGGER.error("The container at {} names the loot table '{}', which is not a valid id, so it starts empty", getBlockPos(), named);
            return;
        }
        setLootTable(ResourceKey.create(Registries.LOOT_TABLE, table), seed);
    }

    public static int comparatorOutput(BlockEntity held) {
        return held instanceof Container container ? AbstractContainerMenu.getRedstoneSignalFromContainer(container) : 0;
    }
}
