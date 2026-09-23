package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentVillages;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(StructureTemplateManager.class) public abstract class MixinStructureTemplateManager {
    @Unique private static final StructureTemplate rdpl$EMPTY = new StructureTemplate();

    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void rdpl$emptied(ResourceLocation id, CallbackInfoReturnable<Optional<StructureTemplate>> cir) {
        if (cir.getReturnValue().isEmpty() || !ContentVillages.emptied(id)) { return; }
        ContentLog.LOGGER.debug("Structure {} is left empty by the pack's piece list, so whatever asked for it places nothing", id);
        cir.setReturnValue(Optional.of(rdpl$EMPTY));
    }

    @Inject(method = "getOrCreate", at = @At("RETURN"), cancellable = true)
    private void rdpl$emptiedJigsaw(ResourceLocation id, CallbackInfoReturnable<StructureTemplate> cir) {
        if (ContentVillages.emptied(id)) { cir.setReturnValue(rdpl$EMPTY); }
    }
}
