package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.ContentParser;

import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import java.util.List;

public record FluidDef(Identifier key, String name, int color, Identifier still, Identifier flowing, int temperature, int density, int viscosity, int luminosity, boolean gaseous, boolean bucket,
                       boolean createBlock, String material, String creativeTab, int flammability, int fireSpread, int quantaPerBlock, List<String> potions, List<String> requires) {
    private static final int OPAQUE = 0xFF000000;
    private static final int LEGACY_WATER = 0x4260FF;

    public Identifier id() { return Identifier.fromNamespaceAndPath(key.getNamespace(), name); }

    public boolean waterMaterial() { return "water".equals(material); }

    public boolean lavaMaterial() { return "lava".equals(material); }

    public int tint() {
        if (!ContentParser.DEFAULT_STILL.equals(still.toString())) { return color | OPAQUE; }
        int red = ARGB.red(color) * ARGB.red(LEGACY_WATER) / 0xFF;
        int green = ARGB.green(color) * ARGB.green(LEGACY_WATER) / 0xFF;
        int blue = ARGB.blue(color) * ARGB.blue(LEGACY_WATER) / 0xFF;
        return OPAQUE | red << 16 | green << 8 | blue;
    }
}
