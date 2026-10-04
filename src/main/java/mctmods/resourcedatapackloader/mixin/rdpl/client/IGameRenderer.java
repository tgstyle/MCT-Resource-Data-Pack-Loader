package mctmods.resourcedatapackloader.mixin.rdpl.client;

import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class) public interface IGameRenderer { @Accessor("resourcePool") CrossFrameResourcePool rdpl$getResourcePool(); }
