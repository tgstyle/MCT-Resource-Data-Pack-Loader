package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
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
        Fluid fluid = Registered.find(ForgeRegistries.FLUIDS, ResourceLocation.tryParse(Settings.lower(oceanFluid)));
        if (fluid instanceof FlowingFluid flowing) { fluid = flowing.getSource(); }
        BlockState block = fluid == null ? null : fluid.defaultFluidState().createLegacyBlock();
        ResourceLocation id = block != null && block.getBlock() instanceof LiquidBlock ? ForgeRegistries.BLOCKS.getKey(block.getBlock()) : null;
        if (id != null) { return id.toString(); }
        ContentLog.LOGGER.errorOnce(UNUSABLE, owner + "|" + oceanFluid, "{} names oceanFluid '{}', which is not a loaded fluid with a liquid block, so the oceans are made of what useLavaOceans says instead", owner, oceanFluid);
        return null;
    }
}
