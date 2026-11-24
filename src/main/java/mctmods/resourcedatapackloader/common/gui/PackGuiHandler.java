package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.entities.EntityStorage;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.items.ContentPortwayItem;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.StorageDef;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class PackGuiHandler implements IGuiHandler {
    public static final int CONTAINER = 1;
    public static final int POUCH_MAIN = 2;
    public static final int POUCH_OFF = 3;
    public static final int POUCH_BAUBLE = 4;
    public static final int ENTITY = 5;
    public static final int PORTWAY = 6;
    public static final int PORTWAY_MAIN = 7;
    public static final int PORTWAY_OFF = 8;

    public static void openHand(EntityPlayer player, World world, EnumHand hand, int mainId, int offId) {
        player.openGui(ResourceDataPackLoader.INSTANCE, hand == EnumHand.MAIN_HAND ? mainId : offId, world, 0, 0, 0);
    }

    @Nullable private static ContainerPortway portway(EntityPlayer player, World world, int id, int x, int y, int z) {
        if (id == PORTWAY_MAIN || id == PORTWAY_OFF) {
            EnumHand hand = hand(id, PORTWAY_OFF);
            return player.getHeldItem(hand).getItem() instanceof ContentPortwayItem ? new ContainerPortway(player.inventory, world, hand) : null;
        }
        BlockPos pos = new BlockPos(x, y, z);
        return world.getBlockState(pos).getBlock() instanceof ContentPortwayBlock ? new ContainerPortway(player.inventory, world, pos) : null;
    }

    private static EnumHand hand(int id, int offId) { return id == offId ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND; }

    private static ContainerPouch pouchContainer(EntityPlayer player, Pouch pouch, int id, int x) {
        return new ContainerPouch(player.inventory, pouch.inventory, pouch.stack, pouch.source, pouch.def.rows, pouch.def.columns).worn(id == POUCH_BAUBLE ? x : -1);
    }

    @Nullable private static ContainerEntityStorage storage(EntityPlayer player, World world, int entityId) {
        Entity entity = world.getEntityByID(entityId);
        StorageDef def = EntityStorage.def(entity);
        return entity == null || def == null ? null : new ContainerEntityStorage(player.inventory, entity, def);
    }

    @Override @Nullable public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == ENTITY) { return storage(player, world, x); }
        if (id == PORTWAY || id == PORTWAY_MAIN || id == PORTWAY_OFF) { return portway(player, world, id, x, y, z); }
        Pouch pouch = id == POUCH_BAUBLE ? bauble(player, x) : id == POUCH_MAIN || id == POUCH_OFF ? pouch(player, id) : null;
        if (id == POUCH_MAIN || id == POUCH_OFF || id == POUCH_BAUBLE) { return pouch == null ? null : pouchContainer(player, pouch, id, x); }
        TileEntityPackContainer tile = held(world, x, y, z);
        if (id != CONTAINER || tile == null) { return null; }
        return tile.createContainer(player.inventory, player);
    }

    private static final class Pouch {
        private final IInventory inventory;
        private final ItemStack stack;
        private final ContainerDef def;
        private final ContainerPouch.ISource source;
        private Pouch(IInventory inventory, ItemStack stack, ContainerDef def, ContainerPouch.ISource source) {
            this.inventory = inventory;
            this.stack = stack;
            this.def = def;
            this.source = source;
        }
    }

    @Nullable private static Pouch pouch(EntityPlayer player, int id) {
        EnumHand hand = hand(id, POUCH_OFF);
        return of(player, player.getHeldItem(hand), holder -> holder.getHeldItem(hand));
    }

    @Nullable private static Pouch bauble(EntityPlayer player, int slot) {
        if (!ContentContainerItem.baubled()) { return null; }
        return of(player, mctmods.resourcedatapackloader.core.integration.baubles.BaublesPouch.worn(player, slot),
                holder -> mctmods.resourcedatapackloader.core.integration.baubles.BaublesPouch.worn(holder, slot));
    }

    @Nullable private static Pouch of(EntityPlayer player, ItemStack stack, ContainerPouch.ISource source) {
        if (!(stack.getItem() instanceof ContentContainerItem)) { return null; }
        ContainerDef def = ((ContentContainerItem) stack.getItem()).holds();
        String named = Lang.nameKey(stack.getTranslationKey());
        IInventory inventory = def.ender ? new EnderInventory(player.world, player.getUniqueID(), def.rows, def.columns, PouchInventory.title(stack, named), stack.hasDisplayName(), null) : new PouchInventory(stack, def, named);
        return new Pouch(inventory, stack, def, source);
    }

    @Override @Nullable public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == PORTWAY || id == PORTWAY_MAIN || id == PORTWAY_OFF) {
            ContainerPortway ways = portway(player, world, id, x, y, z);
            return ways == null ? null : new mctmods.resourcedatapackloader.client.gui.GuiPortway(ways);
        }
        if (id == ENTITY) {
            ContainerEntityStorage storage = storage(player, world, x);
            Entity entity = world.getEntityByID(x);
            return storage == null || entity == null ? null : new mctmods.resourcedatapackloader.client.gui.GuiEntityStorage(storage, entity);
        }
        Pouch shown = id == POUCH_BAUBLE ? bauble(player, x) : id == POUCH_MAIN || id == POUCH_OFF ? pouch(player, id) : null;
        if (shown != null) { return new mctmods.resourcedatapackloader.client.gui.GuiPackContainer(pouchContainer(player, shown, id, x), shown.inventory, shown.def.rows, shown.def.columns, shown.def); }
        TileEntityPackContainer tile = held(world, x, y, z);
        if (id != CONTAINER || tile == null) { return null; }
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        ContainerDef def = block instanceof ContentContainerBlock ? ((ContentContainerBlock) block).container() : null;
        EnderInventory ender = tile.ender(player);
        if (ender != null) { return new mctmods.resourcedatapackloader.client.gui.GuiPackContainer(new ContainerPack(player.inventory, ender), ender, ender.rows(), ender.columns(), def); }
        return new mctmods.resourcedatapackloader.client.gui.GuiPackContainer(player.inventory, tile, def);
    }

    @Nullable private static TileEntityPackContainer held(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return tile instanceof TileEntityPackContainer ? (TileEntityPackContainer) tile : null;
    }
}
