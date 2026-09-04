package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TerrainGen.class) public class MixinTerrainGen {
    @Redirect(method = "decorate(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraftforge/event/terraingen/DecorateBiomeEvent$Decorate$EventType;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", remap = false), remap = false)
    private static boolean rdpl$postWithFakeWorldHeight(EventBus bus, Event event) {
        CompatHandler.postBiomeDecorateWithFakeWorldHeight((DecorateBiomeEvent.Decorate) event);
        return false;
    }
}
