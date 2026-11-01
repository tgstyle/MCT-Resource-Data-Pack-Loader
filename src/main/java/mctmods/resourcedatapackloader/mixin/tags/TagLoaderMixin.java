package mctmods.resourcedatapackloader.mixin.tags;

import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TagLoader.class) public abstract class TagLoaderMixin<T> {
    @Shadow @Final private String directory;
    @Shadow @Final private TagLoader.ElementLookup<T> elementLookup;

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN")) private void rdpl$disable(Map<Identifier, List<TagLoader.EntryWithSource>> builders, CallbackInfoReturnable<Map<Identifier, List<T>>> cir) { ContentDisabled.strip(directory, cir.getReturnValue(), id -> elementLookup.get(id, false)); }
}
