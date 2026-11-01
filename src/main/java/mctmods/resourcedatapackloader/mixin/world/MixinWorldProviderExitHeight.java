package mctmods.resourcedatapackloader.mixin.world;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;

import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldProvider.class) public abstract class MixinWorldProviderExitHeight implements IExitHeight {
    @Shadow protected World world;

    @Override public double getYCoordinateToTeleport() {
        DimensionDef def = ContentDimensions.byId(world.provider.getDimension());
        if (def != null && def.galacticraft != null && def.galacticraft.exitHeight > 0.0D) { return def.galacticraft.exitHeight; }
        if (GenHeights.rubic(world)) { return GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) + 944; }
        return 1200;
    }
}
