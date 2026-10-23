package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class OceanFluid {
    private static final Set<String> UNUSABLE = ConcurrentHashMap.newKeySet();

    private OceanFluid() {}

    @Nullable public static JsonObject state(String oceanFluid, boolean lavaOceans, String owner) {
        String block = oceanFluid.isEmpty() ? null : blockOf(oceanFluid, owner);
        if (block == null && lavaOceans) { block = "minecraft:lava"; }
        if (block == null) { return null; }
        JsonObject state = new JsonObject();
        state.addProperty("Name", block);
        JsonObject properties = new JsonObject();
        properties.addProperty("level", "0");
        state.add("Properties", properties);
        return state;
    }

    @Nullable private static String blockOf(String oceanFluid, String owner) {
        Fluid fluid = Registered.find(BuiltInRegistries.FLUID, Identifier.tryParse(Settings.lower(oceanFluid)));
        if (fluid instanceof FlowingFluid flowing) { fluid = flowing.getSource(); }
        BlockState block = fluid == null ? null : fluid.defaultFluidState().createLegacyBlock();
        if (block != null && block.getBlock() instanceof LiquidBlock) { return BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString(); }
        ContentLog.LOGGER.errorOnce(UNUSABLE, owner + "|" + oceanFluid, "{} names oceanFluid '{}', which is not a loaded fluid with a liquid block, so the oceans are made of what useLavaOceans says instead", owner, oceanFluid);
        return null;
    }
}
