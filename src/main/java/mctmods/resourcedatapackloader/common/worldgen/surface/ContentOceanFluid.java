package mctmods.resourcedatapackloader.common.worldgen.surface;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class ContentOceanFluid {
    private static final Map<World, IBlockState> SEAS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Set<String> UNUSABLE = new HashSet<>();

    private ContentOceanFluid() {}

    public static IBlockState of(World world) {
        IBlockState sea = SEAS.get(world);
        return sea == null ? Blocks.WATER.getDefaultState() : sea;
    }

    @Nullable public static IBlockState chosen(World world, @Nullable String options) {
        IBlockState sea = resolve(options);
        if (sea != null) { SEAS.put(world, sea); }
        return sea;
    }

    @Nullable private static IBlockState resolve(@Nullable String options) {
        if (options == null || options.isEmpty()) { return null; }
        JsonObject json = Json.parseObject(options);
        if (json == null) { return null; }
        JsonElement asked = json.get("oceanFluid");
        if (asked == null || !asked.isJsonPrimitive()) { return null; }
        String id = asked.getAsString().trim();
        if (id.isEmpty()) { return null; }
        return fluidBlock(id, "oceanFluid");
    }

    @Nullable public static IBlockState fluidBlock(String id, String owner) {
        ResourceLocation key = new ResourceLocation(id);
        FluidDef def = ContentRegistry.fluidDef(key);
        Fluid fluid = FluidRegistry.getFluid(def == null ? key.getPath() : def.name);
        Block block = fluid == null ? null : fluid.getBlock();
        if (block != null) { return block.getDefaultState(); }
        ContentLog.LOGGER.errorOnce(UNUSABLE, owner + id, "The {} '{}' is not a loaded fluid with a block, so the fallback for it is used instead; this is said once", owner, id);
        return null;
    }
}
