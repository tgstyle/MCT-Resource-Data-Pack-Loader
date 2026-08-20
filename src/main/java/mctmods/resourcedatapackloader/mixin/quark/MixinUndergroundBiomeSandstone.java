package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.world.world.underground.UndergroundBiomeSandstone;

@Mixin(value = UndergroundBiomeSandstone.class, remap = false) public abstract class MixinUndergroundBiomeSandstone {
    @Unique private static final int PLACE_OFFSET = 6;

    @Inject(method = "spawnDungeon", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$requireLoaded(WorldServer world, BlockPos pos, EnumFacing side, CallbackInfo ci) {
        Template template = world.getStructureTemplateManager().getTemplate(world.getMinecraftServer(), UndergroundBiomeSandstone.HUSK_GRAVE_STRUCTURE);
        BlockPos size = template.getSize();
        int reach = PLACE_OFFSET + Math.max(size.getX(), size.getZ());
        if (!ContentCascade.loaded(world, pos, reach)) { ci.cancel(); }
    }
}
