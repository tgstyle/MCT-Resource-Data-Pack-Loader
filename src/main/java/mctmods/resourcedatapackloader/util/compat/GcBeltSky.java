package mctmods.resourcedatapackloader.util.compat;

import micdoodle8.mods.galacticraft.planets.asteroids.client.SkyProviderAsteroids;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) final class GcBeltSky {
    private GcBeltSky() {}

    static void apply(GcWorldProvider provider) {
        if (provider.getSkyRenderer() == null) { provider.setSkyRenderer(new SkyProviderAsteroids(provider)); }
    }
}
