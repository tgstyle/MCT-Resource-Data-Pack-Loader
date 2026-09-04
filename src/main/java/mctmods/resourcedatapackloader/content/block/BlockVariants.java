package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

public final class BlockVariants {
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final ThreadLocal<PropertyVariant> PROPERTY = new ThreadLocal<>();

    private BlockVariants() {}

    public static void begin(BlockDef def) { CONSTRUCTING.set(def); }

    public static void begin(BlockDef def, PropertyVariant property) {
        CONSTRUCTING.set(def);
        PROPERTY.set(property);
    }

    public static void end() {
        CONSTRUCTING.remove();
        PROPERTY.remove();
    }

    public static BlockDef def() { return CONSTRUCTING.get(); }

    public static PropertyVariant property() { return PROPERTY.get(); }

    public static PropertyVariant property(PropertyVariant property) {
        PROPERTY.set(property);
        return property;
    }

    public static PropertyVariant fresh() { return property(new PropertyVariant(ContentSetup.names(CONSTRUCTING.get()))); }
}
