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
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class ContentFireworks {
    private static final double AIR_REACH = 2.0D;
    private static final Map<Item, Entry> ENTRIES = new LinkedHashMap<>();

    private ContentFireworks() {}

    private record Entry(FireworkDef def, boolean ownUse, int cooldown) {}

    public static void index(Item item, ItemDef itemDef) {
        FireworkDef def = itemDef.firework();
        if (def == null) { return; }
        ENTRIES.put(item, new Entry(def, hasOwnUse(item, itemDef), itemDef.cooldown()));
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
        if (item.isEdible() || item instanceof PotionItem || item instanceof TieredItem || item instanceof ArmorItem || item instanceof BlockItem) { return true; }
        if (item instanceof ContentDrinkItem || item instanceof ContentContainerItem || item instanceof ContentContainerUpgradeItem || item instanceof ContentPortwayItem) { return true; }
        if (item instanceof ContentRollItem || item instanceof ContentLocatorItem || item instanceof ContentSleepingBagItem) { return true; }
        return def.weapon() != null;
    }

    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        Entry entry = fires(event.getEntity(), event.getItemStack());
        if (entry == null || !entry.def().onBlock()) { return; }
        Player player = event.getEntity();
        if (!player.isShiftKeyDown() && !player.level().isClientSide) {
            BlockState state = player.level().getBlockState(event.getPos());
            InteractionResult used = state.use(player.level(), player, event.getHand(), event.getHitVec());
            if (used.consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(used);
                return;
            }
        }
        launch(event, entry, player, event.getItemStack(), facePoint(event.getHitVec().getLocation(), event.getHitVec().getDirection()));
    }

    public static void onUseAir(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Entry entry = fires(player, event.getItemStack());
        if (entry == null || !entry.def().onAir()) { return; }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(AIR_REACH));
        BlockHitResult hit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 at = hit.getType() == HitResult.Type.BLOCK ? facePoint(hit.getLocation(), hit.getDirection()) : end;
        launch(event, entry, player, event.getItemStack(), at);
    }

    @Nullable private static Entry fires(Player player, ItemStack held) {
        if (held.isEmpty()) { return null; }
        Entry entry = ENTRIES.get(held.getItem());
        if (entry == null || player.getCooldowns().isOnCooldown(held.getItem())) { return null; }
        if (entry.ownUse() && !entry.def().replaceUse() && !player.isShiftKeyDown()) { return null; }
        return entry;
    }

    private static Vec3 facePoint(Vec3 hit, Direction face) {
        return new Vec3(hit.x + face.getStepX() * 0.15D, hit.y + face.getStepY() * 0.15D, hit.z + face.getStepZ() * 0.15D);
    }

    private static void launch(PlayerInteractEvent event, Entry entry, Player player, ItemStack held, Vec3 at) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (player.level().isClientSide) { return; }
        Item item = held.getItem();
        FireworkRockets.spawn(player.level(), at.x, at.y, at.z, FireworkRockets.stack(item, entry.def()), FireworkDef.MODE_BURST.equals(entry.def().mode()));
        if (entry.def().consume() && !player.getAbilities().instabuild) { held.shrink(1); }
        if (entry.cooldown() > 0) { player.getCooldowns().addCooldown(item, entry.cooldown()); }
    }
}
