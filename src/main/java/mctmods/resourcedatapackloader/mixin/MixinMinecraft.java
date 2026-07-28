package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.RDPLResourcePack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow @Final
    private List<IResourcePack> defaultResourcePacks;
    @Unique
    private RDPLResourcePack rdpl$normal;
    @Unique
    private RDPLResourcePack rdpl$override;

    @ModifyArg(
            method = "refreshResources",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;reloadResources(Ljava/util/List;)V", ordinal = 0),
            index = 0
    )
    private List<IResourcePack> rdpl$insertPack(List<IResourcePack> list) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty() || manager.getRoot() == null) { return list; }
        if (rdpl$normal == null) { rdpl$normal = new RDPLResourcePack(manager.getRoot().toFile(), false); }
        if (rdpl$override == null) { rdpl$override = new RDPLResourcePack(manager.getRoot().toFile(), true); }
        list.remove(rdpl$normal);
        list.remove(rdpl$override);
        if (manager.hasTier(false)) { list.add(Math.min(defaultResourcePacks.size(), list.size()), rdpl$normal); }
        if (manager.hasTier(true)) { list.add(rdpl$override); }
        return list;
    }
}
