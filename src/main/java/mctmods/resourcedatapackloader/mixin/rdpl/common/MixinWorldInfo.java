package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IPregenMemory;
import mctmods.resourcedatapackloader.content.interfaces.IVoidMemory;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldSettings;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;
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

@Mixin(WorldInfo.class) public abstract class MixinWorldInfo implements IPregenMemory, IRubicWorldSettings, IVoidMemory {
    @Unique private NBTTagCompound rdpl$landMade = new NBTTagCompound();
    @Unique private NBTTagCompound rdpl$pregenRun = new NBTTagCompound();
    @Unique private NBTTagCompound rdpl$packOptions = new NBTTagCompound();
    @Unique private NBTTagCompound rdpl$voidMade = new NBTTagCompound();

    @Override public boolean rdpl$voidRecorded() { return rdpl$voidMade.hasKey("enabled"); }

    @Override public boolean rdpl$voidAppliesTo(int dimension) {
        if (!rdpl$voidMade.getBoolean("enabled")) { return false; }
        boolean listed = false;
        for (int held : rdpl$voidMade.getIntArray("dimensions")) {
            if (held == dimension) {
                listed = true;
                break;
            }
        }
        return listed != rdpl$voidMade.getBoolean("areBlacklist");
    }

    @Override public void rdpl$recordVoid(boolean enabled, int[] dimensions, boolean areBlacklist) {
        rdpl$voidMade.setBoolean("enabled", enabled);
        rdpl$voidMade.setIntArray("dimensions", dimensions);
        rdpl$voidMade.setBoolean("areBlacklist", areBlacklist);
    }

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
        if (nbt.hasKey("RDPLVoidWorld", 10)) { rdpl$voidMade = nbt.getCompoundTag("RDPLVoidWorld"); }
        rdpl$comparePackOptions();
    }

    @Inject(method = "updateTagCompound", at = @At("TAIL")) private void rdpl$writeLandMade(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo ci) {
        if (!rdpl$landMade.isEmpty()) { nbt.setTag("RDPLLandMade", rdpl$landMade); }
        if (!rdpl$pregenRun.isEmpty()) { nbt.setTag("RDPLPregenRun", rdpl$pregenRun); }
        if (!rdpl$voidMade.isEmpty()) { nbt.setTag("RDPLVoidWorld", rdpl$voidMade); }
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
    @Shadow private boolean hardcore;

    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("TAIL"))
    private void rdpl$shapeTerrain(WorldSettings settings, String name, CallbackInfo ci) {
        PackOptions.worldTold();
        ContentVoidWorld.record(this);
        String seed = ContentTerrain.worldSeed();
        if (!seed.isEmpty()) {
            randomSeed = ContentTerrain.seedFrom(seed);
            Summary.info("terrain.seed", "Making every new world with the seed " + seed + ", which is what a pack asks for");
        }
        String mode = ContentTerrain.worldGameMode();
        if (!mode.isEmpty()) {
            GameType asked = ContentTerrain.gameModeFrom(mode);
            if (asked == GameType.NOT_SET) { ContentLog.LOGGER.error("A pack asks for the game mode '{}', which is not one of survival, hardcore, creative, adventure or spectator, so '{}' is played the way it was chosen", mode, name); }
            else {
                gameType = asked;
                if (asked == GameType.CREATIVE) { allowCommands = true; }
                if (ContentTerrain.hardcoreAsked()) { hardcore = true; }
                Summary.info("terrain.gamemode", "Starting every new world in " + (ContentTerrain.hardcoreAsked() ? "hardcore" : asked.getName()) + ", which is what a pack asks for");
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

    @Unique private boolean rdpl$isRubic;

    @Inject(method = "populateFromWorldSettings", at = @At("RETURN")) private void onConstructWithSettings(WorldSettings settings, CallbackInfo cbi) {
        this.rdpl$isRubic = ((IRubicWorldSettings) (Object) settings).rdpl$isRubic();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At("RETURN"))
    private void onConstructWithSettings(WorldInfo worldInformation, CallbackInfo cbi) { this.rdpl$isRubic = ((IRubicWorldSettings) worldInformation).rdpl$isRubic(); }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void onConstructWithSettings(NBTTagCompound nbt, CallbackInfo cbi) { this.rdpl$isRubic = nbt.getBoolean("isRubicWorld"); }

    @Inject(method = "updateTagCompound", at = @At("RETURN")) private void onConstructWithSettings(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo cbi) {
        nbt.setBoolean("isRubicWorld", rdpl$isRubic);
    }

    @Override public boolean rdpl$isRubic() { return rdpl$isRubic; }

    @Override public void rdpl$setRubic(boolean rubic) { this.rdpl$isRubic = rubic; }
}
