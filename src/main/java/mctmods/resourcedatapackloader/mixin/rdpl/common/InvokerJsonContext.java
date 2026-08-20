package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.JsonContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = JsonContext.class, remap = false) public interface InvokerJsonContext { @Invoker("loadConstants") void rdpl$loadConstants(JsonObject... jsons); }
