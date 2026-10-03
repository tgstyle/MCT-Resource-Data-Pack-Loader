package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityStorage {
    public static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "storage");
    public static final AttachmentType<EntityStorage> TYPE = AttachmentType.builder(holder -> new EntityStorage(holder instanceof Entity entity ? def(entity) : null)).serialize(new Saved()).build();
    private static final String ITEMS = "Items";
    private static final String FLUID = "Fluid";
    private static final String ENERGY = "Energy";
    @Nullable private final Items items;
    @Nullable private final Tank tank;
    @Nullable private final Energy energy;
    private final boolean buckets;
    private final boolean dropsOnDeath;

    private EntityStorage(@Nullable StorageDef def) {
        this.items = def != null && def.slots() > 0 ? new Items(def.slots(), def.items()) : null;
        this.tank = def != null && def.fluidCapacity() > 0 ? new Tank(def.fluidCapacity(), def.fluids()) : null;
        this.energy = def != null && def.energyCapacity() > 0 ? new Energy(def.energyCapacity(), def.energyTransfer()) : null;
        this.buckets = def != null && def.buckets();
        this.dropsOnDeath = def != null && def.dropsOnDeath();
    }

    @Nullable public static StorageDef def(@Nullable Entity entity) {
        EntityVariantDef def = entity == null ? null : ContentEntities.def(entity);
        return def == null ? null : def.storage();
    }

    @Nullable public static EntityStorage of(@Nullable Entity entity) { return entity == null || def(entity) == null ? null : entity.getData(TYPE); }

    @Nullable public IItemHandler items() { return items; }

    public Fluid fluid() { return tank == null ? Fluids.EMPTY : tank.getFluid().getFluid(); }

    public int fluidAmount() { return tank == null ? 0 : tank.getFluidAmount(); }

    public int energyStored() { return energy == null ? 0 : energy.getEnergyStored(); }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (EntityType<?> type : ContentEntities.types().values()) {
            EntityVariantDef def = ContentEntities.def(type);
            if (def == null || def.storage() == null) { continue; }
            event.registerEntity(Capabilities.ItemHandler.ENTITY, type, (entity, context) -> entity.getData(TYPE).items);
            event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, type, (entity, side) -> entity.getData(TYPE).items);
            event.registerEntity(Capabilities.FluidHandler.ENTITY, type, (entity, side) -> entity.getData(TYPE).tank);
            event.registerEntity(Capabilities.EnergyStorage.ENTITY, type, (entity, side) -> entity.getData(TYPE).energy);
        }
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
            player.openMenu(new SimpleMenuProvider((id, inventory, opener) -> new EntityStorageMenu(id, inventory, target, def), target.getDisplayName()), extra -> EntityStorageMenu.write(extra, target, def));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static final class Saved implements IAttachmentSerializer<CompoundTag, EntityStorage> {
        @Override @Nonnull public EntityStorage read(@Nonnull IAttachmentHolder holder, @Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
            EntityStorage storage = new EntityStorage(holder instanceof Entity entity ? def(entity) : null);
            if (storage.items != null) { storage.items.deserializeNBT(lookup, tag.getCompound(ITEMS)); }
            if (storage.tank != null) { storage.tank.readFromNBT(lookup, tag.getCompound(FLUID)); }
            if (storage.energy != null) { storage.energy.load(tag.getInt(ENERGY)); }
            return storage;
        }

        @Override @Nullable public CompoundTag write(@Nonnull EntityStorage storage, @Nonnull HolderLookup.Provider lookup) {
            if (storage.items == null && storage.tank == null && storage.energy == null) { return null; }
            CompoundTag tag = new CompoundTag();
            if (storage.items != null) { tag.put(ITEMS, storage.items.serializeNBT(lookup)); }
            if (storage.tank != null) { tag.put(FLUID, storage.tank.writeToNBT(lookup, new CompoundTag())); }
            if (storage.energy != null) { tag.putInt(ENERGY, storage.energy.getEnergyStored()); }
            return tag;
        }
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

        @Override public int fill(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
            if (resource.isEmpty()) { return 0; }
            int room = filter.room(resource.getFluid(), getFluidAmount());
            if (room <= 0) { return 0; }
            return super.fill(room < resource.getAmount() ? resource.copyWithAmount(room) : resource, action);
        }
    }

    private static final class Energy extends EnergyStorage {
        private Energy(int capacity, int transfer) { super(capacity, transfer); }

        private void load(int stored) { energy = Mth.clamp(stored, 0, capacity); }
    }
}
