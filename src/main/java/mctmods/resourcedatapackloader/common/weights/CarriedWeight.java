package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

public final class CarriedWeight {
    private static final int DEPTH = 3;
    private static final String ITEMS = "Items";

    private CarriedWeight() {}

    private record Scope(WeightDef settings, HolderLookup.Provider access) {}

    public static List<ItemStack> worn(Player player) {
        List<ItemStack> found = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) { continue; }
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(ContentCurios.worn(player, _ -> true));
        return found;
    }

    public static long total(Player player, WeightDef settings, List<ItemStack> worn) {
        Scope scope = new Scope(settings, player.registryAccess());
        long sum = 0L;
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) { sum += weigh(stack, scope, 0); }
        sum += weigh(player.getOffhandItem(), scope, 0);
        sum += weigh(player.containerMenu.getCarried(), scope, 0);
        sum += weigh(ContentCarry.carried(player), scope, 0);
        long wornSum = 0L;
        for (ItemStack stack : worn) { wornSum += weigh(stack, scope, 0); }
        return sum + Math.round(wornSum * settings.wornFactor);
    }

    public static long single(ItemStack stack, WeightDef settings, HolderLookup.Provider access) { return stack.isEmpty() ? 0L : ContentWeights.of(stack) + held(stack, new Scope(settings, access), 0); }

    private static long weigh(ItemStack stack, Scope scope, int depth) {
        if (stack.isEmpty()) { return 0L; }
        return ((long) ContentWeights.of(stack) + held(stack, scope, depth)) * stack.getCount();
    }

    private static long held(ItemStack stack, Scope scope, int depth) { return depth < DEPTH ? Math.round(contents(stack, scope, depth) * scope.settings.containerFactor) : 0L; }

    private static long contents(ItemStack stack, Scope scope, int depth) {
        if (WeightDef.CONTENTS_NONE.equals(scope.settings.countContents)) { return 0L; }
        if (stack.getItem() instanceof ContentContainerItem) { return listed(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY), scope, depth); }
        if (!WeightDef.CONTENTS_ALL.equals(scope.settings.countContents)) { return 0L; }
        ResourceHandler<ItemResource> handler = ItemAccess.forHandlerIndex(VanillaContainerWrapper.of(new SimpleContainer(stack.copyWithCount(1))), 0).getCapability(Capabilities.Item.ITEM);
        if (handler != null) {
            long sum = 0L;
            for (int slot = 0; slot < handler.size(); slot++) { sum += weigh(ItemUtil.getStack(handler, slot), scope, depth + 1); }
            return sum;
        }
        long sum = listed(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY), scope, depth);
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) { sum += LineCompat.bundled(bundle).mapToLong(held -> weigh(held, scope, depth + 1)).sum(); }
        TypedEntityData<BlockEntityType<?>> tile = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (tile != null) {
            for (Tag tag : tile.copyTagWithoutId().getListOrEmpty(ITEMS)) { sum += weigh(ItemStack.CODEC.parse(scope.access.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY), scope, depth + 1); }
        }
        return sum;
    }

    private static long listed(ItemContainerContents contents, Scope scope, int depth) { return contents.nonEmptyItemCopyStream().mapToLong(held -> weigh(held, scope, depth + 1)).sum(); }
}
