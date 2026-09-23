package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.LightShardSource;
import mctmods.resourcedatapackloader.util.LightShards;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataLayerStorageMap.class) public abstract class MixinDataLayerStorageMap implements LightShardSource {
    @Shadow @Final protected Long2ObjectOpenHashMap<DataLayer> map;
    @Unique private final LongSet rdpl$touched = new LongOpenHashSet();
    @Unique private LightShards rdpl$issued = new LightShards();

    @Override public LightShards rdpl$issue() {
        rdpl$issued = rdpl$issued.with(map, rdpl$touched);
        rdpl$touched.clear();
        return rdpl$issued;
    }

    @Inject(method = "copyDataLayer(J)Lnet/minecraft/world/level/chunk/DataLayer;", at = @At("HEAD"))
    private void rdpl$touchedByCopy(long index, CallbackInfoReturnable<DataLayer> cir) { rdpl$touched.add(index); }

    @Inject(method = "removeLayer(J)Lnet/minecraft/world/level/chunk/DataLayer;", at = @At("HEAD"))
    private void rdpl$touchedByRemove(long sectionPos, CallbackInfoReturnable<DataLayer> cir) { rdpl$touched.add(sectionPos); }

    @Inject(method = "setLayer(JLnet/minecraft/world/level/chunk/DataLayer;)V", at = @At("HEAD"))
    private void rdpl$touchedBySet(long sectionPos, DataLayer array, CallbackInfo ci) { rdpl$touched.add(sectionPos); }
}
