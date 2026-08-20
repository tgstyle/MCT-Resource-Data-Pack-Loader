package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BiomeDecorator.class) public class MixinBiomeDecorator {
    @Redirect(method = "genDecorations",
            slice = @Slice(
                    from = @At(value = "NEW", target = "net/minecraftforge/event/terraingen/DecorateBiomeEvent$Post", remap = false),
                    to = @At(value = "TAIL")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z",
                    remap = false
            )
    )
    private boolean postEvent(EventBus eventBus, Event event) { return CompatHandler.postBiomePostDecorateWithFakeWorldHeight((DecorateBiomeEvent.Post) event); }
}
