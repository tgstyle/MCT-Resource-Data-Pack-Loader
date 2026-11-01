package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public final class ContentContainerUpgrades {
    private static boolean swapping;

    private ContentContainerUpgrades() {}

    public static boolean dropsAllowed() { return !swapping; }

    public static EnumActionResult use(ContainerUpgradeDef upgrade, Item kit, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof ContentContainerBlock)) { return EnumActionResult.PASS; }
        if (player.getCooldownTracker().hasCooldown(kit)) { return EnumActionResult.FAIL; }
        if (world.isRemote) { return EnumActionResult.SUCCESS; }
        if (!(player instanceof EntityPlayerMP)) { return EnumActionResult.PASS; }
        TileEntity found = world.getTileEntity(pos);
        if (!(found instanceof TileEntityPackContainer)) { return EnumActionResult.PASS; }
        TileEntityPackContainer old = (TileEntityPackContainer) found;
        ContainerUpgradeDef.Path path = pathFor(upgrade, state);
        if (path == null) { return refuse(player, "no path for that container", "wrong"); }
        IBlockState target = ContentStates.parse(path.to, kit.getRegistryName());
        if (target == null || !(target.getBlock() instanceof ContentContainerBlock) || target == state) { return refuse(player, "nothing to become", "unavailable"); }
        if (((ContentContainerBlock) target.getBlock()).container().size() < old.getSizeInventory()) { return refuse(player, "target is smaller", "smaller"); }
        if (old.watched()) { return refuse(player, "someone has it open", "in_use"); }
        if (old.pendingLoot() && !upgrade.carryLoot) { return refuse(player, "loot not rolled", "loot"); }
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand)) || !ContentCarry.asks((EntityPlayerMP) player, pos)) {
            ContentLog.LOGGER.info("Container upgrade: {} was refused at {} (not allowed to change it)", player.getName(), pos);
            player.sendStatusMessage(new TextComponentTranslation("rdpl.carry.denied"), true);
            return EnumActionResult.FAIL;
        }
        if (!swap(state, target, world, pos, old)) { return refuse(player, "the swap failed", "unavailable"); }
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the container at {} to {}", player.getName(), pos, target.getBlock().getRegistryName());
        return EnumActionResult.SUCCESS;
    }

    public static void check(ItemDef def) {
        if (def.upgrade == null) { return; }
        for (ContainerUpgradeDef.Path path : def.upgrade.paths) {
            IBlockState from = ContentStates.parse(path.from, def.registryName);
            IBlockState to = ContentStates.parse(path.to, def.registryName);
            if (from == null || to == null) { continue; }
            if (!(from.getBlock() instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a fromBlock, which is not a container", def.registryName, path.from); }
            else if (!(to.getBlock() instanceof ContentContainerBlock)) { ContentLog.LOGGER.error("The container upgrade {} names {} as a toBlock, which is not a container", def.registryName, path.to); }
            else if (path.from.equals(path.to)) { ContentLog.LOGGER.error("The container upgrade {} changes {} into itself", def.registryName, path.from); }
            else if (((ContentContainerBlock) to.getBlock()).container().size() < ((ContentContainerBlock) from.getBlock()).container().size()) { ContentLog.LOGGER.error("The container upgrade {} changes {} into {}, which holds fewer slots", def.registryName, path.from, path.to); }
        }
    }

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

    private static EnumActionResult refuse(EntityPlayer player, String why, String key) {
        ContentLog.LOGGER.info("Container upgrade: {} was refused ({})", player.getName(), why);
        player.sendStatusMessage(new TextComponentTranslation("rdpl.container_upgrade." + key), true);
        return EnumActionResult.FAIL;
    }

    private static boolean place(World world, BlockPos pos, IBlockState state) {
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
            ((ContentContainerBlock) target.getBlock()).reshape((TileEntityPackContainer) tile, target);
            ((TileEntityPackContainer) tile).adopt(tag);
            world.notifyBlockUpdate(pos, target, target, 3);
            world.notifyNeighborsOfStateChange(pos, target.getBlock(), false);
            SoundType sound = target.getBlock().getSoundType(target, world, pos, null);
            world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
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
            ((ContentContainerBlock) state.getBlock()).reshape((TileEntityPackContainer) tile, state);
            ((TileEntityPackContainer) tile).adopt(tag);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The container at {} could not be put back, its saved data was {}", pos, tag, ex);
        }
    }
}
