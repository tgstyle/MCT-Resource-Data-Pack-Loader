package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class) public interface IEntityRenderer { @Accessor int getRendererUpdateCount(); }
