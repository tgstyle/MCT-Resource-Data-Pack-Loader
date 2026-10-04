package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.util.RainSplash;

import micdoodle8.mods.galacticraft.api.world.IWeatherProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import javax.annotation.Nullable;

public class GcWeatherWorldProvider extends GcWorldProvider implements IWeatherProvider {
    @Override @Nullable public RainDef rainSplash() { return null; }

    @Override @SideOnly(Side.CLIENT) public Particle getParticle(WorldClient world, double x, double y, double z) { return RainSplash.particle(world, rain(), x, y, z); }

    @Override public void weatherSounds(int count, Minecraft mc, World world, BlockPos pos, double x, double y, double z, Random random) {
        RainDef rain = rain();
        if (rain != null) { RainSplash.sound(world, rain, pos, x, y, z); }
    }

    @Override public int getSoundInterval(float strength) {
        RainDef rain = rain();
        return rain == null ? 3 : rain.interval;
    }
}
