package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.ItemHandlers;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public final class RdplWorldApi {
    private static final ResourceLocation SCRIPT_KEY = ResourceLocation.fromNamespaceAndPath("resourcedatapackloader", "script");
    private final ServerLevel world;

    public RdplWorldApi(ServerLevel world) { this.world = world; }

    public int scan(Player player, Object match) {
        Predicate<ItemStack> test = predicate(match);
        if (test == null) { return 0; }
        int total = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) { total += count(player.getInventory().getItem(slot), test); }
        return total;
    }

    public int scanAt(double x, double y, double z, Object match) {
        Predicate<ItemStack> test = predicate(match);
        IItemHandler handler = items(x, y, z);
        if (test == null || handler == null) { return 0; }
        return (int) ItemHandlers.sum(handler, stack -> count(stack, test));
    }

    @Nullable public Object capability(double x, double y, double z, String type) {
        if ("items".equals(type)) { return items(x, y, z); }
        if ("fluids".equals(type)) { return at(x, y, z, Capabilities.FluidHandler.BLOCK); }
        if ("energy".equals(type)) { return at(x, y, z, Capabilities.EnergyStorage.BLOCK); }
        return null;
    }

    public boolean placeStructure(String template, double x, double y, double z) {
        ResourceLocation named = ResourceLocation.tryParse(template);
        Optional<StructureTemplate> loaded = named == null ? Optional.empty() : world.getStructureManager().get(named);
        if (loaded.isEmpty()) { return false; }
        BlockPos origin = BlockPos.containing(x, y, z);
        loaded.get().placeInWorld(world, origin, origin, new StructurePlaceSettings(), world.getRandom(), Block.UPDATE_ALL);
        return true;
    }

    public boolean summon(String id, double x, double y, double z, String nbt) {
        boolean given = !nbt.trim().isEmpty();
        CompoundTag tag;
        try { tag = given ? TagParser.parseTag(nbt) : new CompoundTag(); }
        catch (CommandSyntaxException e) { throw new IllegalArgumentException("Bad entity NBT: " + e.getMessage()); }
        tag.putString("id", id);
        Entity entity = EntityType.loadEntityRecursive(tag, world, created -> {
            created.moveTo(x, y, z, created.getYRot(), created.getXRot());
            return created;
        });
        if (entity == null) { return false; }
        if (!given && entity instanceof Mob mob) { EventHooks.finalizeMobSpawn(mob, world, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null); }
        return world.tryAddFreshEntityWithPassengers(entity);
    }

    @Nullable private BlockEntity entity(double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        return world.isLoaded(pos) ? world.getBlockEntity(pos) : null;
    }

    @Nullable private <T> T at(double x, double y, double z, BlockCapability<T, @org.jetbrains.annotations.Nullable Direction> capability) {
        BlockEntity entity = entity(x, y, z);
        if (entity == null) { return null; }
        BlockPos pos = entity.getBlockPos();
        T handler = world.getCapability(capability, pos, entity.getBlockState(), entity, null);
        if (handler != null) { return handler; }
        for (Direction side : Direction.values()) {
            handler = world.getCapability(capability, pos, entity.getBlockState(), entity, side);
            if (handler != null) { return handler; }
        }
        return null;
    }

    @Nullable private IItemHandler items(double x, double y, double z) {
        IItemHandler handler = at(x, y, z, Capabilities.ItemHandler.BLOCK);
        BlockEntity entity = entity(x, y, z);
        return handler == null && entity instanceof Container container ? new InvWrapper(container) : handler;
    }

    @Nullable private static Predicate<ItemStack> predicate(Object match) {
        if (match instanceof Function function) { return stack -> Context.toBoolean(function.call(Context.getCurrentContext(), function.getParentScope(), function.getParentScope(), new Object[] {stack})); }
        if (!(match instanceof CharSequence)) { throw new IllegalArgumentException("match must be an item string or a function"); }
        ItemMatch parsed = ItemMatch.parse(SCRIPT_KEY, match.toString());
        return parsed == null ? null : parsed::matches;
    }

    private static int count(ItemStack stack, Predicate<ItemStack> test) {
        if (stack.isEmpty()) { return 0; }
        int total = test.test(stack) ? stack.getCount() : 0;
        if (!(stack.getItem() instanceof BlockItem held) || !(held.getBlock() instanceof ShulkerBoxBlock)) { return total; }
        for (ItemStack inside : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
            if (test.test(inside)) { total += inside.getCount(); }
        }
        return total;
    }
}
