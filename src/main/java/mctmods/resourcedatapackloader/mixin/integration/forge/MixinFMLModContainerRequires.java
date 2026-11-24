package mctmods.resourcedatapackloader.mixin.integration.forge;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.definitions.PackRequirements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FMLModContainer.class, remap = false) public abstract class MixinFMLModContainerRequires {
    @Unique private boolean rdpl$notLoader() { return !ResourceDataPackLoader.MOD_ID.equals(((FMLModContainer) (Object) this).getModId()); }

    @Inject(method = "getRequirements", at = @At("RETURN"), cancellable = true) private void rdpl$addPackRequirements(CallbackInfoReturnable<Set<ArtifactVersion>> cir) {
        if (rdpl$notLoader()) { return; }
        Set<String> wanted = PackRequirements.required();
        if (wanted.isEmpty()) { return; }
        cir.setReturnValue(rdpl$withPackRequirements(new LinkedHashSet<>(cir.getReturnValue() == null ? Collections.emptySet() : cir.getReturnValue()), wanted));
    }

    @Inject(method = "getDependencies", at = @At("RETURN"), cancellable = true) private void rdpl$addPackDependencies(CallbackInfoReturnable<List<ArtifactVersion>> cir) {
        if (rdpl$notLoader()) { return; }
        Set<String> wanted = PackRequirements.required();
        if (wanted.isEmpty()) { return; }
        cir.setReturnValue(rdpl$withPackRequirements(new ArrayList<>(cir.getReturnValue() == null ? Collections.emptyList() : cir.getReturnValue()), wanted));
    }

    @Unique private static <C extends Collection<ArtifactVersion>> C rdpl$withPackRequirements(C copy, Set<String> wanted) {
        for (String modid : wanted) { copy.add(VersionParser.parseVersionReference(modid)); }
        return copy;
    }
}
