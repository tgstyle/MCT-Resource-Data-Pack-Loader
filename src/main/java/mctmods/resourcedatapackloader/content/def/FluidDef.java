package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import java.util.List;

public record FluidDef(ResourceLocation key, String name, int color, ResourceLocation still, ResourceLocation flowing, int temperature, int density, int viscosity, int luminosity, boolean gaseous, boolean bucket,
                       boolean createBlock, String material, String creativeTab, int flammability, int fireSpread, int quantaPerBlock, List<String> potions, List<String> requires) {
    private static final int OPAQUE = 0xFF000000;
    private static final int LEGACY_WATER = 0x4260FF;

    public ResourceLocation id() { return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), name); }

    public boolean waterMaterial() { return "water".equals(material); }

    public boolean lavaMaterial() { return "lava".equals(material); }

    public int tint() {
        if (!ContentParser.DEFAULT_STILL.equals(still.toString())) { return color | OPAQUE; }
        int red = FastColor.ARGB32.red(color) * FastColor.ARGB32.red(LEGACY_WATER) / 0xFF;
        int green = FastColor.ARGB32.green(color) * FastColor.ARGB32.green(LEGACY_WATER) / 0xFF;
        int blue = FastColor.ARGB32.blue(color) * FastColor.ARGB32.blue(LEGACY_WATER) / 0xFF;
        return OPAQUE | red << 16 | green << 8 | blue;
    }
}
