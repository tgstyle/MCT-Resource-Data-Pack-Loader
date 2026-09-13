package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class ContentCaveAmbience {
    private static final int EVERY = 10;
    private static final int REACH = 16;
    private static final int POSITIONS_PER_TICK = 667;
    private static final int MOST_PARTICLES = 200;
    private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();
    private static final Map<String, EnumParticleTypes> PARTICLES = new HashMap<>();
    private static final Set<String> TOLD = new HashSet<>();
    private static final Map<String, String> WHERE = new HashMap<>();

    private ContentCaveAmbience() {}

    public static boolean wanted() {
        for (CaveRegionDef def : ContentRegistry.caveRegions()) {
            if (def.weight > 0 && def.hasAmbience()) { return true; }
        }
        return false;
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || !(player instanceof EntityPlayerMP) || !(player.world instanceof WorldServer) || player.ticksExisted % EVERY != 0 || player.isSpectator()) { return; }
        WorldServer world = (WorldServer) player.world;
        BlockPos eyes = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        CaveRegionDef region = ContentCaveRegions.regionAt(world, eyes.getX(), eyes.getY(), eyes.getZ());
        String now = region == null ? "" : region.key.toString();
        if (ContentLog.LOGGER.debugEnabled() && !now.equals(WHERE.put(player.getName(), now))) { ContentLog.LOGGER.debug("{} at {}, {}, {} is {}", player.getName(), eyes.getX(), eyes.getY(), eyes.getZ(), now.isEmpty() ? "in no cave region" : "in cave region " + now + (region.hasAmbience() ? " with ambience" : " without ambience")); }
        if (region == null || !region.hasAmbience()) { return; }
        Random random = world.rand;
        if (!region.ambientSound.isEmpty() && random.nextFloat() < 1.0F - (float) Math.pow(1.0F - region.soundChance, EVERY)) { sound((EntityPlayerMP) player, region, random); }
        if (!region.particle.isEmpty() && region.particleChance > 0.0F) { particles((EntityPlayerMP) player, world, region, random); }
    }

    private static void sound(EntityPlayerMP player, CaveRegionDef region, Random random) {
        SoundEvent sound = SOUNDS.computeIfAbsent(region.ambientSound, name -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(name)));
        if (sound == null) {
            if (TOLD.add(region.key + " sound")) { ContentLog.LOGGER.error("Cave region {} names the ambient sound {}, which nothing registers, so the region stays quiet", region.key, region.ambientSound); }
            return;
        }
        player.connection.sendPacket(new SPacketSoundEffect(sound, SoundCategory.AMBIENT, player.posX, player.posY + player.getEyeHeight(), player.posZ, 1.0F, 0.8F + random.nextFloat() * 0.4F));
    }

    private static void particles(EntityPlayerMP player, WorldServer world, CaveRegionDef region, Random random) {
        EnumParticleTypes type = PARTICLES.computeIfAbsent(region.particle, EnumParticleTypes::getByName);
        if (type == null) {
            if (TOLD.add(region.key + " particle")) { ContentLog.LOGGER.error("Cave region {} names the particle {}, which is not one of the game's particle names, so the region shows none", region.key, region.particle); }
            return;
        }
        int tries = MathHelper.clamp(Math.round(POSITIONS_PER_TICK * region.particleChance * EVERY), 1, MOST_PARTICLES);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < tries; i++) {
            at.setPos(player.posX + random.nextInt(REACH) - random.nextInt(REACH), player.posY + random.nextInt(REACH) - random.nextInt(REACH), player.posZ + random.nextInt(REACH) - random.nextInt(REACH));
            if (!world.isBlockLoaded(at) || !world.isAirBlock(at) || ContentCaveRegions.regionAt(world, at.getX(), at.getY(), at.getZ()) != region) { continue; }
            world.spawnParticle(player, type, false, at.getX() + random.nextDouble(), at.getY() + random.nextDouble(), at.getZ() + random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
