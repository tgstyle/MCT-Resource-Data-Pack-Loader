package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(TileEntityLockableLoot.class) public interface ITileEntityLockableLoot { @Nullable @Accessor("lootTable") ResourceLocation rdpl$lootTable(); }
