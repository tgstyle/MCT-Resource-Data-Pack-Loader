package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.WindGust;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentDimensionAmbience {
    private static final int TRIES = 667;
    private static final int NEAR = 16;
    private static final int FAR = 32;
    private static final double SHOWN = 1024.0D;
    private static final int FADE = 40;
    private static final float QUIETEST = 0.001F;
    private static final float NO_RED = 1.0E-4F;
    private static final Map<AmbienceDef, MusicTicker.MusicType> MUSIC = new IdentityHashMap<>();
    private static final List<Loop> LOOPS = new ArrayList<>();
    private static final Random RANDOM = new Random();
    @Nullable private static World seen;
    @Nullable private static ResourceLocation playing;

    private ContentDimensionAmbience() {}

    @Nullable private static AmbienceDef ambience(@Nullable World world) { return world != null && world.provider instanceof ContentWorldProvider ? ((ContentWorldProvider) world.provider).ambience() : null; }

    public static MusicTicker.MusicType music(AmbienceDef ambience) { return MUSIC.computeIfAbsent(ambience, held -> EnumHelperClient.addMusicType("RDPL_AMBIENCE_" + MUSIC.size(), sound(held.music), held.musicMinDelay, held.musicMaxDelay)); }

    private static SoundEvent sound(ResourceLocation name) {
        SoundEvent registered = ForgeRegistries.SOUND_EVENTS.getValue(name);
        return registered == null ? new SoundEvent(name) : registered;
    }

    @SubscribeEvent public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;
        if (world != seen) {
            seen = world;
            LOOPS.clear();
            playing = null;
        }
        Entity view = mc.getRenderViewEntity();
        if (world == null || view == null || mc.isGamePaused()) { return; }
        AmbienceDef ambience = ambience(world);
        loop(mc, ambience == null ? null : ambience.loopSound);
        if (ambience != null && ambience.shows()) { particles(world, view, ambience); }
    }

    private static void loop(Minecraft mc, @Nullable ResourceLocation wanted) {
        LOOPS.removeIf(Loop::isDonePlaying);
        if (Objects.equals(wanted, playing)) { return; }
        playing = wanted;
        LOOPS.forEach(Loop::fadeOut);
        if (wanted == null) { return; }
        Loop loop = new Loop(sound(wanted));
        mc.getSoundHandler().playSound(loop);
        LOOPS.add(loop);
    }

    private static void particles(WorldClient world, Entity view, AmbienceDef ambience) {
        EnumParticleTypes type = EnumParticleTypes.getByName(ambience.particle);
        if (type == null) { return; }
        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        if (ambience.particleColor != AmbienceDef.NO_COLOR && colored(type)) {
            red = Math.max((ambience.particleColor >> 16 & 255) / 255.0F, NO_RED);
            green = (ambience.particleColor >> 8 & 255) / 255.0F;
            blue = (ambience.particleColor & 255) / 255.0F;
        }
        int x = MathHelper.floor(view.posX);
        int y = MathHelper.floor(view.posY);
        int z = MathHelper.floor(view.posZ);
        Minecraft mc = Minecraft.getMinecraft();
        RainDef rain = ((ContentWorldProvider) world.provider).rain();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < TRIES; i++) {
            spot(mc, world, view, at, NEAR, x, y, z, type, ambience.particleChance, red, green, blue, rain);
            spot(mc, world, view, at, FAR, x, y, z, type, ambience.particleChance, red, green, blue, rain);
        }
    }

    private static void spot(Minecraft mc, WorldClient world, Entity view, BlockPos.MutableBlockPos at, int reach, int x, int y, int z, EnumParticleTypes type, float chance, double red, double green, double blue, @Nullable RainDef rain) {
        at.setPos(x + RANDOM.nextInt(reach) - RANDOM.nextInt(reach), y + RANDOM.nextInt(reach) - RANDOM.nextInt(reach), z + RANDOM.nextInt(reach) - RANDOM.nextInt(reach));
        IBlockState state = world.getBlockState(at);
        if (state.isFullCube() || RANDOM.nextFloat() > chance) { return; }
        double spotX = at.getX() + RANDOM.nextDouble();
        double spotY = at.getY() + RANDOM.nextDouble();
        double spotZ = at.getZ() + RANDOM.nextDouble();
        int level = mc.gameSettings.particleSetting;
        if (level == 1 && world.rand.nextInt(3) == 0) { level = 2; }
        if (!type.getShouldIgnoreRange() && (level > 1 || view.getDistanceSq(spotX, spotY, spotZ) > SHOWN)) { return; }
        Particle particle = mc.effectRenderer.spawnEffectParticle(type.getParticleID(), spotX, spotY, spotZ, red, green, blue);
        if (particle != null && rain != null) { WindGust.blow(particle, rain, world.getTotalWorldTime()); }
    }

    private static boolean colored(EnumParticleTypes type) { return type == EnumParticleTypes.REDSTONE || type == EnumParticleTypes.SPELL_MOB || type == EnumParticleTypes.SPELL_MOB_AMBIENT; }

    private static final class Loop extends MovingSound {
        private int fade;
        private int direction = 1;

        private Loop(SoundEvent sound) {
            super(sound, SoundCategory.AMBIENT);
            repeat = true;
            repeatDelay = 0;
            attenuationType = AttenuationType.NONE;
            volume = QUIETEST;
        }

        private void fadeOut() {
            fade = Math.min(fade, FADE);
            direction = -1;
        }

        @Override public void update() {
            if (fade < 0) {
                donePlaying = true;
                return;
            }
            fade += direction;
            volume = Math.max(QUIETEST, MathHelper.clamp(fade / (float) FADE, 0.0F, 1.0F));
            Entity view = Minecraft.getMinecraft().getRenderViewEntity();
            if (view == null) { return; }
            xPosF = (float) view.posX;
            yPosF = (float) (view.posY + view.getEyeHeight());
            zPosF = (float) view.posZ;
        }
    }
}
