package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

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
        JsonElement asked;
        try { asked = new JsonParser().parse(options).getAsJsonObject().get("oceanFluid"); }
        catch (RuntimeException notJson) { return null; }
        if (asked == null || !asked.isJsonPrimitive()) { return null; }
        String id = asked.getAsString().trim();
        if (id.isEmpty()) { return null; }
        ResourceLocation key = new ResourceLocation(id);
        FluidDef def = ContentRegistry.fluidDef(key);
        Fluid fluid = FluidRegistry.getFluid(def == null ? key.getPath() : def.name);
        Block block = fluid == null ? null : fluid.getBlock();
        if (block != null) { return block.getDefaultState(); }
        ContentLog.LOGGER.errorOnce(UNUSABLE, id, "The oceanFluid '{}' is not a loaded fluid with a block, so the oceans are made of what useLavaOceans says instead; this is said once", id);
        return null;
    }
}
