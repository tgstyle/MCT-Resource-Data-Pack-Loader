package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.IParticleManager;

import micdoodle8.mods.galacticraft.api.world.IWeatherProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import javax.annotation.Nullable;

public class GcWeatherWorldProvider extends GcWorldProvider implements IWeatherProvider {
    @Nullable private GalacticraftDef.Rain rain() { return def == null || def.galacticraft == null ? null : def.galacticraft.rain; }

    @Override @SideOnly(Side.CLIENT) public Particle getParticle(WorldClient world, double x, double y, double z) {
        GalacticraftDef.Rain rain = rain();
        EnumParticleTypes type = rain == null ? null : EnumParticleTypes.getByName(rain.particle);
        IParticleFactory factory = type == null ? null : ((IParticleManager) Minecraft.getMinecraft().effectRenderer).getParticleTypes().get(type.getParticleID());
        Particle particle = factory == null ? null : factory.createParticle(type.getParticleID(), world, x, y, z, 0.0D, 0.0D, 0.0D);
        return particle == null ? new ParticleRain.Factory().createParticle(EnumParticleTypes.WATER_DROP.getParticleID(), world, x, y, z, 0.0D, 0.0D, 0.0D) : particle;
    }

    @Override public void weatherSounds(int count, Minecraft mc, World world, BlockPos pos, double x, double y, double z, Random random) {
        GalacticraftDef.Rain rain = rain();
        if (rain == null) { return; }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(rain.sound);
        if (sound == null) { return; }
        if ((int) y >= pos.getY() + 1 && world.getPrecipitationHeight(pos).getY() > pos.getY()) { world.playSound(x, y, z, sound, SoundCategory.WEATHER, rain.volume * 0.5F, 0.5F, false); }
        else { world.playSound(x, y, z, sound, SoundCategory.WEATHER, rain.volume, 1.0F, false); }
    }

    @Override public int getSoundInterval(float strength) {
        GalacticraftDef.Rain rain = rain();
        return rain == null ? 3 : rain.interval;
    }
}
