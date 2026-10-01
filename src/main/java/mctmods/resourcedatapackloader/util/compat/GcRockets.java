package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.RocketDef;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;
import mctmods.resourcedatapackloader.content.entity.RocketCargo;
import mctmods.resourcedatapackloader.content.interfaces.IPackRocket;
import mctmods.resourcedatapackloader.content.item.ContentItemRocket;
import mctmods.resourcedatapackloader.util.ContentLog;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GCFluids;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import java.util.List;
import javax.annotation.Nullable;

public final class GcRockets {
    private static final String CARGO = "Items";
    private static final String FUEL = "RocketFuel";
    private static final int CARGO_LIST = 9;
    private static final float ON_PAD = 0.4F;

    private GcRockets() {}

    public static int tier(Entity rocket, int base) {
        RocketDef def = RocketCargo.def(rocket);
        return def == null || def.tier < 0 ? base : def.tier;
    }

    public static int fuelTank(Entity rocket, int base) {
        RocketDef def = RocketCargo.def(rocket);
        return def == null || def.fuelTank < 0 ? base : def.fuelTank;
    }

    public static ItemStack picked(Entity rocket) {
        Item item = item(rocket);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static List<ItemStack> dropped(Entity rocket, List<ItemStack> drops) {
        EntityTieredRocket flown = (EntityTieredRocket) rocket;
        for (int slot = 0; slot < flown.getSizeInventory(); slot++) {
            ItemStack stack = flown.getStackInSlot(slot);
            if (!stack.isEmpty()) { drops.add(stack); }
        }
        drops.addAll(EntityStorage.taken(rocket));
        Item item = item(rocket);
        if (item == null) { return drops; }
        ItemStack packed = carrying(item, NonNullList.create());
        NBTTagCompound tag = packed.getTagCompound();
        if (tag != null) { tag.setInteger(FUEL, flown.fuelTank.getFluidAmount()); }
        drops.add(packed);
        return drops;
    }

    @SuppressWarnings("unused") public static void teleported(Entity rocket, EntityPlayerMP player) {
        Item item = item(rocket);
        EntityPlayerMP rider = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        if (item == null || rider == null) { return; }
        GCPlayerStats stats = GCPlayerStats.get(rider);
        NonNullList<ItemStack> held = stats.getRocketStacks();
        if (held == null || held.isEmpty()) { return; }
        held.set(held.size() - 1, carrying(item, NonNullList.create()));
        stats.setRocketItem(item);
        stats.setRocketType(0);
    }

    public static EnumActionResult place(ItemStack stack, World world, BlockPos pos, boolean keeps, ResourceLocation variant) {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                BlockPos pad = pos.add(x, 0, z);
                IBlockState state = world.getBlockState(pad);
                if (state.getBlock() != GCBlocks.landingPadFull || state.getBlock().getMetaFromState(state) != 0) { continue; }
                if (!placed(stack, world, pad, variant)) { return EnumActionResult.FAIL; }
                if (!keeps) { stack.shrink(1); }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    private static boolean placed(ItemStack stack, World world, BlockPos pad, ResourceLocation variant) {
        TileEntity tile = world.getTileEntity(pad);
        if (!(tile instanceof TileEntityLandingPad) || ((TileEntityLandingPad) tile).getDockedEntity() != null) { return false; }
        Entity made = EntityList.createEntityByIDFromName(variant, world);
        RocketDef def = made == null ? null : RocketCargo.def(made);
        if (def == null || !(made instanceof EntityTieredRocket) || !(made instanceof IPackRocket)) {
            ContentLog.LOGGER.error("Rocket item {} places {}, which is not an entity variant of a Galacticraft rocket with a galacticraft block", stack.getItem().getRegistryName(), variant);
            return false;
        }
        EntityTieredRocket rocket = (EntityTieredRocket) made;
        rocket.rocketType = def.cargoSlots == 27 ? EnumRocketType.INVENTORY27 : def.cargoSlots == 36 ? EnumRocketType.INVENTORY36 : def.cargoSlots == 54 ? EnumRocketType.INVENTORY54 : EnumRocketType.DEFAULT;
        NonNullList<ItemStack> cargo = NonNullList.withSize(rocket.getSizeInventory(), ItemStack.EMPTY);
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(CARGO, CARGO_LIST)) { ItemStackHelper.loadAllItems(tag, cargo); }
        else { RocketCargo.preload(def, cargo); }
        ((IPackRocket) rocket).packCargo(cargo);
        rocket.setPosition(pad.getX() + 0.5D, pad.getY() + ON_PAD + rocket.getOnPadYOffset(), pad.getZ() + 0.5D);
        rocket.prevPosX = rocket.posX;
        rocket.prevPosY = rocket.posY;
        rocket.prevPosZ = rocket.posZ;
        world.spawnEntity(rocket);
        if (tag != null && tag.hasKey(FUEL)) { rocket.fuelTank.fill(new FluidStack(GCFluids.fluidFuel, tag.getInteger(FUEL)), true); }
        return true;
    }

    private static ItemStack carrying(Item item, NonNullList<ItemStack> cargo) {
        ItemStack packed = new ItemStack(item);
        packed.setTagCompound(ItemStackHelper.saveAllItems(new NBTTagCompound(), cargo));
        return packed;
    }

    @Nullable private static Item item(Entity rocket) {
        ResourceLocation variant = EntityList.getKey(rocket);
        return variant == null ? null : ContentItemRocket.placing(variant);
    }
}
