package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBasin;
import mctmods.resourcedatapackloader.content.worldgen.ContentBelt;
import mctmods.resourcedatapackloader.content.worldgen.ContentDecoration;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeode;
import mctmods.resourcedatapackloader.content.worldgen.ContentImprint;
import mctmods.resourcedatapackloader.content.worldgen.ContentLargeVein;
import mctmods.resourcedatapackloader.content.worldgen.ContentNodule;
import mctmods.resourcedatapackloader.content.worldgen.ContentPlacer;
import mctmods.resourcedatapackloader.content.worldgen.ContentPlate;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpire;
import mctmods.resourcedatapackloader.content.worldgen.ContentVein;
import mctmods.resourcedatapackloader.content.worldgen.ContentVent;
import mctmods.resourcedatapackloader.content.worldgen.ContentVines;
import mctmods.resourcedatapackloader.content.worldgen.ContentTree;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class WorldgenDef {
    public final ResourceLocation registryName;
    public final ResourceLocation block;
    public final int meta;
    public final List<BlockWeightDef> blocks;
    public final AmountDef size;
    public final AmountDef attempts;
    public final int minHeight;
    public final int maxHeight;
    public final List<BlockMatchDef> replaces;
    public final boolean sparse;
    public final ShapeDef shape;
    public final float leastTemperature;
    public final float mostTemperature;
    public final float leastRainfall;
    public final float mostRainfall;
    public final List<Integer> dimensions;
    public final boolean dimensionsAreBlacklist;
    public final List<String> biomes;
    public final List<String> biomeTypes;
    public final boolean biomesAreBlacklist;
    public final List<String> requires;
    public final boolean retrogen;
    public final String retrogenKey;
    public final int minDistanceFromSpawn;
    public final SpreadDef spread;
    private String token;
    @Nullable private IBlockState state;
    @Nullable private IContentShape figure;
    private Set<String> biomeNames = Collections.emptySet();
    private List<BiomeDictionary.Type> types = Collections.emptyList();

    public WorldgenDef(ResourceLocation registryName, ResourceLocation block, int meta, List<BlockWeightDef> blocks, AmountDef size, AmountDef attempts, int minHeight, int maxHeight, List<BlockMatchDef> replaces, boolean sparse, List<Integer> dimensions, boolean dimensionsAreBlacklist, List<String> biomes, List<String> biomeTypes, boolean biomesAreBlacklist, List<String> requires, boolean retrogen, String retrogenKey, int minDistanceFromSpawn, SpreadDef spread, ShapeDef shape, float leastTemperature, float mostTemperature, float leastRainfall, float mostRainfall) {
        this.registryName = registryName;
        this.block = block;
        this.meta = meta;
        this.blocks = blocks;
        this.size = size;
        this.attempts = attempts;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.replaces = replaces;
        this.sparse = sparse;
        this.dimensions = dimensions;
        this.dimensionsAreBlacklist = dimensionsAreBlacklist;
        this.biomes = biomes;
        this.biomeTypes = biomeTypes;
        this.biomesAreBlacklist = biomesAreBlacklist;
        this.requires = requires;
        this.retrogen = retrogen;
        this.retrogenKey = retrogenKey;
        this.minDistanceFromSpawn = minDistanceFromSpawn;
        this.spread = spread;
        this.shape = shape;
        this.leastTemperature = leastTemperature;
        this.mostTemperature = mostTemperature;
        this.leastRainfall = leastRainfall;
        this.mostRainfall = mostRainfall;
        this.token = registryName.toString();
    }

    public void buildToken(String globalKey) { token = registryName + "#" + globalKey.trim() + retrogenKey.trim(); }

    public String getToken() { return token; }

    public void resolve(List<IBlockState> states, List<Integer> weights, Set<Block> targets, Set<IBlockState> exact, Set<Block> surface, @Nullable IBlockState outline, @Nullable IBlockState fill) {
        this.state = states.isEmpty() ? null : states.get(0);
        this.figure = states.isEmpty() ? null : build(new ContentPlacer(states, weights, targets, exact), surface, outline, fill);
        this.biomeNames = biomes.isEmpty() ? Collections.emptySet() : new HashSet<>(biomes);

        if (biomeTypes.isEmpty()) { return; }
        List<BiomeDictionary.Type> resolved = new ArrayList<>(biomeTypes.size());
        for (String name : biomeTypes) { resolved.add(BiomeDictionary.Type.getType(name)); }
        this.types = resolved;
    }

    @Nullable public IContentShape getShape() { return figure; }

    private IContentShape build(ContentPlacer placer, Set<Block> surface, @Nullable IBlockState outline, @Nullable IBlockState fill) {
        if (ShapeDef.PLATE.equals(shape.type)) { return new ContentPlate(placer, shape); }
        if (ShapeDef.GEODE.equals(shape.type) && outline != null) { return new ContentGeode(placer, shape, outline, fill); }
        if (ShapeDef.LARGEVEIN.equals(shape.type)) { return new ContentLargeVein(placer, size, sparse, shape.slim); }
        if (ShapeDef.DECORATION.equals(shape.type)) { return new ContentDecoration(placer, size, shape, surface); }
        if (ShapeDef.TREE.equals(shape.type)) { return new ContentTree(size, shape, surface, registryName); }
        if (ShapeDef.VINES.equals(shape.type)) { return new ContentVines(placer, size, shape); }
        if (ShapeDef.BASIN.equals(shape.type)) { return new ContentBasin(placer, shape); }
        if (ShapeDef.SPIRE.equals(shape.type)) { return new ContentSpire(placer, shape); }
        if (ShapeDef.NODULE.equals(shape.type)) { return new ContentNodule(placer, shape); }
        if (ShapeDef.VENT.equals(shape.type)) { return new ContentVent(placer, shape); }
        if (ShapeDef.IMPRINT.equals(shape.type)) { return new ContentImprint(shape, registryName); }
        if (ShapeDef.BELT.equals(shape.type)) { return new ContentBelt(placer, shape, minHeight, maxHeight, registryName); }

        return new ContentVein(placer, size, sparse);
    }

    public boolean climateAllows(float temperature, float rainfall) {
        return temperature >= leastTemperature && temperature <= mostTemperature
                && rainfall >= leastRainfall && rainfall <= mostRainfall;
    }

    public Set<String> getBiomeNames() { return biomeNames; }

    public List<BiomeDictionary.Type> getTypes() { return types; }

    @Nullable public IBlockState getState() { return state; }

    public boolean hasBiomeFilter() { return !biomes.isEmpty() || !biomeTypes.isEmpty(); }
}
