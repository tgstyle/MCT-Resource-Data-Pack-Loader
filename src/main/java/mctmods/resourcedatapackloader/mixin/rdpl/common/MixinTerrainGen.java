package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;

@Mixin(TerrainGen.class) public class MixinTerrainGen {
    /**
     * @author tgstyle
     * @reason Route the decorate event through the per-mod fake world height poster.
     */
    @Overwrite(remap = false) public static boolean decorate(World world, Random rand, ChunkPos chunkPos, DecorateBiomeEvent.Decorate.EventType type)
    {
        DecorateBiomeEvent.Decorate event = new DecorateBiomeEvent.Decorate(world, rand, chunkPos, null, type);
        CompatHandler.postBiomeDecorateWithFakeWorldHeight(event);
        return event.getResult() != Event.Result.DENY;
    }
}
