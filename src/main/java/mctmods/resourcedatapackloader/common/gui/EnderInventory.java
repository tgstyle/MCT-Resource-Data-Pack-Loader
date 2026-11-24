package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EnderInventory extends StackListInventory {
    public static final int STORE = ContainerDef.MOST_ROWS * ContainerDef.MOST_COLUMNS;
    @Nullable private final EnderStore store;
    private final UUID owner;
    private final int rows;
    private final int columns;
    private final ITextComponent title;
    private final boolean custom;
    @Nullable private final TileEntityPackContainer at;

    public EnderInventory(World world, UUID owner, int rows, int columns, ITextComponent title, boolean custom, @Nullable TileEntityPackContainer at) {
        this(world.isRemote ? null : EnderStore.get(world), owner, rows, columns, title, custom, at);
    }

    private EnderInventory(@Nullable EnderStore store, UUID owner, int rows, int columns, ITextComponent title, boolean custom, @Nullable TileEntityPackContainer at) {
        super(store == null ? NonNullList.withSize(STORE, ItemStack.EMPTY) : store.items(owner));
        this.store = store;
        this.owner = owner;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        this.title = title;
        this.custom = custom;
        this.at = at;
    }

    public int rows() { return rows; }

    public int columns() { return columns; }

    public int version() { return store == null ? 0 : store.version(owner); }

    @Override public int getSizeInventory() { return Math.min(STORE, rows * columns); }

    @Override public void markDirty() { if (store != null) { store.changed(owner); } }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return at == null || at.isUsableByPlayer(player); }

    @Override public void openInventory(@Nonnull EntityPlayer player) { if (at != null) { at.openInventory(player); } }

    @Override public void closeInventory(@Nonnull EntityPlayer player) {
        markDirty();
        if (at != null) { at.closeInventory(player); }
    }

    @Override @Nonnull public String getName() { return title.getUnformattedText(); }

    @Override public boolean hasCustomName() { return custom; }

    @Override @Nonnull public ITextComponent getDisplayName() { return title; }
}
