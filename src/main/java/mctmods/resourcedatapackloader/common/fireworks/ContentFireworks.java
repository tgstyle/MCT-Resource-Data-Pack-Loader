package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.items.ContentContainerUpgradeItem;
import mctmods.resourcedatapackloader.common.items.ContentDrinkItem;
import mctmods.resourcedatapackloader.common.items.ContentLocatorItem;
import mctmods.resourcedatapackloader.common.items.ContentPortwayItem;
import mctmods.resourcedatapackloader.common.items.ContentRollItem;
import mctmods.resourcedatapackloader.common.items.ContentSleepingBagItem;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ContentFireworks {
    private static final double AIR_REACH = 2.0D;
    private static final double FACE_OFFSET = 0.15D;
    private static final Map<Item, Entry> ENTRIES = new LinkedHashMap<>();

    private ContentFireworks() {}

    private record Entry(FireworkDef def, ItemDef itemDef, int cooldown) {}

    public static void index(Item item, ItemDef itemDef) {
        FireworkDef def = itemDef.firework();
        if (def == null) { return; }
        ENTRIES.put(item, new Entry(def, itemDef, itemDef.cooldown()));
    }

    @Nullable public static FireworkDef def(Item item) {
        Entry entry = ENTRIES.get(item);
        return entry == null ? null : entry.def();
    }

    public static Map<Item, FireworkDef> dispensing() {
        Map<Item, FireworkDef> result = new LinkedHashMap<>();
        for (Map.Entry<Item, Entry> each : ENTRIES.entrySet()) {
            if (each.getValue().def().dispense()) { result.put(each.getKey(), each.getValue().def()); }
        }
        return Collections.unmodifiableMap(result);
    }

    private static boolean hasOwnUse(Item item, ItemDef def) {
        if (item.components().has(DataComponents.FOOD) || item instanceof PotionItem || item instanceof TieredItem || item instanceof ArmorItem || item instanceof BlockItem) { return true; }
        if (item instanceof ContentDrinkItem || item instanceof ContentContainerItem || item instanceof ContentContainerUpgradeItem || item instanceof ContentPortwayItem) { return true; }
        if (item instanceof ContentRollItem || item instanceof ContentLocatorItem || item instanceof ContentSleepingBagItem) { return true; }
        return def.weapon() != null;
    }

    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        Entry entry = fires(event.getEntity(), event.getItemStack());
        if (entry == null || !entry.def().onBlock()) { return; }
        Player player = event.getEntity();
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (event.getLevel().isClientSide) { return; }
        if (!player.isShiftKeyDown()) {
            InteractionResult used = blockUse(event, player);
            if (used.consumesAction()) {
                event.setCancellationResult(used);
                return;
            }
        }
        launch(entry, player, event.getItemStack(), blockPoint(event.getHitVec().getLocation(), event.getPos(), event.getFace()));
    }

    private static InteractionResult blockUse(PlayerInteractEvent.RightClickBlock event, Player player) {
        BlockState state = event.getLevel().getBlockState(event.getPos());
        ItemInteractionResult withItem = state.useItemOn(event.getItemStack(), event.getLevel(), player, event.getHand(), event.getHitVec());
        if (withItem != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) { return withItem.result(); }
        return state.useWithoutItem(event.getLevel(), player, event.getHitVec());
    }

    public static void onUseAir(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Entry entry = fires(player, event.getItemStack());
        if (entry == null || !entry.def().onAir()) { return; }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(AIR_REACH));
        BlockHitResult hit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 at = hit.getType() == HitResult.Type.BLOCK ? blockPoint(hit.getLocation(), hit.getBlockPos(), hit.getDirection()) : end;
        launch(entry, player, event.getItemStack(), at);
    }

    @Nullable private static Entry fires(Player player, ItemStack held) {
        if (held.isEmpty()) { return null; }
        Entry entry = ENTRIES.get(held.getItem());
        if (entry == null || player.getCooldowns().isOnCooldown(held.getItem())) { return null; }
        if (hasOwnUse(held.getItem(), entry.itemDef()) && !entry.def().replaceUse() && !player.isShiftKeyDown()) { return null; }
        return entry;
    }

    private static Vec3 blockPoint(@Nullable Vec3 hit, BlockPos pos, @Nullable Direction face) {
        Direction side = face == null ? Direction.UP : face;
        Vec3 base = hit != null ? hit : new Vec3(pos.getX() + 0.5D + side.getStepX() * 0.5D, pos.getY() + 0.5D + side.getStepY() * 0.5D, pos.getZ() + 0.5D + side.getStepZ() * 0.5D);
        return base.add(side.getStepX() * FACE_OFFSET, side.getStepY() * FACE_OFFSET, side.getStepZ() * FACE_OFFSET);
    }

    private static void launch(Entry entry, Player player, ItemStack held, Vec3 at) {
        if (player.level().isClientSide) { return; }
        Item item = held.getItem();
        FireworkRockets.spawn(player.level(), at.x, at.y, at.z, FireworkRockets.stack(item, entry.def()), entry.def().burstMode());
        if (entry.def().consume() && !player.getAbilities().instabuild) { held.shrink(1); }
        if (entry.cooldown() > 0) { player.getCooldowns().addCooldown(item, entry.cooldown()); }
    }
}
