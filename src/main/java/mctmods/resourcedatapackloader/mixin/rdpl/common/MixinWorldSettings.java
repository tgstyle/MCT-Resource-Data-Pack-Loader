package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldSettings;

import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSettings.class) public class MixinWorldSettings implements IRubicWorldSettings {
    @Unique private boolean rdpl$isRubic;

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At("RETURN"))
    private void onConstruct(WorldInfo info, CallbackInfo cbi) { this.rdpl$isRubic = ((IRubicWorldSettings) info).rdpl$isRubic(); }

    @Inject(method = "<init>(JLnet/minecraft/world/GameType;ZZLnet/minecraft/world/WorldType;)V", at = @At("RETURN"))
    private void onConstruct(long seedIn, GameType gameType, boolean enableMapFeatures, boolean hardcoreMode, WorldType worldTypeIn, CallbackInfo ci) {
        this.rdpl$isRubic = mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.wanted();
    }

    @Override public boolean rdpl$isRubic() { return rdpl$isRubic; }

    @Override public void rdpl$setRubic(boolean rubic) { this.rdpl$isRubic = rubic; }
}
