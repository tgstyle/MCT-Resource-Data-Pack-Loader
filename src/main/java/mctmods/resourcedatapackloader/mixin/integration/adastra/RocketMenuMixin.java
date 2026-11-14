package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.RocketSlot;

import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import earth.terrarium.adastra.common.menus.vehicles.RocketMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RocketMenu.class, remap = false) public abstract class RocketMenuMixin {
    @Redirect(method = "addMenuSlots()V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Lnet/minecraft/world/inventory/Slot;", ordinal = 0)) private Slot rdpl$cargoSlot(Container container, int slot, int x, int y) { return RocketSlot.made(((BaseEntityContainerMenu<?>) (Object) this).getEntity(), container, slot, x, y); }
    @Redirect(method = "addMenuSlots()V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Lnet/minecraft/world/inventory/Slot;", ordinal = 1)) private Slot rdpl$fuelSlot(Container container, int slot, int x, int y) { return RocketSlot.fuel(((BaseEntityContainerMenu<?>) (Object) this).getEntity(), container, slot, x, y); }
}
