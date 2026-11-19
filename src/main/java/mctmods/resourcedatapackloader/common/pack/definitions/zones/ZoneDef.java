package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record ZoneDef(String id, String dimension, @Nullable int[] box, boolean claim, boolean exceptCreative, String exceptTag, @Nullable ZoneListDef<BlockState> breaks, @Nullable ZoneListDef<BlockState> places, @Nullable ZoneListDef<ItemStack> uses, @Nullable ZoneListDef<BlockState> opens, boolean noFly, String message, EnumSet<ZoneTool> denyTools, @Nullable ZoneDamageDef damage) {}
