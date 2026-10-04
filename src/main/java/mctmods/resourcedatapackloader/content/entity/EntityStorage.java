package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;

import com.google.common.collect.MapMaker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityStorage implements ICapabilitySerializable<NBTTagCompound> {
    private static final ResourceLocation KEY = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "storage");
    private static final String ITEMS = "Items";
    private static final String FLUID = "Fluid";
    private static final String ENERGY = "Energy";
    private static final UUID SLOWED = UUID.fromString("5c3f1a2e-8d47-4b61-9f0a-2e7d6c1b8a94");
    private static final int SECOND = 20;
    private static final Map<Entity, EntityStorage> HELD = new MapMaker().weakKeys().makeMap();
    @Nullable private final Items items;
    @Nullable private final Tank tank;
    @Nullable private final Energy energy;
    private final boolean buckets;
    private final boolean dropsOnDeath;
    private final int fluidUse;
    private final int energyUse;
    private final StorageDef.Dry runsDry;

    private EntityStorage(StorageDef def) {
        this.items = def.slots() > 0 ? new Items(def.slots(), def.items) : null;
        this.tank = def.fluidCapacity > 0 ? new Tank(def.fluidCapacity, def.fluids) : null;
        this.energy = def.energyCapacity > 0 ? new Energy(def.energyCapacity, def.energyTransfer) : null;
        this.buckets = def.buckets;
        this.dropsOnDeath = def.dropsOnDeath;
        this.fluidUse = tank == null ? 0 : def.fluidUse;
        this.energyUse = energy == null ? 0 : def.energyUse;
        this.runsDry = def.runsDry;
    }

    private boolean spends() { return fluidUse > 0 || energyUse > 0; }

    private boolean dry() { return tank != null && tank.getFluidAmount() < fluidUse || energy != null && energy.getEnergyStored() < energyUse; }

    private void spend() {
        if (tank != null && fluidUse > 0) { tank.drainInternal(fluidUse, true); }
        if (energy != null && energyUse > 0) { energy.spend(energyUse); }
    }

    public static boolean stalled(EntityLiving living) {
        EntityStorage storage = of(living);
        if (storage == null || storage.runsDry != StorageDef.Dry.STOPS || !storage.dry()) { return false; }
        living.setMoveForward(0.0F);
        living.moveStrafing = 0.0F;
        living.moveVertical = 0.0F;
        living.setJumping(false);
        living.getNavigator().clearPath();
        return true;
    }

    @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living.world.isRemote || living.ticksExisted % SECOND != 0) { return; }
        EntityStorage storage = of(living);
        if (storage == null || !storage.spends()) { return; }
        boolean paid = !storage.dry();
        if (paid) { storage.spend(); }
        if (storage.runsDry == StorageDef.Dry.SLOWS) { slow(living, storage.dry()); }
        else if (storage.runsDry == StorageDef.Dry.HURTS && !paid) { living.attackEntityFrom(DamageSource.STARVE, 1.0F); }
    }

    private static void slow(EntityLivingBase living, boolean dry) {
        IAttributeInstance speed = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (dry == (speed.getModifier(SLOWED) != null)) { return; }
        if (dry) { speed.applyModifier(new AttributeModifier(SLOWED, "rdpl dry", -0.5D, 2).setSaved(false)); }
        else { speed.removeModifier(SLOWED); }
    }

    @Nullable public static StorageDef def(@Nullable Entity entity) {
        EntityVariantDef def = entity == null ? null : ContentEntities.BY_CLASS.get(entity.getClass());
        return def == null ? null : def.storage;
    }

    @Nullable public static EntityStorage of(@Nullable Entity entity) { return entity == null ? null : HELD.get(entity); }

    @SuppressWarnings("unused") @Nullable public static Object capability(Entity entity, Capability<?> capability, @Nullable Object base) {
        EntityStorage storage = of(entity);
        Object own = storage == null ? null : storage.held(capability);
        return own == null ? base : own;
    }

    @SuppressWarnings("unused") public static boolean has(Entity entity, Capability<?> capability, boolean base) {
        EntityStorage storage = of(entity);
        return base || storage != null && storage.held(capability) != null;
    }

    @Nullable public IItemHandler items() { return items; }

    @Nullable public IFluidHandler tank() { return tank; }

    @Nullable public IEnergyStorage energy() { return energy; }

    @SubscribeEvent public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
        StorageDef def = def(event.getObject());
        if (def == null) { return; }
        EntityStorage storage = new EntityStorage(def);
        HELD.put(event.getObject(), storage);
        event.addCapability(KEY, storage);
    }

    public static List<ItemStack> taken(Entity entity) {
        List<ItemStack> taken = new ArrayList<>();
        EntityStorage storage = of(entity);
        if (storage == null || storage.items == null || !storage.dropsOnDeath) { return taken; }
        for (int slot = 0; slot < storage.items.getSlots(); slot++) {
            ItemStack stack = storage.items.getStackInSlot(slot);
            if (stack.isEmpty()) { continue; }
            taken.add(stack);
            storage.items.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return taken;
    }

    @SuppressWarnings("unused") public static void dying(Entity entity) {
        if (entity.world.isRemote || entity.isDead) { return; }
        for (ItemStack stack : taken(entity)) { entity.entityDropItem(stack, 0.0F); }
    }

    @SubscribeEvent public static void onDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        for (ItemStack stack : taken(entity)) { event.getDrops().add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack)); }
    }

    private static boolean poured(PlayerInteractEvent.EntityInteract event) {
        EntityStorage storage = of(event.getTarget());
        if (storage == null || storage.tank == null || !storage.buckets) { return false; }
        if (event.getWorld().isRemote) { return FluidUtil.getFluidHandler(event.getItemStack()) != null; }
        return FluidUtil.interactWithFluidHandler(event.getEntityPlayer(), event.getHand(), storage.tank);
    }

    @SubscribeEvent public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != EnumHand.MAIN_HAND || def(event.getTarget()) == null) { return; }
        if (!event.getEntityPlayer().isSneaking()) {
            if (poured(event)) {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
            }
            return;
        }
        if (!event.getWorld().isRemote) { event.getEntityPlayer().openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.ENTITY, event.getWorld(), event.getTarget().getEntityId(), 0, 0); }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return held(capability) != null; }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && items != null) { return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(items); }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tank != null) { return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank); }
        if (capability == CapabilityEnergy.ENERGY && energy != null) { return CapabilityEnergy.ENERGY.cast(energy); }
        return null;
    }

    @Nullable private Object held(Capability<?> capability) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? items : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? tank : capability == CapabilityEnergy.ENERGY ? energy : null;
    }

    @Override public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (items != null) { tag.setTag(ITEMS, items.serializeNBT()); }
        if (tank != null) { tag.setTag(FLUID, tank.writeToNBT(new NBTTagCompound())); }
        if (energy != null) { tag.setInteger(ENERGY, energy.getEnergyStored()); }
        return tag;
    }

    @Override public void deserializeNBT(NBTTagCompound tag) {
        if (items != null) { items.deserializeNBT(tag.getCompoundTag(ITEMS)); }
        if (tank != null) { tank.readFromNBT(tag.getCompoundTag(FLUID)); }
        if (energy != null) { energy.load(tag.getInteger(ENERGY)); }
    }

    private static final class Items extends ItemStackHandler {
        private final int slots;
        private final FilterDef filter;

        private Items(int slots, FilterDef filter) {
            super(slots);
            this.slots = slots;
            this.filter = filter;
        }

        @Override public void setSize(int size) { super.setSize(slots); }

        @Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            int room = filter.room(stack, stacks);
            if (room >= stack.getCount()) { return super.insertItem(slot, stack, simulate); }
            if (room <= 0) { return stack; }
            ItemStack left = super.insertItem(slot, ItemHandlerHelper.copyStackWithSize(stack, room), simulate);
            return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - room + left.getCount());
        }
    }

    private static final class Tank extends FluidTank {
        private final FilterDef filter;

        private Tank(int capacity, FilterDef filter) {
            super(capacity);
            this.filter = filter;
        }

        @Override public int fill(@Nullable FluidStack resource, boolean doFill) {
            if (resource == null) { return 0; }
            int room = filter.room(resource, getFluidAmount());
            if (room <= 0) { return 0; }
            return super.fill(room < resource.amount ? new FluidStack(resource, room) : resource, doFill);
        }
    }

    private static final class Energy extends EnergyStorage {
        private Energy(int capacity, int transfer) { super(capacity, transfer); }

        private void load(int stored) { energy = Math.min(capacity, Math.max(0, stored)); }

        private void spend(int used) { energy = Math.max(0, energy - used); }
    }
}
