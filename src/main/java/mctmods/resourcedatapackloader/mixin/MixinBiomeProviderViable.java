package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(BiomeProvider.class)
public abstract class MixinBiomeProviderViable {
    @Redirect(method = "areBiomesViable", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean rdpl$viableAfterSubstitution(List<Biome> allowed, Object held) { return ContentBiomeControl.viable((BiomeProvider) (Object) this, (Biome) held, allowed); }
}
