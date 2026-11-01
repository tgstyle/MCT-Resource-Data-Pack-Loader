package mctmods.resourcedatapackloader.mixin.world.level.levelgen.structure.templatesystem;

import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplateManager.class) public abstract class StructureTemplateManagerMixin {
    @Unique private static final StructureTemplate rdpl$EMPTY = new StructureTemplate();

    @Inject(method = "get", at = @At("RETURN"), cancellable = true) private void rdpl$emptied(Identifier id, CallbackInfoReturnable<Optional<StructureTemplate>> cir) {
        if (cir.getReturnValue().isEmpty() || !ContentVillages.emptied(id)) { return; }
        ContentLog.LOGGER.debug("Structure {} is left empty by the pack's piece list, so whatever asked for it places nothing", id);
        cir.setReturnValue(Optional.of(rdpl$EMPTY));
    }

    @Inject(method = "getOrCreate", at = @At("RETURN"), cancellable = true) private void rdpl$emptiedJigsaw(Identifier id, CallbackInfoReturnable<StructureTemplate> cir) { if (ContentVillages.emptied(id)) { cir.setReturnValue(rdpl$EMPTY); } }
}
