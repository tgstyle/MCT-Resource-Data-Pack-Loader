package mctmods.resourcedatapackloader.common.pack.definitions.portals;

import net.minecraft.resources.Identifier;

public record PortalDef(Identifier dimension, Identifier returnDimension, String gate, int cooldown, boolean platform, String platformBlock, String sound, boolean owned, boolean walkIn) {}
