package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.ItemHandlers;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.rhino.Context;
import mctmods.resourcedatapackloader.core.rhino.Function;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public final class RdplWorldApi {
    private static final ResourceLocation SCRIPT_KEY = new ResourceLocation("resourcedatapackloader", "script");
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String ITEMS = "Items";
    private final WorldServer world;

    public RdplWorldApi(WorldServer world) { this.world = world; }

    public int scan(EntityPlayer player, Object match) {
        Predicate<ItemStack> test = predicate(match);
        if (test == null) { return 0; }
        int total = 0;
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) { total += count(player.inventory.getStackInSlot(slot), test); }
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
        if ("fluids".equals(type)) { return at(x, y, z, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY); }
        if ("energy".equals(type)) { return at(x, y, z, CapabilityEnergy.ENERGY); }
        return null;
    }

    public boolean placeStructure(String template, double x, double y, double z) {
        Template loaded = world.getStructureTemplateManager().get(world.getMinecraftServer(), new ResourceLocation(template));
        if (loaded == null) { return false; }
        loaded.addBlocksToWorld(world, new BlockPos(x, y, z), new PlacementSettings());
        return true;
    }

    public boolean summon(String id, double x, double y, double z, String nbt) {
        NBTTagCompound tag;
        try { tag = nbt.trim().isEmpty() ? new NBTTagCompound() : JsonToNBT.getTagFromJson(nbt); }
        catch (NBTException e) { throw new IllegalArgumentException("Bad entity NBT: " + e.getMessage()); }
        boolean given = !nbt.trim().isEmpty();
        tag.setString("id", id);
        Entity entity = AnvilChunkLoader.readWorldEntityPos(tag, world, x, y, z, true);
        if (entity == null) { return false; }
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        if (!given && entity instanceof EntityLiving) { ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null); }
        return true;
    }

    @Nullable private TileEntity entity(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        return world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
    }

    @Nullable private <T> T at(double x, double y, double z, Capability<T> capability) {
        TileEntity entity = entity(x, y, z);
        if (entity == null) { return null; }
        T handler = entity.getCapability(capability, null);
        if (handler != null) { return handler; }
        for (EnumFacing side : EnumFacing.values()) {
            handler = entity.getCapability(capability, side);
            if (handler != null) { return handler; }
        }
        return null;
    }

    @Nullable private IItemHandler items(double x, double y, double z) {
        IItemHandler handler = at(x, y, z, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        TileEntity entity = entity(x, y, z);
        return handler == null && entity instanceof IInventory ? new InvWrapper((IInventory) entity) : handler;
    }

    @Nullable private static Predicate<ItemStack> predicate(Object match) {
        if (match instanceof Function) {
            Function function = (Function) match;
            return stack -> Context.toBoolean(function.call(Context.getCurrentContext(), function.getParentScope(), function.getParentScope(), new Object[] {stack}));
        }
        if (!(match instanceof CharSequence)) { throw new IllegalArgumentException("match must be an item string or a function"); }
        ItemMatch parsed = ItemMatch.parse(SCRIPT_KEY, match.toString());
        return parsed == null ? null : parsed::matches;
    }

    private static int count(ItemStack stack, Predicate<ItemStack> test) {
        if (stack.isEmpty()) { return 0; }
        int total = test.test(stack) ? stack.getCount() : 0;
        if (!(Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox) || !stack.hasTagCompound()) { return total; }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(BLOCK_ENTITY_TAG, Constants.NBT.TAG_COMPOUND)) { return total; }
        NBTTagCompound entity = tag.getCompoundTag(BLOCK_ENTITY_TAG);
        if (!entity.hasKey(ITEMS, Constants.NBT.TAG_LIST)) { return total; }
        NBTTagList items = entity.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            ItemStack inside = new ItemStack(items.getCompoundTagAt(i));
            if (!inside.isEmpty() && test.test(inside)) { total += inside.getCount(); }
        }
        return total;
    }
}
