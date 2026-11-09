package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.items.ContentItem;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class ContentFireworks {
    private static final double AIR_REACH = 2.0D;
    private static final Map<Item, Entry> ENTRIES = new LinkedHashMap<>();

    private ContentFireworks() {}

    private static final class Entry {
        final FireworkDef def;
        final boolean ownUse;
        final int cooldown;

        Entry(FireworkDef def, boolean ownUse, int cooldown) {
            this.def = def;
            this.ownUse = ownUse;
            this.cooldown = cooldown;
        }
    }

    public static void index(Item item, FireworkDef def) {
        ItemDef itemDef = item.getRegistryName() == null ? null : ContentRegistry.itemDef(item.getRegistryName());
        ENTRIES.put(item, new Entry(def, hasOwnUse(item, itemDef), itemDef == null ? 0 : itemDef.cooldown));
    }

    @Nullable public static FireworkDef def(Item item) {
        Entry entry = ENTRIES.get(item);
        return entry == null ? null : entry.def;
    }

    public static Map<Item, FireworkDef> dispensing() {
        Map<Item, FireworkDef> result = new LinkedHashMap<>();
        for (Map.Entry<Item, Entry> each : ENTRIES.entrySet()) {
            if (each.getValue().def.dispense) { result.put(each.getKey(), each.getValue().def); }
        }
        return Collections.unmodifiableMap(result);
    }

    private static boolean hasOwnUse(Item item, @Nullable ItemDef def) {
        if (item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemSeeds || item instanceof ItemBlock) { return true; }
        if (item instanceof ContentItem && item.getClass() != ContentItem.class) { return true; }
        if (def == null) { return false; }
        return def.weapon != null || "drink".equals(def.type) || "potion".equals(def.type) || def.sleepingBag || def.passesTurn || !def.locates.isEmpty() || !def.rolls.isEmpty();
    }

    @SubscribeEvent public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        Entry entry = fires(player, event.getItemStack());
        if (entry == null || !entry.def.onBlock()) { return; }
        Vec3d at = blockPoint(event.getHitVec(), event.getPos(), event.getFace());
        if (!player.isSneaking() && !player.world.isRemote && activates(event, at)) {
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            return;
        }
        launch(event, entry, player, event.getItemStack(), at);
    }

    @SubscribeEvent public static void onUseAir(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        Entry entry = fires(player, event.getItemStack());
        if (entry == null || !entry.def.onAir()) { return; }
        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d end = eye.add(player.getLookVec().scale(AIR_REACH));
        RayTraceResult hit = player.world.rayTraceBlocks(eye, end, false, true, false);
        Vec3d at = end;
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            EnumFacing side = hit.sideHit;
            at = hit.hitVec.add(side.getXOffset() * 0.15D, side.getYOffset() * 0.15D, side.getZOffset() * 0.15D);
        }
        launch(event, entry, player, event.getItemStack(), at);
    }

    private static boolean activates(PlayerInteractEvent.RightClickBlock event, Vec3d at) {
        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        EnumFacing face = event.getFace() == null ? EnumFacing.UP : event.getFace();
        IBlockState state = player.world.getBlockState(pos);
        return state.getBlock().onBlockActivated(player.world, pos, state, player, event.getHand(), face, (float) (at.x - pos.getX()), (float) (at.y - pos.getY()), (float) (at.z - pos.getZ()));
    }

    @Nullable private static Entry fires(EntityPlayer player, ItemStack held) {
        if (held.isEmpty()) { return null; }
        Entry entry = ENTRIES.get(held.getItem());
        if (entry == null || player.getCooldownTracker().hasCooldown(held.getItem())) { return null; }
        if (entry.ownUse && !entry.def.replaceUse && !player.isSneaking()) { return null; }
        return entry;
    }

    private static Vec3d blockPoint(@Nullable Vec3d hit, BlockPos pos, @Nullable EnumFacing face) {
        if (hit != null) { return hit; }
        EnumFacing side = face == null ? EnumFacing.UP : face;
        return new Vec3d(pos.getX() + 0.5D + side.getXOffset() * 0.5D, pos.getY() + 0.5D + side.getYOffset() * 0.5D, pos.getZ() + 0.5D + side.getZOffset() * 0.5D);
    }

    private static void launch(PlayerInteractEvent event, Entry entry, EntityPlayer player, ItemStack held, Vec3d at) {
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (player.world.isRemote) { return; }
        Item item = held.getItem();
        ItemStack rocket = FireworkRockets.stack(item, entry.def);
        FireworkRockets.spawn(player.world, at.x, at.y, at.z, rocket, FireworkDef.MODE_BURST.equals(entry.def.mode));
        if (entry.def.consume && !player.capabilities.isCreativeMode) { held.shrink(1); }
        if (entry.cooldown > 0) { player.getCooldownTracker().setCooldown(item, entry.cooldown); }
    }
}
