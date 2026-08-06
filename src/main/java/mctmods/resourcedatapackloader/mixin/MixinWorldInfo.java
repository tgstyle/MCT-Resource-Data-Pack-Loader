package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.interfaces.PregenMemory;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo implements PregenMemory {
    @Unique private NBTTagCompound rdpl$landMade = new NBTTagCompound();
    @Unique private NBTTagCompound rdpl$pregenRun = new NBTTagCompound();
    @Unique private NBTTagCompound rdpl$packOptions = new NBTTagCompound();

    @Override public NBTTagCompound rdpl$pregenRun() { return rdpl$pregenRun; }

    @Override public void rdpl$setPregenRun(NBTTagCompound run) { rdpl$pregenRun = run == null ? new NBTTagCompound() : run; }

    @Override public int rdpl$landMadeTo(int dimension) { return rdpl$landMade.getInteger("to" + dimension); }

    @Override public void rdpl$setLandMadeTo(int dimension, int radius) { rdpl$landMade.setInteger("to" + dimension, radius); }

    @Override public int rdpl$landMadeAt(int dimension) { return rdpl$landMade.getInteger("at" + dimension); }

    @Override public void rdpl$setLandMadeAt(int dimension, int reached) { rdpl$landMade.setInteger("at" + dimension, reached); }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("TAIL"))
    private void rdpl$rememberLandMade(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("RDPLLandMade", 10)) { rdpl$landMade = nbt.getCompoundTag("RDPLLandMade"); }
        if (nbt.hasKey("RDPLPregenRun", 10)) { rdpl$pregenRun = nbt.getCompoundTag("RDPLPregenRun"); }
        if (nbt.hasKey("RDPLPackOptions", 10)) { rdpl$packOptions = nbt.getCompoundTag("RDPLPackOptions"); }
        rdpl$comparePackOptions();
    }

    @Inject(method = "updateTagCompound", at = @At("TAIL"))
    private void rdpl$writeLandMade(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo ci) {
        if (!rdpl$landMade.isEmpty()) { nbt.setTag("RDPLLandMade", rdpl$landMade); }
        if (!rdpl$pregenRun.isEmpty()) { nbt.setTag("RDPLPregenRun", rdpl$pregenRun); }
        NBTTagCompound options = new NBTTagCompound();
        for (Map.Entry<String, Boolean> entry : PackOptions.gatingValues().entrySet()) { options.setBoolean(entry.getKey(), entry.getValue()); }
        if (!options.isEmpty()) { nbt.setTag("RDPLPackOptions", options); }
    }

    @Unique private void rdpl$comparePackOptions() {
        List<String> changed = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : PackOptions.gatingValues().entrySet()) {
            if (!rdpl$packOptions.hasKey(entry.getKey())) { continue; }

            boolean was = rdpl$packOptions.getBoolean(entry.getKey());
            if (was == entry.getValue()) { continue; }

            changed.add(entry.getKey() + " (" + (was ? "on" : "off") + " to " + (entry.getValue() ? "on" : "off") + ")");
        }
        PackOptions.worldChanged(changed);
        if (!changed.isEmpty()) { ContentLog.LOGGER.info("Pack options have changed since this world was last played: {}", changed); }
    }

    @Shadow private long randomSeed;
    @Shadow private String generatorOptions;
    @Shadow private WorldType terrainType;
    @Shadow private GameType gameType;
    @Shadow private boolean allowCommands;

    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("TAIL"))
    private void rdpl$shapeTerrain(WorldSettings settings, String name, CallbackInfo ci) {
        PackOptions.worldTold();
        String seed = ContentTerrain.worldSeed();
        if (!seed.isEmpty()) {
            randomSeed = ContentTerrain.seedFrom(seed);
            Summary.info("terrain.seed", "Making every new world with the seed " + seed + ", which is what a pack asks for");
        }

        String mode = ContentTerrain.worldGameMode();
        if (!mode.isEmpty()) {
            GameType asked = ContentTerrain.gameModeFrom(mode);
            if (asked == GameType.NOT_SET) { ContentLog.LOGGER.error("A pack asks for the game mode '{}', which is not one of survival, creative, adventure or spectator, so '{}' is played the way it was chosen", mode, name); }
            else {
                gameType = asked;
                if (asked == GameType.CREATIVE) { allowCommands = true; }

                Summary.info("terrain.gamemode", "Starting every new world in " + asked.getName() + ", which is what a pack asks for");
            }
        }

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
