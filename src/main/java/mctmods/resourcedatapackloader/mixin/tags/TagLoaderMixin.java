package mctmods.resourcedatapackloader.mixin.tags;

import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TagLoader.class) public abstract class TagLoaderMixin<T> {
    @Shadow @Final private String directory;
    @Shadow @Final Function<ResourceLocation, Optional<? extends T>> idToValue;

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN")) private void rdpl$disable(Map<ResourceLocation, List<TagLoader.EntryWithSource>> builders, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> cir) { ContentDisabled.strip(directory, cir.getReturnValue(), idToValue); }
}
