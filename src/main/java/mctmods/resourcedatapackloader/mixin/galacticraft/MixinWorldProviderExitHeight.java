package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldProvider.class) public abstract class MixinWorldProviderExitHeight implements IExitHeight {
    @Shadow protected World world;

    @Override public double getYCoordinateToTeleport() {
        if (GenHeights.rubic(world)) { return GenHeights.ceiling(world, 256) + 944; }
        return 1200;
    }
}
