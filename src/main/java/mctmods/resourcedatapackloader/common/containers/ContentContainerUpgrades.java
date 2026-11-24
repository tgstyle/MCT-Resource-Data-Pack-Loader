package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.world.PlacedBlock;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public final class ContentContainerUpgrades {
    static final String CANNOT_MOVE = "cannot_move";
    private static final String UNAVAILABLE = "unavailable";
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean dropsAllowed() { return !swapping; }

    public static EnumActionResult use(ContainerUpgradeDef upgrade, Item kit, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path != null && path.contents != ContainerUpgradeDef.Contents.CONTAINER) { return ContentStorageSwap.use(upgrade, path, kit, player, hand, world, pos, side, state); }
        if (!(state.getBlock() instanceof ContentContainerBlock)) { return EnumActionResult.PASS; }
        EnumActionResult early = early(player, kit, world);
        if (early != null) { return early; }
        TileEntity found = world.getTileEntity(pos);
        if (!(found instanceof TileEntityPackContainer)) { return EnumActionResult.PASS; }
        TileEntityPackContainer old = (TileEntityPackContainer) found;
        if (path == null) { return refuse(player, "no path for that container", "wrong"); }
        IBlockState target = ContentStates.parse(path.to, kit.getRegistryName());
        if (target == null || !(target.getBlock() instanceof ContentContainerBlock) || target == state) { return unavailable(player); }
        if (((ContentContainerBlock) target.getBlock()).container().size() < old.getSizeInventory()) { return smaller(player); }
        if (old.watched()) { return open(player); }
        if (old.pendingLoot() && !upgrade.carryLoot) { return unrolled(player); }
        EnumActionResult barred = barred((EntityPlayerMP) player, hand, pos, side);
        if (barred != null) { return barred; }
        if (!swap(state, target, world, pos, old)) { return refuse(player, "the swap failed", UNAVAILABLE); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", player.getName(), pos, target.getBlock().getRegistryName());
        return EnumActionResult.SUCCESS;
    }

    @Nullable static EnumActionResult early(EntityPlayer player, Item kit, World world) {
        if (player.getCooldownTracker().hasCooldown(kit)) { return EnumActionResult.FAIL; }
        if (world.isRemote) { return EnumActionResult.SUCCESS; }
        return player instanceof EntityPlayerMP ? null : EnumActionResult.PASS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade.paths) {
            IBlockState from = ContentStates.parse(path.from, def.registryName);
            IBlockState to = ContentStates.parse(path.to, def.registryName);
            if (from == null || to == null) { continue; }
            if (path.contents != ContainerUpgradeDef.Contents.CONTAINER) { validate(def, path, from, to, held -> held.getBlock().hasTileEntity(held), "holds nothing", true); }
            else { validate(def, path, from, to, held -> held.getBlock() instanceof ContentContainerBlock, "is not a container", false); }
        }
    }

    private static void validate(ItemDef def, ContainerUpgradeDef.Path path, IBlockState from, IBlockState to, Predicate<IBlockState> holds, String lacking, boolean storage) {
        if (!holds.test(from)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which {}", def.registryName, path.from, lacking); }
        else if (!holds.test(to)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which {}", def.registryName, path.to, lacking); }
        else if (path.from.equals(path.to)) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.registryName, path.from); }
        else if (storage && ContentCarry.twoBlock(from)) { ContentLog.LOGGER.error("The container upgrade {} names {}, which is two blocks, so it never works", def.registryName, path.from); }
        else if (storage && ContentCarry.twoBlock(to)) { ContentLog.LOGGER.error("The container upgrade {} names {}, which is two blocks, so it never works", def.registryName, path.to); }
        else if ((!storage || path.contents == ContainerUpgradeDef.Contents.SLOTS && !path.fit) && shrinks(from, to)) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.registryName, path.from, path.to); }
    }

    private static boolean shrinks(IBlockState from, IBlockState to) { return from.getBlock() instanceof ContentContainerBlock && to.getBlock() instanceof ContentContainerBlock && ((ContentContainerBlock) to.getBlock()).container().size() < ((ContentContainerBlock) from.getBlock()).container().size(); }

    @Nullable private static ContainerUpgradeDef.Path pathFor(ContainerUpgradeDef upgrade, IBlockState state) {
        for (ContainerUpgradeDef.Path path : upgrade.paths) {
            if (matches(path.from, state)) { return path; }
        }
        return null;
    }

    private static boolean matches(String from, IBlockState state) {
        String[] parts = from.split(":");
        ResourceLocation named = new ResourceLocation(parts.length < 3 ? from : parts[0] + ":" + parts[1]);
        if (!named.equals(state.getBlock().getRegistryName())) { return false; }
        return parts.length < 3 || parts[2].trim().equals(String.valueOf(state.getBlock().getMetaFromState(state)));
    }

    static EnumActionResult refuse(EntityPlayer player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getName(), why);
        player.sendStatusMessage(new TextComponentTranslation("rdpl.container_upgrade." + key), true);
        return EnumActionResult.FAIL;
    }

    static EnumActionResult unavailable(EntityPlayer player) { return refuse(player, "nothing to become", UNAVAILABLE); }

    static EnumActionResult smaller(EntityPlayer player) { return refuse(player, "target is smaller", "smaller"); }

    static EnumActionResult open(EntityPlayer player) { return refuse(player, "someone has it open", "in_use"); }

    static EnumActionResult unrolled(EntityPlayer player) { return refuse(player, "loot not rolled", "loot"); }

    @Nullable static EnumActionResult barred(EntityPlayerMP player, EnumHand hand, BlockPos pos, EnumFacing side) {
        return player.canPlayerEdit(pos, side, player.getHeldItem(hand)) && ContentCarry.asks(player, pos) ? null : denied(player, pos, "not allowed to change it");
    }

    static EnumActionResult denied(EntityPlayer player, BlockPos pos, String why) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused at {} ({})", player.getName(), pos, why);
        player.sendStatusMessage(new TextComponentTranslation(ContentCarry.DENIED), true);
        return EnumActionResult.FAIL;
    }

    static boolean place(World world, BlockPos pos, IBlockState state) {
        swapping = true;
        try { return world.setBlockState(pos, state, BlockFlags.UPDATE_CLIENTS); }
        finally { swapping = false; }
    }

    private static boolean swap(IBlockState state, IBlockState target, World world, BlockPos pos, TileEntityPackContainer old) {
        NBTTagCompound tag = old.vacate();
        try {
            boolean set = place(world, pos, target);
            TileEntity tile = world.getTileEntity(pos);
            if (!set || !(tile instanceof TileEntityPackContainer)) {
                ContentLog.LOGGER.error("The container at {} could not become {} (set {}, found {}), so it is put back", pos, target, set, tile);
                recover(world, pos, state, tag);
                return false;
            }
            fill((TileEntityPackContainer) tile, target, tag);
            PlacedBlock.announce(world, pos, target);
            return true;
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container upgrade at {} failed, so the container is put back", pos, ex);
            recover(world, pos, state, tag);
            return false;
        }
    }

    private static void recover(World world, BlockPos pos, IBlockState state, NBTTagCompound tag) {
        try {
            if (world.getBlockState(pos) != state) { place(world, pos, state); }
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof TileEntityPackContainer)) { throw new IllegalStateException("no container block entity at " + pos); }
            fill((TileEntityPackContainer) tile, state, tag);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container at {} could not be put back, its saved data was {}", pos, tag, ex);
        }
    }

    private static void fill(TileEntityPackContainer tile, IBlockState state, NBTTagCompound tag) {
        ContentContainerBlock.reshape(tile, state, false);
        tile.adopt(tag);
    }
}
