package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldSettings;

import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DerivedWorldInfo.class) public class MixinDerivedWorldInfo extends MixinWorldInfo {
    @Shadow @Final private WorldInfo delegate;

    @Override public boolean rdpl$isRubic() { return ((IRubicWorldSettings) delegate).rdpl$isRubic(); }
}
