package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortway;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.def.CostPart;
import mctmods.resourcedatapackloader.content.def.PortwayCost;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.def.PortwayMenu;
import mctmods.resourcedatapackloader.content.item.ContentItemPortway;
import mctmods.resourcedatapackloader.content.util.PortwayCosts;
import mctmods.resourcedatapackloader.content.util.PortwayFx;
import mctmods.resourcedatapackloader.content.util.PortwayOrder;
import mctmods.resourcedatapackloader.content.util.PortwayStop;
import mctmods.resourcedatapackloader.content.util.PortwayTravel;
import mctmods.resourcedatapackloader.content.util.Portways;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerPortway extends Container {
    public static final String TAG = "rdplStop";
    public static final String DIMENSION = "dim";
    public static final String HERE = "here";
    public static final String AWAY = "away";
    public static final String NAME = "name";
    public static final String LOCKED = "locked";
    public static final String XP = "xp";
    public static final String POINTS = "points";
    public static final String ITEM = "item";
    public static final String ITEM_NAME = "itemName";
    public static final String HEALTH = "health";
    public static final String HUNGER = "hunger";
    public static final String PERSONAL = "personal";
    public static final String WAIT = "wait";
    public static final String NAV = "rdplNav";
    public static final String KIND = "kind";
    public static final String PAGE = "page";
    public static final String PAGES = "pages";
    public static final String ORDER = "order";
    public static final int FILTER_MAX = 32;
    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    private static final int SLOTS = ROWS * COLUMNS;
    private static final int PAGED = 45;
    private static final int PREVIOUS = 45;
    private static final int SORT = 47;
    private static final int PAGE_AT = 49;
    private static final int NEXT = 53;
    private static final double REACH = 64.0D;
    private final InventoryBasic icons;
    private final InventoryPlayer viewer;
    @Nullable private final BlockPos origin;
    @Nullable private final EnumHand hand;
    @Nullable private final Item openedWith;
    private final int originDimension;
    private List<PortwayOrder.Entry> sorted = Collections.emptyList();
    private List<PortwayOrder.Entry> shown = Collections.emptyList();
    private Set<PortwayStop> mine = Collections.emptySet();
    private String order = "";
    private String filter = "";
    private String pending = "";
    private long filtered = -4L;
    private int page;

    public ContainerPortway(InventoryPlayer player, World world, BlockPos origin) { this(player, world, origin, null, null, new ItemStack(world.getBlockState(origin).getBlock()).getDisplayName()); }

    public ContainerPortway(InventoryPlayer player, World world, EnumHand hand) { this(player, world, null, hand, player.player.getHeldItem(hand).getItem(), player.player.getHeldItem(hand).getDisplayName()); }

    private ContainerPortway(InventoryPlayer player, World world, @Nullable BlockPos origin, @Nullable EnumHand hand, @Nullable Item openedWith, String title) {
        this.viewer = player;
        this.origin = origin;
        this.hand = hand;
        this.openedWith = openedWith;
        this.originDimension = world.provider.getDimension();
        this.icons = new InventoryBasic(title, true, SLOTS);
        int wide = ContainerPack.width(COLUMNS);
        int left = (wide - COLUMNS * ContainerPack.SLOT) / 2 + 1;
        for (int slot = 0; slot < SLOTS; slot++) {
            addSlotToContainer(new Slot(icons, slot, left + slot % COLUMNS * ContainerPack.SLOT, ContainerPack.HEADER + 1 + slot / COLUMNS * ContainerPack.SLOT) {
                @Override public boolean isItemValid(@Nonnull ItemStack stack) { return false; }

                @Override public boolean canTakeStack(@Nonnull EntityPlayer player) { return false; }
            });
        }
        for (Slot slot : ContainerPack.playerSlots(player, wide, ContainerPack.height(ROWS), Slot::new)) { addSlotToContainer(slot); }
        if (!world.isRemote && player.player instanceof EntityPlayerMP) { reload(); }
    }

    @Nullable private PortwayDef opening(EntityPlayer player) {
        if (origin != null) {
            Block block = player.world.getBlockState(origin).getBlock();
            return block instanceof ContentBlockPortway ? ((ContentBlockPortway) block).settings() : null;
        }
        Item item = hand == null ? null : player.getHeldItem(hand).getItem();
        return item instanceof ContentItemPortway ? ((ContentItemPortway) item).portway().settings : null;
    }

    private void reload() {
        PortwayDef opening = opening(viewer.player);
        if (opening == null) {
            sorted = Collections.emptyList();
            mine = Collections.emptySet();
        }
        else {
            PortwayTravel.prune((EntityPlayerMP) viewer.player);
            if (order.isEmpty()) { order = PortwayOrder.saved(viewer.player, opening.menu.order); }
            sorted = PortwayOrder.entries(viewer.player, Portways.listed(viewer.player, opening, originDimension));
            PortwayOrder.arrange(sorted, order);
            mine = new HashSet<>(Portways.stops(viewer.player));
        }
        shown = PortwayOrder.matching(sorted, filter);
        refill(opening);
    }

    private int pages() { return Math.max(1, (shown.size() + PAGED - 1) / PAGED); }

    private void refill(@Nullable PortwayDef opening) {
        for (int slot = 0; slot < SLOTS; slot++) { icons.setInventorySlotContents(slot, ItemStack.EMPTY); }
        page = Math.max(0, Math.min(page, pages() - 1));
        if (opening == null) { return; }
        PortwayMenu menu = opening.menu;
        ItemStack locked = PortwayFx.icon(menu.locked, PortwayMenu.PLAIN.locked);
        for (int slot = 0; slot < PAGED; slot++) {
            int index = page * PAGED + slot;
            if (index >= shown.size()) { break; }
            PortwayOrder.Entry entry = shown.get(index);
            icons.setInventorySlotContents(slot, describe(viewer.player, opening, entry.stop, entry.label, mine.contains(entry.stop), locked));
        }
        if (page > 0) { icons.setInventorySlotContents(PREVIOUS, nav(PortwayFx.icon(menu.previous, PortwayMenu.PLAIN.previous), "previous")); }
        icons.setInventorySlotContents(SORT, sorter(PortwayFx.icon(menu.sort, PortwayMenu.PLAIN.sort)));
        icons.setInventorySlotContents(PAGE_AT, nav(PortwayFx.icon(menu.page, PortwayMenu.PLAIN.page), "page"));
        if (page < pages() - 1) { icons.setInventorySlotContents(NEXT, nav(PortwayFx.icon(menu.next, PortwayMenu.PLAIN.next), "next")); }
    }

    private ItemStack nav(ItemStack icon, String kind) {
        NBTTagCompound tag = icon.getOrCreateSubCompound(NAV);
        tag.setString(KIND, kind);
        tag.setInteger(PAGE, page + 1);
        tag.setInteger(PAGES, pages());
        return icon;
    }

    private ItemStack sorter(ItemStack icon) {
        NBTTagCompound tag = icon.getOrCreateSubCompound(NAV);
        tag.setString(KIND, "sort");
        tag.setString(ORDER, order);
        return icon;
    }

    private ItemStack describe(EntityPlayer player, PortwayDef opening, PortwayStop stop, String label, boolean personal, ItemStack locked) {
        PortwayDef held = stop.def();
        boolean barred = PortwayTravel.lockOf(player, held, stop.dimension, originDimension, false) != null;
        ItemStack shown = barred ? locked.copy() : new ItemStack(Block.REGISTRY.getObject(new ResourceLocation(stop.block)));
        NBTTagCompound tag = shown.getOrCreateSubCompound(TAG);
        tag.setInteger(DIMENSION, stop.dimension);
        tag.setInteger("x", stop.pos.getX());
        tag.setInteger("y", stop.pos.getY());
        tag.setInteger("z", stop.pos.getZ());
        tag.setString(NAME, label);
        tag.setBoolean(LOCKED, barred);
        boolean here = stop.dimension == originDimension && stop.pos.equals(origin);
        if (here) { tag.setBoolean(HERE, true); }
        else {
            tag.setBoolean(PERSONAL, personal);
            if (stop.dimension == originDimension) { tag.setInteger(AWAY, (int) Portways.distance(player, stop, originDimension)); }
        }
        tag.setInteger(WAIT, PortwayCosts.waiting(player));
        if (!PortwayCosts.exempt(player)) { price(tag, opening.cost, Portways.distance(player, stop, originDimension), stop.dimension != originDimension); }
        return shown;
    }

    private static void price(NBTTagCompound tag, PortwayCost cost, double distance, boolean cross) {
        tag.setInteger(XP, PortwayCosts.costOf(cost.xp, distance, cross));
        tag.setBoolean(POINTS, cost.xp.points);
        CostPart item = cost.item;
        tag.setInteger(ITEM, PortwayCosts.costOf(item, distance, cross));
        tag.setString(ITEM_NAME, item.id);
        tag.setInteger(HEALTH, PortwayCosts.costOf(cost.health, distance, cross));
        tag.setInteger(HUNGER, PortwayCosts.costOf(cost.hunger, distance, cross));
    }

    public InventoryBasic icons() { return icons; }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) {
        if (origin == null) { return hand != null && player.getHeldItem(hand).getItem() == openedWith && opening(player) != null; }
        return player.dimension == originDimension && player.getDistanceSqToCenter(origin) <= REACH && player.world.getBlockState(origin).getBlock() instanceof ContentBlockPortway;
    }

    @Override @Nonnull public ItemStack slotClick(int slot, int button, @Nonnull ClickType type, @Nonnull EntityPlayer player) {
        if (slot < 0 || slot >= SLOTS || !(player instanceof EntityPlayerMP)) { return ItemStack.EMPTY; }
        EntityPlayerMP mover = (EntityPlayerMP) player;
        if (!canInteractWith(mover)) {
            if (origin == null) {
                PortwayFx.card(mover, CardRule.PORTWAY_REFUSED, "", "", "item");
                mover.closeScreen();
            }
            return ItemStack.EMPTY;
        }
        if (slot >= PAGED) {
            if (type == ClickType.PICKUP) { turn(slot); }
            return ItemStack.EMPTY;
        }
        int index = page * PAGED + slot;
        if (index >= shown.size()) { return ItemStack.EMPTY; }
        if (type == ClickType.PICKUP) { choose(mover, shown.get(index).stop); }
        else if (type == ClickType.QUICK_MOVE) { forget(mover, shown.get(index).stop); }
        return ItemStack.EMPTY;
    }

    private void turn(int slot) {
        if (slot == PREVIOUS && page > 0) { page--; }
        else if (slot == NEXT && page < pages() - 1) { page++; }
        else if (slot == SORT) {
            order = PortwayMenu.after(order);
            PortwayOrder.save(viewer.player, order);
            PortwayOrder.arrange(sorted, order);
            shown = PortwayOrder.matching(sorted, filter);
            page = 0;
        }
        else { return; }
        refill(opening(viewer.player));
        detectAndSendChanges();
    }

    public void filter(String text) { pending = text.length() > FILTER_MAX ? text.substring(0, FILTER_MAX) : text; }

    @Override public void detectAndSendChanges() {
        long now = viewer.player.world.getTotalWorldTime();
        if (!pending.equals(filter) && now - filtered >= 4L) {
            filtered = now;
            filter = pending;
            shown = PortwayOrder.matching(sorted, filter);
            page = 0;
            refill(opening(viewer.player));
        }
        super.detectAndSendChanges();
    }

    private void forget(EntityPlayerMP player, PortwayStop stop) {
        boolean here = origin != null && stop.equals(new PortwayStop(originDimension, origin));
        if (here || !Portways.stops(player).contains(stop)) { return; }
        PortwayTravel.forget(player, stop, "player");
        reload();
        detectAndSendChanges();
    }

    private void choose(EntityPlayerMP player, PortwayStop stop) {
        PortwayStop here = origin == null ? null : new PortwayStop(originDimension, origin);
        if (stop.equals(here)) { return; }
        PortwayDef opening = opening(player);
        if (opening == null) { return; }
        PortwayDef target = stop.def();
        if (target != null && !target.global() && Portways.unknown(player, stop)) {
            reload();
            detectAndSendChanges();
            return;
        }
        ItemStack held = hand == null ? ItemStack.EMPTY : player.getHeldItem(hand);
        player.closeScreen();
        PortwayTravel.Result result = PortwayTravel.travel(player, here, stop, opening);
        if (result == PortwayTravel.Result.ARRIVED && held.getItem() instanceof ContentItemPortway) { ((ContentItemPortway) held.getItem()).used(player, held); }
    }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) { return ItemStack.EMPTY; }
}
