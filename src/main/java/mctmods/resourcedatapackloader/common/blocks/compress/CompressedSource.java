package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("deprecation") public final class CompressedSource {
    public final ResourceLocation baseBlock;
    public final int baseMeta;
    public final Material material;
    public final MapColor mapColor;
    @Nullable public final SoundType sound;
    public final String tool;
    public final int toolLevel;
    public final int harvestLevel;
    public final float hardness;
    public final float resistance;
    public final String creativeTab;
    public final List<String> requires;

    private CompressedSource(ResourceLocation baseBlock, int baseMeta, Material material, MapColor mapColor, @Nullable SoundType sound, String tool, int toolLevel, int harvestLevel, float hardness, float resistance, String creativeTab, List<String> requires) {
        this.baseBlock = baseBlock;
        this.baseMeta = baseMeta;
        this.material = material;
        this.mapColor = mapColor;
        this.sound = sound;
        this.tool = tool;
        this.toolLevel = toolLevel;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.resistance = resistance;
        this.creativeTab = creativeTab;
        this.requires = requires;
    }

    @Nullable static CompressedSource of(CompressableDef def, Map<ResourceLocation, BlockDef> defs) { return def.of == null ? pack(def, defs) : foreign(def, def.of); }

    @Nullable private static CompressedSource pack(CompressableDef def, Map<ResourceLocation, BlockDef> defs) {
        BlockDef base = defs.get(def.key);
        if (base == null) {
            ContentLog.LOGGER.error("The compressable block {} did not load, so it gets no compressed layers", def.key);
            return null;
        }
        BlockVariant first = null;
        for (BlockVariant variant : base.visible) {
            if (first == null || variant.meta < first.meta) { first = variant; }
        }
        if (first == null) {
            ContentLog.LOGGER.error("The compressable block {} has no variants, so it gets no compressed layers", def.key);
            return null;
        }
        return new CompressedSource(def.key, first.meta, base.material, base.mapColor, base.soundType, base.harvestTool, base.harvestToolLevel, first.harvestLevel, first.hardness, first.resistance / base.explosionResistanceDivisor, base.creativeTab, base.requires);
    }

    private static String tab(Block block) {
        CreativeTabs tab = FMLCommonHandler.instance().getSide() == Side.CLIENT ? block.getCreativeTab() : null;
        return tab == null ? "" : tab.getTabLabel();
    }

    @Nullable private static CompressedSource foreign(CompressableDef def, ResourceLocation name) {
        String namespace = name.getNamespace();
        List<String> requires = "minecraft".equals(namespace) ? Collections.emptyList() : Collections.singletonList(namespace);
        if (!ContentRegistry.holds(requires, def.key)) { return null; }
        Block block = Registries.find(ForgeRegistries.BLOCKS, name);
        if (block == null) {
            ContentLog.LOGGER.error("The compressable file {} names block {}, which is not registered, so it gets no compressed layers", def.key, name);
            return null;
        }
        IBlockState state = block.getStateFromMeta(def.ofMeta);
        String tool = block.getHarvestTool(state);
        return new CompressedSource(name, def.ofMeta, state.getMaterial(), state.getMaterial().getMaterialMapColor(), block.blockSoundType, tool == null ? "" : tool, Math.max(0, block.getHarvestLevel(state)), Math.max(0, block.getHarvestLevel(state)), block.blockHardness, block.blockResistance / 5.0F, tab(block), requires);
    }
}
