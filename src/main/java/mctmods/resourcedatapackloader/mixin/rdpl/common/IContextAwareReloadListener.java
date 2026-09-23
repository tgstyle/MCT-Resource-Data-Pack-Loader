package mctmods.resourcedatapackloader.mixin.rdpl.common;

import com.google.gson.JsonElement;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ContextAwareReloadListener.class) public interface IContextAwareReloadListener {
    @Invoker("makeConditionalOps") ConditionalOps<JsonElement> rdpl$makeConditionalOps();
}
