package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.def.OverrideDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class) public abstract class MixinItem {
    @Inject(method = "getMaxItemUseDuration", at = @At("HEAD"), cancellable = true) private void rdpl$edibleDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (ContentOverrides.edible((Item) (Object) this) != null) { cir.setReturnValue(32); }
    }

    @Inject(method = "getItemUseAction", at = @At("HEAD"), cancellable = true) private void rdpl$edibleAction(ItemStack stack, CallbackInfoReturnable<EnumAction> cir) {
        if (ContentOverrides.edible((Item) (Object) this) != null) { cir.setReturnValue(EnumAction.EAT); }
    }

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true) private void rdpl$edibleRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        OverrideDef.FoodDef food = ContentOverrides.edible((Item) (Object) this);
        if (food == null) { return; }
        ItemStack held = playerIn.getHeldItem(handIn);
        if (playerIn.canEat(food.alwaysEdible)) {
            playerIn.setActiveHand(handIn);
            cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, held));
        }
        else { cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, held)); }
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true) private void rdpl$edibleFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        OverrideDef.FoodDef food = ContentOverrides.edible((Item) (Object) this);
        if (food == null || !(entityLiving instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) entityLiving;
        player.getFoodStats().addStats(food.heal, food.saturation);
        worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
        if (!worldIn.isRemote) {
            for (PotionEffectDef effect : food.effects) {
                Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.potion));
                if (potion == null) { continue; }
                player.addPotionEffect(new PotionEffect(potion, effect.duration, effect.amplifier, effect.ambient, effect.showParticles));
            }
        }
        stack.shrink(1);
        cir.setReturnValue(stack);
    }
}
