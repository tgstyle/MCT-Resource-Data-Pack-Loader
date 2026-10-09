package mctmods.resourcedatapackloader.client;

import net.minecraft.world.phys.Vec3;

public final class SeamSkyColors {
    private SeamSkyColors() {}

    public static Vec3 rgb(int rgb) { return new Vec3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D); }

    public static int fogOf(String base) {
        return switch (base) {
            case "minecraft:the_nether" -> 0x330808;
            case "minecraft:the_end" -> 0xA080A0;
            default -> 0xC0D8FF;
        };
    }
}
