package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ContentCarry {
    private static final String BLOCKS = "blocks";
    private static final String MOBS = "mobs";
    private static final String CARRY = "rdplCarry";
    private static final String KIND = "kind";
    private static final String BLOCK = "block";
    private static final String MOB = "mob";
    private static final String META = "meta";
    private static final String TILE = "tile";
    private static final String ID = "id";
    private static final int EVERY = 10;
    private static final UUID CARRIED = UUID.fromString("6d1f3a52-94c7-4e08-b3a1-7f2c5e9d0b46");
    private static final Set<Block> BLOCK_RULES = new HashSet<>();
    private static final Set<ResourceLocation> MOB_RULES = new HashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static boolean breaking;

    private ContentCarry() {}

    public static boolean breaking() { return breaking; }

    public static boolean listsMob(Entity target) {
        rules();
        ResourceLocation id = EntityList.getKey(target);
        return id != null && MOB_RULES.contains(id);
    }

    private static void rules() {
        if (GENERATION.stale()) { reload(); }
    }

    private static void reload() {
        BLOCK_RULES.clear();
        MOB_RULES.clear();
        Json.eachObject(PackManager.CARRIES, "carries", ContentCarry::read);
        if (!BLOCK_RULES.isEmpty() || !MOB_RULES.isEmpty()) { ContentLog.LOGGER.info("Loaded carrying for {} block(s) and {} mob(s)", BLOCK_RULES.size(), MOB_RULES.size()); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has(BLOCKS) && !json.has(MOBS)) {
            ContentLog.LOGGER.error("Carry {} names no blocks and no mobs, ignoring it", key);
            return;
        }
        if (json.has(BLOCKS)) {
            for (JsonElement element : Json.getJsonArray(json, BLOCKS)) {
                ResourceLocation name = new ResourceLocation(Json.getString(element, BLOCKS));
                if (!ForgeRegistries.BLOCKS.containsKey(name)) {
                    ContentLog.LOGGER.error("Carry {} names the block {}, which nothing registers, skipping it", key, name);
                    continue;
                }
                BLOCK_RULES.add(ForgeRegistries.BLOCKS.getValue(name));
            }
        }
        if (json.has(MOBS)) {
            for (JsonElement element : Json.getJsonArray(json, MOBS)) {
                ResourceLocation name = new ResourceLocation(Json.getString(element, MOBS));
                if (!EntityList.isRegistered(name)) {
                    ContentLog.LOGGER.error("Carry {} names the mob {}, which nothing registers, skipping it", key, name);
                    continue;
                }
                MOB_RULES.add(name);
            }
        }
    }

    private static boolean carrying(EntityPlayer player) {
        if (player.world.isRemote) { return player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(CARRIED) != null; }
        return !PlayerPersisted.read(player, CARRY).isEmpty();
    }

    private static boolean notReady(PlayerInteractEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        return event.getHand() != EnumHand.MAIN_HAND || !player.isSneaking() || player.isSpectator() || !player.getHeldItemMainhand().isEmpty();
    }

    private static void consume(PlayerInteractEvent event) {
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (notReady(event)) { return; }
        rules();
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (world.isRemote) {
            if (carrying(player) || BLOCK_RULES.contains(world.getBlockState(pos).getBlock())) { consume(event); }
            return;
        }
        EntityPlayerMP mover = (EntityPlayerMP) player;
        NBTTagCompound held = PlayerPersisted.read(player, CARRY);
        if (!held.isEmpty()) {
            consume(event);
            setDown(mover, held, pos, event.getFace() == null ? EnumFacing.UP : event.getFace());
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if (!BLOCK_RULES.contains(state.getBlock())) { return; }
        consume(event);
        pickUp(mover, pos, state);
    }

    @SubscribeEvent public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (notReady(event) || !listsMob(event.getTarget())) { return; }
        consume(event);
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) { return; }
        Entity target = event.getTarget();
        if (carrying(player)) {
            refuse(player, EntityList.getKey(target), "already carrying", "rdpl.carry.full");
            return;
        }
        pickUp((EntityPlayerMP) player, target);
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player.world.isRemote || player.ticksExisted % EVERY != 0) { return; }
        NBTTagCompound held = PlayerPersisted.read(player, CARRY);
        boolean carrying = !held.isEmpty();
        if (carrying && unresolved(held)) {
            ContentLog.LOGGER.error("Carry: {} was carrying something that nothing registers any more, so the carry is cleared: {}", player.getName(), held);
            PlayerPersisted.of(player).removeTag(CARRY);
            carrying = false;
        }
        slow(player, carrying);
    }

    private static boolean unresolved(NBTTagCompound held) {
        String kind = held.getString(KIND);
        if (BLOCK.equals(kind)) { return !ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(held.getString(BLOCK))); }
        return !MOB.equals(kind) || !EntityList.isRegistered(new ResourceLocation(held.getCompoundTag(MOB).getString(ID)));
    }

    private static void slow(EntityPlayer player, boolean carrying) {
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (carrying == (speed.getModifier(CARRIED) != null)) { return; }
        if (carrying) { speed.applyModifier(new AttributeModifier(CARRIED, "rdpl carry", -0.5D, 2).setSaved(false)); }
        else { speed.removeModifier(CARRIED); }
    }

    private static void refuse(EntityPlayer player, Object what, String why, String key) {
        ContentLog.LOGGER.info("Carry: {} was refused {} ({})", player.getName(), what, why);
        player.sendStatusMessage(new TextComponentTranslation(key), true);
    }

    private static void taken(EntityPlayer player, NBTTagCompound saved, Object what, ITextComponent name, String key) {
        PlayerPersisted.of(player).setTag(CARRY, saved);
        slow(player, true);
        ContentLog.LOGGER.info("Carry: {} picked up {}", player.getName(), what);
        player.sendStatusMessage(new TextComponentTranslation(key, name), true);
    }

    private static void given(EntityPlayer player, Object what, ITextComponent name) {
        PlayerPersisted.of(player).removeTag(CARRY);
        slow(player, false);
        ContentLog.LOGGER.info("Carry: {} set down {}", player.getName(), what);
        player.sendStatusMessage(new TextComponentTranslation("rdpl.carry.down", name), true);
    }

    private static boolean twoBlock(IBlockState state) { return state.getPropertyKeys().contains(BlockDoor.HALF) || state.getPropertyKeys().contains(BlockDoublePlant.HALF) || state.getPropertyKeys().contains(BlockBed.PART); }

    private static boolean permits(EntityPlayerMP player, BlockPos pos) {
        breaking = true;
        try { return ForgeHooks.onBlockBreakEvent(player.world, player.interactionManager.getGameType(), player, pos) >= 0; }
        finally { breaking = false; }
    }

    private static ITextComponent called(Block block) { return new TextComponentTranslation(block.getTranslationKey() + ".name"); }

    private static void pickUp(EntityPlayerMP player, BlockPos pos, IBlockState state) {
        World world = player.world;
        Block block = state.getBlock();
        ResourceLocation name = block.getRegistryName();
        if (state.getBlockHardness(world, pos) < 0.0F) { refuse(player, name, "unbreakable", "rdpl.carry.denied"); return; }
        if (twoBlock(state)) { refuse(player, name, "two blocks", "rdpl.carry.denied"); return; }
        if (!permits(player, pos)) { refuse(player, name, "not allowed to break it", "rdpl.carry.denied"); return; }
        NBTTagCompound saved = new NBTTagCompound();
        saved.setString(KIND, BLOCK);
        saved.setString(BLOCK, String.valueOf(name));
        saved.setInteger(META, block.getMetaFromState(state));
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            saved.setTag(TILE, tile.writeToNBT(new NBTTagCompound()));
            world.removeTileEntity(pos);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        taken(player, saved, name, called(block), "rdpl.carry.block");
    }

    private static void pickUp(EntityPlayerMP player, Entity target) {
        ResourceLocation name = EntityList.getKey(target);
        if (!target.isEntityAlive()) { refuse(player, name, "not alive", "rdpl.carry.denied"); return; }
        target.removePassengers();
        target.dismountRidingEntity();
        NBTTagCompound data = new NBTTagCompound();
        if (!target.writeToNBTOptional(data)) { refuse(player, name, "cannot be saved", "rdpl.carry.denied"); return; }
        ITextComponent label = target.getDisplayName();
        target.setDead();
        NBTTagCompound saved = new NBTTagCompound();
        saved.setString(KIND, MOB);
        saved.setTag(MOB, data);
        taken(player, saved, name, label, "rdpl.carry.mob");
    }

    private static void setDown(EntityPlayerMP player, NBTTagCompound held, BlockPos pos, EnumFacing side) {
        World world = player.world;
        BlockPos target = world.getBlockState(pos).getBlock().isReplaceable(world, pos) ? pos : pos.offset(side);
        if (unresolved(held)) { refuse(player, held.getString(KIND), "nothing registers it", "rdpl.carry.denied"); return; }
        if (BLOCK.equals(held.getString(KIND))) { setDownBlock(player, held, target, side); }
        else { setDownMob(player, held, target, side); }
    }

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    private static void setDownBlock(EntityPlayerMP player, NBTTagCompound held, BlockPos target, EnumFacing side) {
        World world = player.world;
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(held.getString(BLOCK)));
        IBlockState state = block.getStateFromMeta(held.getInteger(META));
        if (!player.canPlayerEdit(target, side, ItemStack.EMPTY) || !world.mayPlace(block, target, false, side, null)) { refuse(player, block.getRegistryName(), "no room", "rdpl.carry.denied"); return; }
        BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, target);
        if (!world.setBlockState(target, state, 3) || ForgeEventFactory.onPlayerBlockPlace(player, snapshot, side, EnumHand.MAIN_HAND).isCanceled()) {
            snapshot.restore(true);
            refuse(player, block.getRegistryName(), "placement canceled", "rdpl.carry.denied");
            return;
        }
        if (held.hasKey(TILE, Constants.NBT.TAG_COMPOUND)) {
            TileEntity tile = world.getTileEntity(target);
            if (tile != null) {
                NBTTagCompound data = held.getCompoundTag(TILE).copy();
                data.setInteger("x", target.getX());
                data.setInteger("y", target.getY());
                data.setInteger("z", target.getZ());
                tile.readFromNBT(data);
                tile.markDirty();
                IBlockState placed = world.getBlockState(target);
                world.notifyBlockUpdate(target, placed, placed, 3);
            }
        }
        given(player, block.getRegistryName(), called(block));
    }

    private static void setDownMob(EntityPlayerMP player, NBTTagCompound held, BlockPos target, EnumFacing side) {
        World world = player.world;
        Entity entity = EntityList.createEntityFromNBT(held.getCompoundTag(MOB).copy(), world);
        ResourceLocation name = new ResourceLocation(held.getCompoundTag(MOB).getString(ID));
        if (entity == null) { refuse(player, name, "cannot be created", "rdpl.carry.denied"); return; }
        entity.setLocationAndAngles(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        if (!player.canPlayerEdit(target, side, ItemStack.EMPTY) || !world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() || !world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity) || !world.spawnEntity(entity)) {
            refuse(player, name, "no room", "rdpl.carry.denied");
            return;
        }
        given(player, name, entity.getDisplayName());
    }
}
