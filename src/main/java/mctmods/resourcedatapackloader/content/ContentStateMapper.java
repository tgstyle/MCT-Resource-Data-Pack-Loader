package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public final class ContentStateMapper implements IStateMapper {
    private final BlockDef def;
    private final PropertyVariant property;
    private final ResourceLocation registryName;
    private final Set<String> ignored;

    public ContentStateMapper(BlockDef def, PropertyVariant property, ResourceLocation registryName, Set<String> ignored) {
        this.def = def;
        this.property = property;
        this.registryName = registryName;
        this.ignored = ignored;
    }

    @Override @Nonnull public Map<IBlockState, ModelResourceLocation> putStateModelLocations(@Nonnull Block block) {
        Map<IBlockState, ModelResourceLocation> locations = new LinkedHashMap<>();
        String fallback = fallback();

        for (IBlockState state : block.getBlockState().getValidStates()) {
            locations.put(state, new ModelResourceLocation(registryName, variantString(state, fallback)));
        }
        return locations;
    }

    public String variantFor(IBlockState state) { return variantString(state, fallback()); }

    private String variantString(IBlockState state, String fallback) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
            IProperty<?> key = entry.getKey();
            if (ignored.contains(key.getName())) { continue; }
            if (builder.length() != 0) { builder.append(','); }
            builder.append(key.getName()).append('=').append(value(key, entry.getValue(), fallback));
        }

        return builder.length() == 0 ? "normal" : builder.toString();
    }

    private String value(IProperty<?> key, Comparable<?> value, String fallback) {
        if (key != property) { return name(key, value); }

        String variant = (String) value;
        return def.byMeta[ContentSetup.metaOf(def, variant)].hidden ? fallback : variant;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String name(IProperty<T> key, Comparable<?> value) { return key.getName((T) value); }

    private String fallback() {
        for (BlockVariant variant : def.visible) { return variant.name; }
        return def.at(0).name;
    }
}
