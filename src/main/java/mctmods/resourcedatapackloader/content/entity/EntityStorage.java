package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import com.google.common.collect.MapMaker;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityStorage implements ICapabilitySerializable<CompoundTag> {
    private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "storage");
    private static final String ITEMS = "Items";
    private static final String FLUID = "Fluid";
    private static final String ENERGY = "Energy";
    private static final Map<Entity, EntityStorage> HELD = new MapMaker().weakKeys().makeMap();
    @Nullable private final Items items;
    @Nullable private final Tank tank;
    @Nullable private final Energy energy;
    private final LazyOptional<Items> itemsHeld;
    private final LazyOptional<Tank> tankHeld;
    private final LazyOptional<Energy> energyHeld;
    private final boolean buckets;
    private final boolean dropsOnDeath;

    private EntityStorage(StorageDef def) {
        this.items = def.slots() > 0 ? new Items(def.slots(), def.items()) : null;
        this.tank = def.fluidCapacity() > 0 ? new Tank(def.fluidCapacity(), def.fluids()) : null;
        this.energy = def.energyCapacity() > 0 ? new Energy(def.energyCapacity(), def.energyTransfer()) : null;
        this.itemsHeld = items == null ? LazyOptional.empty() : LazyOptional.of(() -> items);
        this.tankHeld = tank == null ? LazyOptional.empty() : LazyOptional.of(() -> tank);
        this.energyHeld = energy == null ? LazyOptional.empty() : LazyOptional.of(() -> energy);
        this.buckets = def.buckets();
        this.dropsOnDeath = def.dropsOnDeath();
    }

    @Nullable public static StorageDef def(@Nullable Entity entity) {
        EntityVariantDef def = entity == null ? null : ContentEntities.def(entity);
        return def == null ? null : def.storage();
    }

    @Nullable public static EntityStorage of(@Nullable Entity entity) { return entity == null ? null : HELD.get(entity); }

    @SuppressWarnings("unused") public static LazyOptional<?> capability(Entity entity, Capability<?> capability, LazyOptional<?> base) {
        EntityStorage storage = of(entity);
        LazyOptional<?> own = storage == null ? LazyOptional.empty() : storage.held(capability);
        return own.isPresent() ? own : base;
    }

    @Nullable public IItemHandler items() { return items; }

    public Fluid fluid() { return tank == null ? Fluids.EMPTY : tank.getFluid().getFluid(); }

    public int fluidAmount() { return tank == null ? 0 : tank.getFluidAmount(); }

    public int energyStored() { return energy == null ? 0 : energy.getEnergyStored(); }

    public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
        StorageDef def = def(event.getObject());
        if (def == null) { return; }
        EntityStorage storage = new EntityStorage(def);
        HELD.put(event.getObject(), storage);
        event.addCapability(KEY, storage);
    }

    private static List<ItemStack> taken(Entity entity) {
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

    public static void onLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level) || entity instanceof LivingEntity || entity.getRemovalReason() == null || !entity.getRemovalReason().shouldDestroy()) { return; }
        for (ItemStack stack : taken(entity)) { level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack)); }
    }

    public static void onDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        for (ItemStack stack : taken(entity)) { event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack)); }
    }

    private static boolean poured(PlayerInteractEvent.EntityInteract event) {
        EntityStorage storage = of(event.getTarget());
        if (storage == null || storage.tank == null || !storage.buckets) { return false; }
        if (event.getLevel().isClientSide()) { return FluidUtil.getFluidHandler(event.getItemStack()).isPresent(); }
        return FluidUtil.interactWithFluidHandler(event.getEntity(), event.getHand(), storage.tank);
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        StorageDef def = def(target);
        if (event.getHand() != InteractionHand.MAIN_HAND || def == null) { return; }
        if (!event.getEntity().isSecondaryUseActive()) {
            if (poured(event)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
            return;
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inventory, opener) -> new EntityStorageMenu(id, inventory, target, def), target.getDisplayName()), extra -> EntityStorageMenu.write(extra, target, def));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) { return held(capability).cast(); }

    private LazyOptional<?> held(Capability<?> capability) {
        return capability == ForgeCapabilities.ITEM_HANDLER ? itemsHeld : capability == ForgeCapabilities.FLUID_HANDLER ? tankHeld : capability == ForgeCapabilities.ENERGY ? energyHeld : LazyOptional.empty();
    }

    @Override public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (items != null) { tag.put(ITEMS, items.serializeNBT()); }
        if (tank != null) { tag.put(FLUID, tank.writeToNBT(new CompoundTag())); }
        if (energy != null) { tag.putInt(ENERGY, energy.getEnergyStored()); }
        return tag;
    }

    @Override public void deserializeNBT(CompoundTag tag) {
        if (items != null) { items.deserializeNBT(tag.getCompound(ITEMS)); }
        if (tank != null) { tank.readFromNBT(tag.getCompound(FLUID)); }
        if (energy != null) { energy.load(tag.getInt(ENERGY)); }
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

        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return filter.room(stack, stacks) > 0; }

        @Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            int room = filter.room(stack, stacks);
            if (room >= stack.getCount()) { return super.insertItem(slot, stack, simulate); }
            if (room <= 0) { return stack; }
            ItemStack left = super.insertItem(slot, stack.copyWithCount(room), simulate);
            return stack.copyWithCount(stack.getCount() - room + left.getCount());
        }
    }

    private static final class Tank extends FluidTank {
        private final FilterDef filter;

        private Tank(int capacity, FilterDef filter) {
            super(capacity);
            this.filter = filter;
        }

        @Override public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) { return 0; }
            int room = filter.room(resource.getFluid(), getFluidAmount());
            if (room <= 0) { return 0; }
            return super.fill(room < resource.getAmount() ? new FluidStack(resource, room) : resource, action);
        }
    }

    private static final class Energy extends EnergyStorage {
        private Energy(int capacity, int transfer) { super(capacity, transfer); }

        private void load(int stored) { energy = Mth.clamp(stored, 0, capacity); }
    }
}
