package mctmods.resourcedatapackloader.content;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ContentSleepingBags {
    private static final Map<UUID, BlockPos> SLEEPERS = new HashMap<>();

    private ContentSleepingBags() {}

    public static EnumActionResult use(EntityPlayer player, World world, BlockPos clicked, EnumFacing face) {
        if (world.isRemote || !(player instanceof EntityPlayerMP) || face != EnumFacing.UP) { return EnumActionResult.PASS; }
        BlockPos target = clicked.up();
        IBlockState below = world.getBlockState(clicked);
        if (!world.getBlockState(target).getBlock().isReplaceable(world, target) || !below.isSideSolid(world, clicked, EnumFacing.UP)) { return EnumActionResult.PASS; }
        EntityPlayer.SleepResult result = player.trySleep(target);
        if (result == EntityPlayer.SleepResult.OK) {
            SLEEPERS.put(player.getUniqueID(), target);
            player.setPosition(target.getX() + 0.5D, target.getY() + 0.05D, target.getZ() + 0.5D);
            return EnumActionResult.SUCCESS;
        }
        if (result == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) { player.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep"), true); }
        else if (result == EntityPlayer.SleepResult.NOT_SAFE) { player.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe"), true); }
        else if (result == EntityPlayer.SleepResult.TOO_FAR_AWAY) { player.sendStatusMessage(new TextComponentTranslation("tile.bed.tooFarAway"), true); }
        return EnumActionResult.SUCCESS;
    }

    @SubscribeEvent public static void onLocationCheck(SleepingLocationCheckEvent event) {
        BlockPos at = SLEEPERS.get(event.getEntityPlayer().getUniqueID());
        if (at != null && at.equals(event.getSleepingLocation())) { event.setResult(Event.Result.ALLOW); }
    }

    @SubscribeEvent public static void onWakeUp(PlayerWakeUpEvent event) { SLEEPERS.remove(event.getEntityPlayer().getUniqueID()); }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SLEEPERS.remove(event.player.getUniqueID()); }
}
