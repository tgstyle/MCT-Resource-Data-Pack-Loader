package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.compat.StreamsRubicValleys;

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;

@Mixin(ForgeEventFactory.class) public class MixinForgeEventFactory {
    /**
     * @author tgstyle
     * @reason Route the pre-populate event through the per-mod fake world height poster.
     */
    @Overwrite(remap = false) public static void onChunkPopulate(boolean pre, IChunkGenerator gen, World world, Random rand, int x, int z, boolean hasVillageGenerated) {
        if (pre) {
            StreamsRubicValleys.carveAhead(world, x, z);
            CompatHandler.postChunkPopulatePreWithFakeWorldHeight(new PopulateChunkEvent.Pre(gen, world, rand, x, z, hasVillageGenerated));
        }
        else { MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(gen, world, rand, x, z, hasVillageGenerated)); }
    }
}
