package mctmods.resourcedatapackloader.mixin.integration.extraplanets;

import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcWorldProvider;

import com.google.common.collect.Lists;
import com.mjr.extraplanets.Config;
import com.mjr.extraplanets.handlers.MainHandlerServer;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainHandlerServer.class, remap = false) public abstract class MixinMainHandlerServer {
    @Shadow protected abstract void checkPressure(LivingUpdateEvent event, EntityPlayerMP playerMP, int amount, List<String> list);

    @Inject(method = "runChecks", at = @At(value = "FIELD", target = "Lcom/mjr/extraplanets/Config;OTHER_ADDON_PLANET_MOON_RAD_VALUES_LIST:Ljava/util/HashMap;", opcode = Opcodes.GETSTATIC, ordinal = 0)) private void rdpl$packPressure(LivingUpdateEvent event, EntityLivingBase entityLiving, CallbackInfo ci) {
        if (!Config.PRESSURE || !(entityLiving.world.provider instanceof GcWorldProvider)) { return; }
        int pressure = ((GcWorldProvider) entityLiving.world.provider).extraPlanetsPressure();
        if (pressure > 0) { checkPressure(event, (EntityPlayerMP) entityLiving, pressure, Lists.newArrayList(Config.SPACE_SUIT_SUPPORTED_ARMOUR)); }
    }
}
