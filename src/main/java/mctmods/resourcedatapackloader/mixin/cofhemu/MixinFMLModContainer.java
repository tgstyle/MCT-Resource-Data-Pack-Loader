package mctmods.resourcedatapackloader.mixin.cofhemu;

import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = FMLModContainer.class, remap = false)
public abstract class MixinFMLModContainer {
    @Unique private static final Set<String> RDPL$USES_API = new HashSet<>(Collections.singletonList("betweenores"));
    @Unique private static Boolean rdpl$enabled;

    @Unique private static boolean rdpl$disabled() {
        if (rdpl$enabled == null) { rdpl$enabled = !rdpl$installed(); }

        return !rdpl$enabled;
    }

    @Unique private boolean rdpl$needsApi() { return RDPL$USES_API.contains(((FMLModContainer) (Object) this).getModId()); }

    @Unique private static boolean rdpl$installed() {
        for (ModContainer container : Loader.instance().getModList()) {
            if ("cofhworld".equals(container.getModId())) { return true; }
        }
        return false;
    }

    @Inject(method = "getRequirements", at = @At("RETURN"), cancellable = true)
    private void rdpl$dropCofhWorld(CallbackInfoReturnable<Set<ArtifactVersion>> cir) {
        if (rdpl$disabled() || rdpl$needsApi()) { return; }

        Set<ArtifactVersion> requirements = cir.getReturnValue();
        if (requirements == null || requirements.isEmpty()) { return; }

        Set<ArtifactVersion> kept = requirements.stream().filter(version -> !"cofhworld".equals(version.getLabel())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (kept.size() != requirements.size()) { cir.setReturnValue(kept); }
    }

    @Inject(method = "getDependencies", at = @At("RETURN"), cancellable = true)
    private void rdpl$dropCofhWorldDependency(CallbackInfoReturnable<List<ArtifactVersion>> cir) {
        if (rdpl$disabled() || rdpl$needsApi()) { return; }

        List<ArtifactVersion> dependencies = cir.getReturnValue();
        if (dependencies == null || dependencies.isEmpty()) { return; }

        List<ArtifactVersion> kept = dependencies.stream().filter(version -> !"cofhworld".equals(version.getLabel())).collect(Collectors.toList());
        if (kept.size() != dependencies.size()) { cir.setReturnValue(kept); }
    }
}
