package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.blocks.portal.logic.ContentPortwayBlockEntity;
import mctmods.resourcedatapackloader.common.cards.CardRule;
import mctmods.resourcedatapackloader.common.containers.ContentContainers;
import mctmods.resourcedatapackloader.common.items.ContentPortwayItem;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayMenu;
import mctmods.resourcedatapackloader.common.portways.PortwayCosts;
import mctmods.resourcedatapackloader.common.portways.PortwayFx;
import mctmods.resourcedatapackloader.common.portways.PortwayOrder;
import mctmods.resourcedatapackloader.common.portways.PortwayStop;
import mctmods.resourcedatapackloader.common.portways.PortwayTravel;
import mctmods.resourcedatapackloader.common.portways.Portways;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.text.Lang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentPortwayMenu extends AbstractContainerMenu {
    public static final int ROWS = 6;
    public static final int COLUMNS = 9;
    public static final int FILTER_MAX = 32;
    public static final ContainerDef SHAPE = new ContainerDef(ROWS, COLUMNS, "", false, null, null, 0, 0);
    private static final int SLOTS = ROWS * COLUMNS;
    private static final int PAGED = SLOTS - COLUMNS;
    private static final int TOP = 18;
    private static final double REACH = 64.0D;
    private static final String NAV = "rdplNav";
    @Nullable private final PortwayStop origin;
    private final PortwayDef opening;
    @Nullable private final InteractionHand hand;
    @Nullable private final PortwayItemDef spend;
    @Nullable private final Item openedWith;
    private final ResourceKey<Level> from;
    private final SimpleContainer icons = new SimpleContainer(SLOTS);
    private final Player viewer;
    private List<PortwayOrder.Entry> sorted = List.of();
    private List<PortwayOrder.Entry> visible = List.of();
    private Set<PortwayStop.Key> mine = Set.of();
    private String order = "";
    private String filter = "";
    private String pending = "";
    private long filtered = -4L;
    private int page;

    public ContentPortwayMenu(int id, Inventory inventory) { this(id, inventory, null, PortwayDef.PLAIN, null, null); }

    public ContentPortwayMenu(int id, Inventory inventory, ServerLevel level, BlockPos pos) { this(id, inventory, Portways.snapshot(level, pos), ((ContentPortwayBlock) level.getBlockState(pos).getBlock()).settings(), null, null); }

    public ContentPortwayMenu(int id, Inventory inventory, PortwayItemDef item, InteractionHand hand) { this(id, inventory, null, item.settings(), hand, item); }

    private ContentPortwayMenu(int id, Inventory inventory, @Nullable PortwayStop origin, PortwayDef opening, @Nullable InteractionHand hand, @Nullable PortwayItemDef spend) {
        super(ContentContainers.PORTWAY_MENU.get(), id);
        this.viewer = inventory.player;
        this.origin = origin;
        this.opening = opening;
        this.hand = hand;
        this.spend = spend;
        this.openedWith = hand == null ? null : inventory.player.getItemInHand(hand).getItem();
        this.from = origin != null ? origin.dimension() : inventory.player.level().dimension();
        int wide = ContentContainerMenu.width(SHAPE);
        int left = (wide - COLUMNS * ContentContainerMenu.SLOT) / 2 + 1;
        for (int slot = 0; slot < SLOTS; slot++) {
            addSlot(new Slot(icons, slot, left + slot % COLUMNS * ContentContainerMenu.SLOT, TOP + slot / COLUMNS * ContentContainerMenu.SLOT) {
                @Override public boolean mayPlace(@Nonnull ItemStack stack) { return false; }

                @Override public boolean mayPickup(@Nonnull Player player) { return false; }
            });
        }
        for (Slot slot : ContentContainerMenu.playerSlots(inventory, wide, ContentContainerMenu.height(SHAPE), Slot::new)) { addSlot(slot); }
        if (inventory.player instanceof ServerPlayer server && (origin != null || hand != null)) { load(server); }
    }

    private int pages() { return Math.max(1, (visible.size() + PAGED - 1) / PAGED); }

    private void load(ServerPlayer player) {
        PortwayTravel.prune(player);
        if (order.isEmpty()) { order = PortwayOrder.saved(player, opening.menu().order()); }
        sorted = PortwayOrder.entries(player, Portways.listed(player, opening, from));
        PortwayOrder.arrange(sorted, order);
        mine = new HashSet<>();
        for (PortwayStop stop : Portways.stops(player)) { mine.add(stop.key()); }
        visible = PortwayOrder.matching(sorted, filter);
        fill(player);
    }

    private void fill(ServerPlayer player) {
        icons.clearContent();
        page = Math.max(0, Math.min(page, pages() - 1));
        PortwayMenu menu = opening.menu();
        ItemStack locked = PortwayFx.icon(menu.locked(), PortwayMenu.PLAIN.locked());
        for (int slot = 0; slot < PAGED && page * PAGED + slot < visible.size(); slot++) { icons.setItem(slot, describe(player, visible.get(page * PAGED + slot), locked)); }
        if (page > 0) { icons.setItem(menu.previousAt(), nav(PortwayFx.icon(menu.previous(), PortwayMenu.PLAIN.previous()), "previous", Component.translatable("rdpl.portway.previous"))); }
        icons.setItem(menu.sortAt(), sorter(PortwayFx.icon(menu.sort(), PortwayMenu.PLAIN.sort())));
        icons.setItem(menu.pageAt(), nav(PortwayFx.icon(menu.page(), PortwayMenu.PLAIN.page()), "page", Component.translatable("rdpl.portway.page", page + 1, pages())));
        if (page < pages() - 1) { icons.setItem(menu.nextAt(), nav(PortwayFx.icon(menu.next(), PortwayMenu.PLAIN.next()), "next", Component.translatable("rdpl.portway.next"))); }
        ContentPortwayBlockEntity held = scoper();
        if (held != null) { icons.setItem(menu.scopeAt(), scope(PortwayFx.icon(menu.scope(), PortwayMenu.PLAIN.scope()), held.global())); }
    }

    @Nullable private ContentPortwayBlockEntity scoper() {
        if (!opening.toggle() || origin == null) { return null; }
        return viewer.level().getBlockEntity(origin.pos()) instanceof ContentPortwayBlockEntity held && held.mayToggle(viewer) ? held : null;
    }

    private ItemStack scope(ItemStack icon, boolean global) {
        ItemStack button = nav(icon, "scope", Component.translatable("rdpl.portway.scope", Component.translatable("rdpl.portway.scope." + (global ? GateDef.GLOBAL : opening.scope()))));
        lore(button, List.of(Component.translatable("rdpl.portway.scope.click", Component.translatable("rdpl.portway.scope." + (global ? opening.scope() : GateDef.GLOBAL)))));
        return button;
    }

    private void switchScope(ServerPlayer player) {
        ContentPortwayBlockEntity held = scoper();
        if (held == null) {
            ContentLog.LOGGER.info("Portway scope click by {} refused", player.getGameProfile().getName());
            return;
        }
        boolean now = !held.global();
        held.setGlobal(now);
        Portways.publish(player.serverLevel(), held.getBlockPos());
        PortwayFx.activated(player, "rdpl.portway.scope.changed", Lang.tr(player, "rdpl.portway.scope." + (now ? GateDef.GLOBAL : opening.scope())));
        load(player);
        broadcastChanges();
    }

    private static ItemStack nav(ItemStack icon, String kind, Component name) {
        icon.setHoverName(name.copy().withStyle(style -> style.withItalic(false)));
        icon.getOrCreateTag().putString(NAV, kind);
        return icon;
    }

    private ItemStack sorter(ItemStack icon) {
        ItemStack button = nav(icon, "sort", Component.translatable("rdpl.portway.sort", Component.translatable("rdpl.portway.order." + order)));
        lore(button, List.of(Component.translatable("rdpl.portway.sort.click", Component.translatable("rdpl.portway.order." + PortwayMenu.after(order)))));
        return button;
    }

    private static void lore(ItemStack stack, List<Component> notes) {
        ListTag lore = new ListTag();
        for (Component note : notes) { lore.add(StringTag.valueOf(Component.Serializer.toJson(note.copy().withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false))))); }
        stack.getOrCreateTagElement("display").put("Lore", lore);
    }

    private static Item iconOf(PortwayStop stop) {
        ResourceLocation id = ResourceLocation.tryParse(stop.block());
        Item item = id == null ? Items.AIR : ForgeRegistries.BLOCKS.getHolder(id).map(holder -> holder.value().asItem()).orElse(Items.AIR);
        return item == Items.AIR ? Items.ENDER_PEARL : item;
    }

    private ItemStack describe(ServerPlayer player, PortwayOrder.Entry entry, ItemStack lockedIcon) {
        PortwayStop stop = entry.stop;
        String label = entry.label;
        boolean locked = PortwayTravel.lockOf(player, stop.def(), stop.dimension(), from, false) != null;
        ItemStack shown = locked ? lockedIcon.copy() : new ItemStack(iconOf(stop));
        Component title = label.isEmpty() ? Component.translatable("rdpl.portway.stop", Portways.dimensionName(stop.dimension()), stop.pos().getX(), stop.pos().getY(), stop.pos().getZ()) : Component.literal(label);
        shown.setHoverName(title.copy().withStyle(style -> style.withItalic(false)));
        List<Component> notes = new ArrayList<>();
        boolean here = origin != null && stop.same(origin);
        if (locked) {
            shown.getOrCreateTag().putBoolean("locked", true);
            notes.add(Component.translatable("rdpl.portway.locked"));
        }
        else if (here) { notes.add(Component.translatable("rdpl.portway.here")); }
        else {
            boolean cross = !stop.dimension().equals(from);
            double distance = Portways.distance(player, stop, from);
            if (!cross) { notes.add(Component.translatable((int) distance == 1 ? "rdpl.portway.distance.one" : "rdpl.portway.distance", (int) distance)); }
            notes.addAll(PortwayCosts.lines(opening.cost(), distance, cross));
            int waiting = PortwayCosts.waiting(player);
            if (waiting > 0) { notes.add(Component.translatable(waiting == 1 ? "rdpl.portway.cooldown.one" : "rdpl.portway.cooldown", waiting)); }
        }
        if (!here && mine.contains(stop.key())) { notes.add(Component.translatable("rdpl.portway.forgethint")); }
        if (!notes.isEmpty()) { lore(shown, notes); }
        return shown;
    }

    @Override public boolean stillValid(@Nonnull Player player) {
        if (hand != null) { return player.getItemInHand(hand).getItem() == openedWith; }
        return origin == null || player.level().dimension().equals(origin.dimension()) && player.distanceToSqr(origin.pos().getCenter()) <= REACH && player.level().getBlockState(origin.pos()).getBlock() instanceof ContentPortwayBlock;
    }

    @Override public void clicked(int slot, int button, @Nonnull ClickType type, @Nonnull Player player) {
        if (!(player instanceof ServerPlayer server) || slot < 0 || slot >= SLOTS) { return; }
        if (!stillValid(server)) {
            if (origin == null) {
                PortwayFx.card(server, CardRule.PORTWAY_REFUSED, "", "", "item");
                server.closeContainer();
            }
            return;
        }
        if (slot >= PAGED) {
            if (type == ClickType.PICKUP) { turn(server, slot); }
            return;
        }
        int index = page * PAGED + slot;
        if (index >= visible.size()) { return; }
        if (type == ClickType.PICKUP) { choose(server, visible.get(index).stop); }
        else if (type == ClickType.QUICK_MOVE) { discard(server, visible.get(index).stop); }
    }

    private void turn(ServerPlayer player, int slot) {
        PortwayMenu menu = opening.menu();
        if (slot == menu.scopeAt() && opening.toggle()) {
            switchScope(player);
            return;
        }
        if (slot == menu.previousAt() && page > 0) { page--; }
        else if (slot == menu.nextAt() && page < pages() - 1) { page++; }
        else if (slot == menu.sortAt()) {
            order = PortwayMenu.after(order);
            PortwayOrder.save(player, order);
            PortwayOrder.arrange(sorted, order);
            visible = PortwayOrder.matching(sorted, filter);
            page = 0;
        }
        else { return; }
        fill(player);
        broadcastChanges();
    }

    public void filter(String text) { pending = text.length() > FILTER_MAX ? text.substring(0, FILTER_MAX) : text; }

    @Override public void broadcastChanges() {
        if (viewer instanceof ServerPlayer server && !pending.equals(filter)) {
            long now = server.level().getGameTime();
            if (now - filtered >= 4L) {
                filtered = now;
                filter = pending;
                visible = PortwayOrder.matching(sorted, filter);
                page = 0;
                fill(server);
            }
        }
        super.broadcastChanges();
    }

    private void discard(ServerPlayer player, PortwayStop stop) {
        if (origin != null && stop.same(origin) || Portways.stops(player).stream().noneMatch(stop::same)) { return; }
        PortwayTravel.forget(player, stop, "player");
        load(player);
        broadcastChanges();
    }

    private void choose(ServerPlayer player, PortwayStop stop) {
        if (origin != null && stop.same(origin)) { return; }
        PortwayDef target = stop.def();
        if (target != null && !target.global() && Portways.unknown(player, stop)) {
            load(player);
            broadcastChanges();
            return;
        }
        player.closeContainer();
        if (PortwayTravel.travel(player, origin, stop, opening) == PortwayTravel.Result.ARRIVED && hand != null && spend != null) { ContentPortwayItem.spent(player, hand, spend); }
    }

    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull Player player, int index) { return ItemStack.EMPTY; }
}
