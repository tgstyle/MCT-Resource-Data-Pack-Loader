package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.block.ContentBlockContainer;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;
import mctmods.resourcedatapackloader.content.item.ContentItemContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import javax.annotation.Nullable;

public class PackGuiHandler implements IGuiHandler {
    public static final int CONTAINER = 1;
    public static final int POUCH_MAIN = 2;
    public static final int POUCH_OFF = 3;
    public static final int POUCH_BAUBLE = 4;
    public static final int ENTITY = 5;

    @Nullable private static ContainerEntityStorage storage(EntityPlayer player, World world, int entityId) {
        Entity entity = world.getEntityByID(entityId);
        StorageDef def = EntityStorage.def(entity);
        return entity == null || def == null ? null : new ContainerEntityStorage(player.inventory, entity, def);
    }

    @Override @Nullable public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == ENTITY) { return storage(player, world, x); }
        Pouch pouch = id == POUCH_BAUBLE ? bauble(player, x) : id == POUCH_MAIN || id == POUCH_OFF ? pouch(player, id) : null;
        if (id == POUCH_MAIN || id == POUCH_OFF || id == POUCH_BAUBLE) {
            return pouch == null ? null : new ContainerPouch(player.inventory, pouch.inventory, pouch.source, pouch.def.rows, pouch.def.columns)
                    .worn(id == POUCH_BAUBLE ? x : -1);
        }
        TileEntityPackContainer tile = held(world, x, y, z);
        if (id != CONTAINER || tile == null) { return null; }
        return tile.createContainer(player.inventory, player);
    }

    private static final class Pouch {
        private final PouchInventory inventory;
        private final ContainerDef def;
        private final ContainerPouch.ISource source;
        private Pouch(PouchInventory inventory, ContainerDef def, ContainerPouch.ISource source) {
            this.inventory = inventory;
            this.def = def;
            this.source = source;
        }
    }

    @Nullable private static Pouch pouch(EntityPlayer player, int id) {
        EnumHand hand = id == POUCH_OFF ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        return of(player.getHeldItem(hand), holder -> holder.getHeldItem(hand));
    }

    @Nullable private static Pouch bauble(EntityPlayer player, int slot) {
        if (!ContentItemContainer.baubled()) { return null; }
        return of(mctmods.resourcedatapackloader.content.compat.BaublesPouch.worn(player, slot),
                holder -> mctmods.resourcedatapackloader.content.compat.BaublesPouch.worn(holder, slot));
    }

    @Nullable private static Pouch of(ItemStack stack, ContainerPouch.ISource source) {
        if (!(stack.getItem() instanceof ContentItemContainer)) { return null; }
        ContentItemContainer item = (ContentItemContainer) stack.getItem();
        return new Pouch(new PouchInventory(stack, item.holds(), stack.getTranslationKey() + ".name"), item.holds(), source);
    }

    @Override @Nullable public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == ENTITY) {
            ContainerEntityStorage storage = storage(player, world, x);
            Entity entity = world.getEntityByID(x);
            return storage == null || entity == null ? null : new mctmods.resourcedatapackloader.client.gui.GuiEntityStorage(storage, entity);
        }
        Pouch shown = id == POUCH_BAUBLE ? bauble(player, x) : id == POUCH_MAIN || id == POUCH_OFF ? pouch(player, id) : null;
        if (shown != null) {
            return new mctmods.resourcedatapackloader.client.gui.GuiPackContainer(
                    new ContainerPouch(player.inventory, shown.inventory, shown.source, shown.def.rows, shown.def.columns)
                            .worn(id == POUCH_BAUBLE ? x : -1),
                    shown.inventory, shown.def.rows, shown.def.columns, shown.def);
        }
        TileEntityPackContainer tile = held(world, x, y, z);
        if (id != CONTAINER || tile == null) { return null; }
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return new mctmods.resourcedatapackloader.client.gui.GuiPackContainer(player.inventory, tile,
                block instanceof ContentBlockContainer ? ((ContentBlockContainer) block).container() : null);
    }

    @Nullable private static TileEntityPackContainer held(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return tile instanceof TileEntityPackContainer ? (TileEntityPackContainer) tile : null;
    }
}
