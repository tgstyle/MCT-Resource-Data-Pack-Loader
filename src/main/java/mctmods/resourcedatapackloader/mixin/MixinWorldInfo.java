package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo {
    @Shadow private String generatorOptions;
    @Shadow private WorldType terrainType;

    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("TAIL"))
    private void rdpl$shapeTerrain(WorldSettings settings, String name, CallbackInfo ci) {
        String wanted = ContentTerrain.worldType();
        boolean asked = !wanted.isEmpty() && (terrainType == null || !wanted.equalsIgnoreCase(terrainType.getName()));
        if (asked && terrainType != null && ContentTerrain.keeps(terrainType.getName())) {
            ContentLog.LOGGER.info("'{}' was made a {} world, which a pack leaves alone, so it is not made a {} world", name, terrainType.getName(), wanted);
            asked = false;
        }
        if (asked) {
            boolean made = false;
            for (WorldType type : WorldType.WORLD_TYPES) {
                if (type == null || !wanted.equalsIgnoreCase(type.getName())) { continue; }

                terrainType = type;
                generatorOptions = "";
                made = true;
                Summary.info("terrain.worldtype", "Making every new world a " + type.getName() + " world, which is what a pack asks for");
                break;
            }
            if (!made) { ContentLog.LOGGER.error("A pack asks for the world type '{}', which nothing here provides, so '{}' is made the way it was chosen", wanted, name); }
        }

        String options = ContentTerrain.merge(generatorOptions, terrainType == null ? "" : terrainType.getName());
        if (options.isEmpty() || options.equals(generatorOptions)) { return; }

        generatorOptions = options;
        Summary.info("terrain.shaped", "Shaping the overworld of '" + name + "' with " + options);
    }
}
