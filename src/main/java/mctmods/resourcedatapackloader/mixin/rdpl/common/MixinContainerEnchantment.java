package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.ContentEnchanting;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(ContainerEnchantment.class) public abstract class MixinContainerEnchantment {
    @Shadow public IInventory tableInventory;
    @Shadow public int xpSeed;
    @Shadow @Final private Random rand;
    @Shadow @Final private World world;
    @Unique private int rdpl$slot;
    @Unique private ItemStack rdpl$lapis = ItemStack.EMPTY;

    @Redirect(method = "onCraftMatrixChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnchantable()Z")) private boolean rdpl$enchantable(ItemStack stack) {
        boolean vanilla = stack.isItemEnchantable();
        return world.isRemote ? vanilla || AnvilCosts.opening(true) : ContentEnchanting.enchantable(stack, vanilla);
    }

    @Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true) private void rdpl$rolled(ItemStack stack, int enchantSlot, int level, CallbackInfoReturnable<List<EnchantmentData>> cir) {
        List<EnchantmentData> rolled = ContentEnchanting.rolled(rand, xpSeed, enchantSlot, stack, level);
        if (rolled != null) { cir.setReturnValue(rolled); }
    }

    @Inject(method = "enchantItem", at = @At("HEAD")) private void rdpl$noteSlot(EntityPlayer playerIn, int id, CallbackInfoReturnable<Boolean> cir) {
        rdpl$slot = id;
        rdpl$lapis = tableInventory.getStackInSlot(1).copy();
    }

    @Redirect(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0)) private int rdpl$lapisAgainstCost(ItemStack lapis) { return lapis.getCount() - AnvilCosts.lapis(world.isRemote, rdpl$slot) + rdpl$slot + 1; }

    @Inject(method = "enchantItem", at = @At("RETURN")) private void rdpl$lapisPaid(EntityPlayer playerIn, int id, CallbackInfoReturnable<Boolean> cir) {
        ItemStack before = rdpl$lapis;
        rdpl$lapis = ItemStack.EMPTY;
        int wanted = AnvilCosts.lapis(false, id);
        if (world.isRemote || before.isEmpty() || wanted == id + 1) { return; }
        ItemStack now = tableInventory.getStackInSlot(1);
        if ((now.isEmpty() ? 0 : now.getCount()) != Math.max(0, before.getCount() - id - 1)) { return; }
        ItemStack kept = before.copy();
        kept.setCount(before.getCount() - wanted);
        tableInventory.setInventorySlotContents(1, kept.getCount() > 0 ? kept : ItemStack.EMPTY);
    }

    @Redirect(method = "enchantItem", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;experienceLevel:I", opcode = Opcodes.GETFIELD, ordinal = 0)) private int rdpl$levelsAgainstCost(EntityPlayer player) { return player.experienceLevel - AnvilCosts.paid(player.world.isRemote, rdpl$slot) + rdpl$slot + 1; }

    @ModifyArg(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;onEnchant(Lnet/minecraft/item/ItemStack;I)V"), index = 1) private int rdpl$levelsPaid(int vanilla) { return AnvilCosts.paid(false, vanilla - 1); }
}
