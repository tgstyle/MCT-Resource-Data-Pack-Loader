package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(CloudRenderer.class) public interface ICloudRenderer {
    @Accessor("texture") @Nullable CloudRenderer.TextureData rdpl$getTexture();

    @Accessor("texture") void rdpl$setTexture(@Nullable CloudRenderer.TextureData texture);
}
