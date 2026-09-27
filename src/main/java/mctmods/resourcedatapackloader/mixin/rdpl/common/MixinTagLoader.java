package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class) public abstract class MixinTagLoader<T> {
    @Shadow @Final private String directory;
    @Shadow @Final private TagLoader.ElementLookup<T> elementLookup;

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN")) private void rdpl$disable(Map<Identifier, List<TagLoader.EntryWithSource>> builders, CallbackInfoReturnable<Map<Identifier, List<T>>> cir) { ContentDisabled.strip(directory, cir.getReturnValue(), id -> elementLookup.get(id, false)); }
}
