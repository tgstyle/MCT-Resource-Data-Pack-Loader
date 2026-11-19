package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Function;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public final class RdplWorldApi {
    private static final Identifier SCRIPT_KEY = Identifier.fromNamespaceAndPath("resourcedatapackloader", "script");
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
        ResourceHandler<ItemResource> handler = at(x, y, z, Capabilities.Item.BLOCK);
        if (test == null || handler == null) { return 0; }
        int total = 0;
        for (int slot = 0; slot < handler.size(); slot++) { total += count(ItemUtil.getStack(handler, slot), test); }
        return total;
    }

    @Nullable public Object capability(double x, double y, double z, String type) {
        if ("items".equals(type)) { return at(x, y, z, Capabilities.Item.BLOCK); }
        if ("fluids".equals(type)) { return at(x, y, z, Capabilities.Fluid.BLOCK); }
        if ("energy".equals(type)) { return at(x, y, z, Capabilities.Energy.BLOCK); }
        return null;
    }

    public boolean placeStructure(String template, double x, double y, double z) {
        Identifier id = Identifier.tryParse(template);
        var loaded = id == null ? null : LineCompat.templates(world.getServer()).get(id).orElse(null);
        if (loaded == null) { return false; }
        BlockPos origin = BlockPos.containing(x, y, z);
        loaded.placeInWorld(world, origin, origin, new StructurePlaceSettings(), world.getRandom(), Block.UPDATE_ALL);
        return true;
    }

    public boolean summon(String id, double x, double y, double z, String nbt) {
        boolean given = !nbt.trim().isEmpty();
        CompoundTag tag;
        try { tag = given ? TagParser.parseCompoundFully(nbt) : new CompoundTag(); }
        catch (CommandSyntaxException e) { throw new IllegalArgumentException("Bad entity NBT: " + e.getMessage()); }
        tag.putString("id", id);
        Entity entity = EntityType.loadEntityRecursive(TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), tag), world, EntitySpawnReason.COMMAND, created -> {
            created.snapTo(x, y, z, created.getYRot(), created.getXRot());
            return created;
        });
        if (entity == null) { return false; }
        if (!given && entity instanceof Mob mob) { EventHooks.finalizeMobSpawn(mob, world, world.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.COMMAND, null); }
        return world.tryAddFreshEntityWithPassengers(entity);
    }

    @Nullable private <T, C> T at(double x, double y, double z, BlockCapability<T, C> capability) {
        BlockPos pos = BlockPos.containing(x, y, z);
        return world.isLoaded(pos) ? world.getCapability(capability, pos, null) : null;
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
        if (!(stack.getItem() instanceof BlockItem block) || !(block.getBlock() instanceof ShulkerBoxBlock)) { return total; }
        return total + stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItemCopyStream().filter(test).mapToInt(ItemStack::getCount).sum();
    }
}
