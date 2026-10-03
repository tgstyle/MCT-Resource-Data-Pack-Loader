package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.menu.EntityStorageMenu;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityStorage {
    public static final Identifier KEY = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "storage");
    public static final AttachmentType<EntityStorage> TYPE = AttachmentType.builder(holder -> new EntityStorage(holder instanceof Entity entity ? def(entity) : null)).serialize(new Saved()).build();
    private static final String ITEMS = "Items";
    private static final String FLUID = "Fluid";
    private static final String ENERGY = "Energy";
    private static final String SLOT = "Slot";
    private static final String SIZE = "Size";
    @Nullable private final Items items;
    @Nullable private final Tank tank;
    @Nullable private final SimpleEnergyHandler energy;
    private final boolean buckets;
    private final boolean dropsOnDeath;

    private EntityStorage(@Nullable StorageDef def) {
        this.items = def != null && def.slots() > 0 ? new Items(def.slots(), def.items()) : null;
        this.tank = def != null && def.fluidCapacity() > 0 ? new Tank(def.fluidCapacity(), def.fluids()) : null;
        this.energy = def != null && def.energyCapacity() > 0 ? new SimpleEnergyHandler(def.energyCapacity(), def.energyTransfer()) : null;
        this.buckets = def != null && def.buckets();
        this.dropsOnDeath = def != null && def.dropsOnDeath();
    }

    @Nullable public static StorageDef def(@Nullable Entity entity) {
        EntityVariantDef def = entity == null ? null : ContentEntities.def(entity);
        return def == null ? null : def.storage();
    }

    @Nullable public static EntityStorage of(@Nullable Entity entity) { return entity == null || def(entity) == null ? null : entity.getData(TYPE); }

    @Nullable public ItemStacksResourceHandler items() { return items; }

    public Fluid fluid() { return tank == null ? Fluids.EMPTY : tank.getResource(0).getFluid(); }

    public int fluidAmount() { return tank == null ? 0 : tank.getAmountAsInt(0); }

    public int energyStored() { return energy == null ? 0 : energy.getAmountAsInt(); }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (EntityType<?> type : ContentEntities.types().values()) {
            EntityVariantDef def = ContentEntities.def(type);
            if (def == null || def.storage() == null) { continue; }
            event.registerEntity(Capabilities.Item.ENTITY, type, (entity, _) -> entity.getData(TYPE).items);
            event.registerEntity(Capabilities.Item.ENTITY_AUTOMATION, type, (entity, _) -> entity.getData(TYPE).items);
            event.registerEntity(Capabilities.Fluid.ENTITY, type, (entity, _) -> entity.getData(TYPE).tank);
            event.registerEntity(Capabilities.Energy.ENTITY, type, (entity, _) -> entity.getData(TYPE).energy);
        }
    }

    private static List<ItemStack> taken(Entity entity) {
        List<ItemStack> taken = new ArrayList<>();
        EntityStorage storage = of(entity);
        if (storage == null || storage.items == null || !storage.dropsOnDeath) { return taken; }
        for (int slot = 0; slot < storage.items.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(storage.items, slot);
            if (stack.isEmpty()) { continue; }
            taken.add(stack);
            storage.items.set(slot, ItemResource.EMPTY, 0);
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
        if (event.getLevel().isClientSide()) { return ItemAccess.forPlayerInteraction(event.getEntity(), event.getHand()).getCapability(Capabilities.Fluid.ITEM) != null; }
        return FluidUtil.interactWithFluidHandler(event.getEntity(), event.getHand(), null, storage.tank, null);
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
            player.openMenu(new SimpleMenuProvider((id, inventory, _) -> new EntityStorageMenu(id, inventory, target, def), target.getDisplayName()), extra -> EntityStorageMenu.write(extra, target, def));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static final class Saved implements IAttachmentSerializer<EntityStorage> {
        @Override @Nonnull public EntityStorage read(@Nonnull IAttachmentHolder holder, @Nonnull ValueInput input) {
            EntityStorage storage = new EntityStorage(holder instanceof Entity entity ? def(entity) : null);
            if (storage.items != null) { storage.items.load(input.childOrEmpty(ITEMS)); }
            if (storage.tank != null) { input.childOrEmpty(FLUID).read(FLUID, FluidStack.CODEC).ifPresent(held -> storage.tank.set(0, FluidResource.of(held), held.getAmount())); }
            if (storage.energy != null) { storage.energy.set(Mth.clamp(input.getIntOr(ENERGY, 0), 0, storage.energy.getCapacityAsInt())); }
            return storage;
        }

        @Override public boolean write(@Nonnull EntityStorage storage, @Nonnull ValueOutput output) {
            if (storage.items == null && storage.tank == null && storage.energy == null) { return false; }
            if (storage.items != null) { storage.items.save(output.child(ITEMS)); }
            if (storage.tank != null) {
                ValueOutput held = output.child(FLUID);
                if (storage.tank.getAmountAsInt(0) > 0) { held.store(FLUID, FluidStack.CODEC, FluidUtil.getStack(storage.tank, 0)); }
            }
            if (storage.energy != null) { output.putInt(ENERGY, storage.energy.getAmountAsInt()); }
            return true;
        }
    }

    private record Held(int slot, ItemStack stack) {
        private static final Codec<Held> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf(SLOT).forGetter(Held::slot), ItemStack.MAP_CODEC.forGetter(Held::stack)).apply(instance, Held::new));
    }

    private static final class Items extends ItemStacksResourceHandler {
        private final FilterDef filter;

        private Items(int slots, FilterDef filter) {
            super(slots);
            this.filter = filter;
        }

        private void load(ValueInput input) {
            for (Held held : input.listOrEmpty(ITEMS, Held.CODEC)) {
                if (held.slot >= 0 && held.slot < size()) { set(held.slot, ItemResource.of(held.stack), held.stack.getCount()); }
            }
        }

        private void save(ValueOutput output) {
            ValueOutput.TypedOutputList<Held> list = output.list(ITEMS, Held.CODEC);
            for (int slot = 0; slot < size(); slot++) {
                ItemStack stack = stacks.get(slot);
                if (!stack.isEmpty()) { list.add(new Held(slot, stack)); }
            }
            output.putInt(SIZE, size());
        }

        @Override public boolean isValid(int index, @Nonnull ItemResource resource) { return filter.room(resource.toStack(), stacks) > 0; }

        @Override public int insert(int index, @Nonnull ItemResource resource, int amount, @Nonnull TransactionContext transaction) {
            int room = filter.room(resource.toStack(), stacks);
            return room <= 0 ? 0 : super.insert(index, resource, Math.min(amount, room), transaction);
        }
    }

    private static final class Tank extends FluidStacksResourceHandler {
        private final FilterDef filter;

        private Tank(int capacity, FilterDef filter) {
            super(1, capacity);
            this.filter = filter;
        }

        @Override public int insert(int index, @Nonnull FluidResource resource, int amount, @Nonnull TransactionContext transaction) {
            int room = filter.room(resource.getFluid(), getAmountAsInt(index));
            return room <= 0 ? 0 : super.insert(index, resource, Math.min(amount, room), transaction);
        }
    }
}
