package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentServer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Consumer;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelRules {
    @Inject(method = "isAllowedToEnterPortal(Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$nether(Level toLevel, CallbackInfoReturnable<Boolean> cir) {
        if (toLevel.dimension() == Level.NETHER && Boolean.FALSE.equals(ContentServer.nether())) { cir.setReturnValue(false); }
    }

    @Inject(method = "isPvpAllowed()Z", at = @At("HEAD"), cancellable = true) private void rdpl$pvp(CallbackInfoReturnable<Boolean> cir) {
        Boolean asked = ContentServer.pvp();
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "isCommandBlockEnabled()Z", at = @At("HEAD"), cancellable = true) private void rdpl$commandBlocks(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.commandBlocks())) { cir.setReturnValue(false); }
    }

    @Inject(method = "isSpawningMonsters()Z", at = @At("HEAD"), cancellable = true) private void rdpl$spawnMonsters(CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.FALSE.equals(ContentServer.spawnMonsters())) { cir.setReturnValue(false); }
    }

    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void rdpl$spawnAnimalsAndNpcs(EntityTickList list, Consumer<Entity> output, Operation<Void> original) {
        boolean animals = !Boolean.FALSE.equals(ContentServer.spawnAnimals());
        boolean npcs = !Boolean.FALSE.equals(ContentServer.spawnNpcs());
        if (animals && npcs) {
            original.call(list, output);
            return;
        }
        original.call(list, (Consumer<Entity>) entity -> {
            if (!entity.isRemoved() && rdpl$discarded(entity, animals, npcs)) { entity.discard(); }
            else { output.accept(entity); }
        });
    }

    @Unique private static boolean rdpl$discarded(Entity entity, boolean animals, boolean npcs) {
        if (!animals && (entity instanceof Animal || entity instanceof WaterAnimal || entity instanceof AgeableWaterCreature)) { return true; }
        return !npcs && entity instanceof Npc;
    }
}
