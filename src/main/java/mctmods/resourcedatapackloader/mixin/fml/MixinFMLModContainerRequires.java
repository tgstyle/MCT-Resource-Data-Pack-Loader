package mctmods.resourcedatapackloader.mixin.fml;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.pack.PackRequirements;

import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = FMLModContainer.class, remap = false) public abstract class MixinFMLModContainerRequires {
    @Unique private boolean rdpl$notLoader() { return !ResourceDataPackLoader.MOD_ID.equals(((FMLModContainer) (Object) this).getModId()); }

    @Inject(method = "getRequirements", at = @At("RETURN"), cancellable = true) private void rdpl$addPackRequirements(CallbackInfoReturnable<Set<ArtifactVersion>> cir) {
        if (rdpl$notLoader()) { return; }
        Set<String> wanted = PackRequirements.required();
        if (wanted.isEmpty()) { return; }
        Set<ArtifactVersion> requirements = new LinkedHashSet<>(cir.getReturnValue() == null ? Collections.emptySet() : cir.getReturnValue());
        for (String modid : wanted) { requirements.add(VersionParser.parseVersionReference(modid)); }
        cir.setReturnValue(requirements);
    }

    @Inject(method = "getDependencies", at = @At("RETURN"), cancellable = true) private void rdpl$addPackDependencies(CallbackInfoReturnable<List<ArtifactVersion>> cir) {
        if (rdpl$notLoader()) { return; }
        Set<String> wanted = PackRequirements.required();
        if (wanted.isEmpty()) { return; }
        List<ArtifactVersion> dependencies = new ArrayList<>(cir.getReturnValue() == null ? Collections.emptyList() : cir.getReturnValue());
        for (String modid : wanted) { dependencies.add(VersionParser.parseVersionReference(modid)); }
        cir.setReturnValue(dependencies);
    }
}
