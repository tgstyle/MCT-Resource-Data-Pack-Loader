package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class WorldFx {
    private WorldFx() {}

    public static void play(World world, double x, double y, double z, String sound, SoundCategory category) {
        SoundEvent event = sound.isEmpty() ? null : SoundEvent.REGISTRY.getObject(new ResourceLocation(sound));
        if (event != null) { world.playSound(null, x, y, z, event, category, 1.0F, 1.0F); }
    }

    public static void particles(World world, double x, double y, double z, EnumParticleTypes type, int count, double spreadXZ, double spreadY) {
        if (world instanceof WorldServer) { ((WorldServer) world).spawnParticle(type, x, y, z, count, spreadXZ, spreadY, spreadXZ, 0.0D); }
    }
}
