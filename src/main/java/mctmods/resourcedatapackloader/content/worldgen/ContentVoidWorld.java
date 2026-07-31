package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nullable;

public final class ContentVoidWorld {

    private ContentVoidWorld() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.VOID)) { return false; }

        return ContentControl.flag(ContentControl.VOID, "voidWorld", Config.worldgen.voidWorld) || ContentBiomeControl.everythingBlocked();
    }

    public static boolean appliesTo(@Nullable World world) {
        if (!enabled() || world == null || world.isRemote) { return false; }
        return world.provider.getDimension() == 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event) {
        World world = event.getWorld();
        if (!appliesTo(world)) { return; }

        BlockPos centre = centre();
        platform(world, centre);
        world.getWorldInfo().setSpawn(centre.up());
        event.setCanceled(true);
        Summary.info("void", "Made a void world with a platform at " + centre.getX() + ", " + centre.getY() + ", " + centre.getZ());
    }

    private static void platform(World world, BlockPos centre) {
        IBlockState state = ContentStates.parse(ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock), "voidPlatformBlock");
        if (state == null) {
            ContentLog.LOGGER.error("voidPlatformBlock '{}' is not a registered block, using stone", ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock));
            state = Blocks.STONE.getDefaultState();
        }

        int reach = Math.max(0, (ContentControl.number(ContentControl.VOID, "voidPlatformSize", Config.worldgen.voidPlatformSize) - 1) / 2);
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                world.setBlockState(centre.add(x, 0, z), state, 2);
            }
        }
    }

    private static BlockPos centre() { return new BlockPos(0, Math.max(1, Math.min(250, ContentControl.number(ContentControl.VOID, "voidPlatformHeight", Config.worldgen.voidPlatformHeight))), 0); }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { standOn(event.player); }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { standOn(event.player); }

    private static void standOn(EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!appliesTo(world)) { return; }

        BlockPos centre = centre();
        if (world.isAirBlock(centre)) { platform(world, centre); }
        if (player.posY >= centre.getY() + 1) { return; }

        player.setPositionAndUpdate(centre.getX() + 0.5, centre.getY() + 1, centre.getZ() + 0.5);
        player.fallDistance = 0.0F;
    }

    @SubscribeEvent
    public static void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        if (appliesTo(event.getWorld())) { event.setCanceled(true); }
    }

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (appliesTo(event.getWorld())) { event.setResult(Event.Result.DENY); }
    }
}
