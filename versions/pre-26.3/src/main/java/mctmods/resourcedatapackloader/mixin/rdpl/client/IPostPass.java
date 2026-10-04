package mctmods.resourcedatapackloader.mixin.rdpl.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(PostPass.class) public interface IPostPass { @Accessor("customUniforms") Map<String, GpuBuffer> rdpl$getCustomUniforms(); }
