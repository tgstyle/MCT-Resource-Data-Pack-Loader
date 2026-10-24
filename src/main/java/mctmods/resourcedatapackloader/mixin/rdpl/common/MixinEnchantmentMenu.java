package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.ContentEnchanting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(EnchantmentMenu.class) public abstract class MixinEnchantmentMenu {
    @Shadow @Final private Container enchantSlots;
    @Shadow @Final private ContainerLevelAccess access;
    @Shadow @Final private RandomSource random;
    @Shadow @Final private DataSlot enchantmentSeed;
    @Unique private int rdpl$slot;
    @Unique private ItemStack rdpl$lapis = ItemStack.EMPTY;

    @Redirect(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z")) private boolean rdpl$enchantable(ItemStack stack) {
        boolean vanilla = stack.isEnchantable();
        return access == ContainerLevelAccess.NULL ? vanilla || AnvilCosts.opening(true) : ContentEnchanting.enchantable(stack, vanilla);
    }

    @Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true) private void rdpl$rolled(RegistryAccess access, ItemStack itemStack, int slot, int enchantmentCost, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> rolled = ContentEnchanting.rolled(access, random, enchantmentSeed.get(), slot, itemStack, enchantmentCost);
        if (rolled != null) { cir.setReturnValue(rolled); }
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD")) private void rdpl$noteSlot(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        rdpl$slot = buttonId;
        rdpl$lapis = enchantSlots.getItem(1).copy();
    }

    @Redirect(method = "clickMenuButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0)) private int rdpl$lapisAgainstCost(ItemStack lapis) { return lapis.getCount() - AnvilCosts.lapis(access == ContainerLevelAccess.NULL, rdpl$slot) + rdpl$slot + 1; }

    @Inject(method = "clickMenuButton", at = @At("RETURN")) private void rdpl$lapisPaid(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        ItemStack before = rdpl$lapis;
        rdpl$lapis = ItemStack.EMPTY;
        int wanted = AnvilCosts.lapis(false, buttonId);
        if (player.level().isClientSide() || before.isEmpty() || wanted == buttonId + 1) { return; }
        ItemStack now = enchantSlots.getItem(1);
        if ((now.isEmpty() ? 0 : now.getCount()) != Math.max(0, before.getCount() - buttonId - 1)) { return; }
        ItemStack kept = before.copy();
        kept.setCount(before.getCount() - wanted);
        enchantSlots.setItem(1, kept.getCount() > 0 ? kept : ItemStack.EMPTY);
    }

    @Redirect(method = "clickMenuButton", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;experienceLevel:I", opcode = Opcodes.GETFIELD, ordinal = 0)) private int rdpl$levelsAgainstCost(Player player) { return player.experienceLevel - AnvilCosts.paid(player.level().isClientSide(), rdpl$slot) + rdpl$slot + 1; }
}
