package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.compat.StreamsRubicValleys;

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(ForgeEventFactory.class) public class MixinForgeEventFactory {
    @Redirect(method = "onChunkPopulate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", remap = false), remap = false)
    private static boolean rdpl$postWithFakeWorldHeight(EventBus bus, Event event, boolean pre, IChunkGenerator gen, World world, Random rand, int x, int z, boolean hasVillageGenerated) {
        if (!(event instanceof PopulateChunkEvent.Pre)) { return bus.post(event); }
        StreamsRubicValleys.carveAhead(world, x, z);
        CompatHandler.postChunkPopulatePreWithFakeWorldHeight((PopulateChunkEvent.Pre) event);
        return false;
    }
}
