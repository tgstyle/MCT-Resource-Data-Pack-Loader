package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(EntityRenderers.class) public interface IEntityRenderers {
    @Accessor("PROVIDERS") static Map<EntityType<?>, EntityRendererProvider<?>> rdpl$providers() { throw new AssertionError(); }
}
