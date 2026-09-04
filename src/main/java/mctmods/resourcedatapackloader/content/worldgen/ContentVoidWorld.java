package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.interfaces.IVoidMemory;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IDragonFightManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.end.DragonFightManager;
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

    @Nullable public static Chunk emptyChunk(@Nullable World world, int chunkX, int chunkZ) {
        if (!appliesTo(world)) { return null; }
        Chunk chunk = new Chunk(world, new ChunkPrimer(), chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    public static boolean appliesTo(@Nullable World world) {
        if (world == null || world.isRemote) { return false; }
        IVoidMemory memory = memory();
        if (memory != null && memory.rdpl$voidRecorded()) { return memory.rdpl$voidAppliesTo(world.provider.getDimension()); }
        if (!enabled()) { return false; }
        int[] wanted = ContentControl.numbers(ContentControl.VOID, "voidWorldDimensions", Config.worldgen.voidWorldDimensions);
        boolean listed = false;
        for (int dimension : wanted) {
            if (dimension == world.provider.getDimension()) {
                listed = true;
                break;
            }
        }
        return listed != ContentControl.flag(ContentControl.VOID, "voidWorldDimensionsAreBlacklist", Config.worldgen.voidWorldDimensionsAreBlacklist);
    }

    public static void record(IVoidMemory memory) {
        memory.rdpl$recordVoid(enabled(),
                ContentControl.numbers(ContentControl.VOID, "voidWorldDimensions", Config.worldgen.voidWorldDimensions),
                ContentControl.flag(ContentControl.VOID, "voidWorldDimensionsAreBlacklist", Config.worldgen.voidWorldDimensionsAreBlacklist));
    }

    @Nullable private static IVoidMemory memory() {
        WorldServer overworld = DimensionManager.getWorld(0);
        return overworld == null ? null : (IVoidMemory) overworld.getWorldInfo();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event) {
        World world = event.getWorld();
        if (!appliesTo(world)) { return; }
        BlockPos center = center();
        platform(world, center);
        world.getWorldInfo().setSpawn(center.up());
        world.getGameRules().setOrCreateGameRule("spawnRadius", "0");
        event.setCanceled(true);
        Summary.info("void", "Made a void world with a platform at " + center.getX() + ", " + center.getY() + ", " + center.getZ());
    }

    private static void platform(World world, BlockPos center) {
        IBlockState state = ContentStates.parse(ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock), "voidPlatformBlock");
        if (state == null) {
            ContentLog.LOGGER.error("voidPlatformBlock '{}' is not a registered block, using stone", ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock));
            state = Blocks.STONE.getDefaultState();
        }
        int reach = Math.max(0, (ContentControl.number(ContentControl.VOID, "voidPlatformSize", Config.worldgen.voidPlatformSize) - 1) / 2);
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) { world.setBlockState(center.add(x, 0, z), state, 2); }
        }
    }

    private static BlockPos center() { return new BlockPos(0, Math.max(1, Math.min(250, ContentControl.number(ContentControl.VOID, "voidPlatformHeight", Config.worldgen.voidPlatformHeight))), 0); }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (ContentEndDragon.wanted(world) || !(world.provider instanceof WorldProviderEnd)) { return; }
        DragonFightManager fight = ((WorldProviderEnd) world.provider).getDragonFightManager();
        if (fight == null) { return; }
        ((IDragonFightManager) fight).rdpl$getBossInfo().setVisible(false);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { standOn(event.player); }

    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { standOn(event.player); }

    private static void standOn(EntityPlayer player) {
        World world = player.getEntityWorld();
        if (!appliesTo(world)) { return; }
        BlockPos center = center();
        if (world.isAirBlock(center)) { platform(world, center); }
        if (standing(player, center)) { return; }
        player.setPositionAndUpdate(center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5);
        player.fallDistance = 0.0F;
    }

    private static boolean standing(EntityPlayer player, BlockPos center) {
        if (player.posY < center.getY() + 1) { return false; }
        return Math.abs(player.posX - (center.getX() + 0.5)) <= 0.5 && Math.abs(player.posZ - (center.getZ() + 0.5)) <= 0.5;
    }

    @SubscribeEvent public static void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        if (appliesTo(event.getWorld())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (appliesTo(event.getWorld())) { event.setResult(Event.Result.DENY); }
    }
}
