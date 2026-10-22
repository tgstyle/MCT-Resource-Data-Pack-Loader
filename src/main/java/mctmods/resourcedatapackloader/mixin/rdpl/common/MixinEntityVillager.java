package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.content.util.ContentDisabled;
import mctmods.resourcedatapackloader.content.worldgen.ContentTradeSwitch;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;
import java.util.Map;

@Mixin(EntityVillager.class) public abstract class MixinEntityVillager implements ITradeSwitchHolder {
    @Unique private final Map<String, ContentTradeSwitch.Held> rdpl$held = new HashMap<>();

    @Override public Map<String, ContentTradeSwitch.Held> rdpl$held() { return rdpl$held; }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL")) private void rdpl$writeTradeSwitch(NBTTagCompound compound, CallbackInfo ci) { ContentTradeSwitch.write((EntityVillager) (Object) this, compound); }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL")) private void rdpl$readTradeSwitch(NBTTagCompound compound, CallbackInfo ci) { ContentTradeSwitch.read((EntityVillager) (Object) this, compound); }

    @Inject(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/EntityVillager;populateBuyingList()V")) private void rdpl$restockTradeSwitch(CallbackInfo ci) { ContentTradeSwitch.restock((EntityVillager) (Object) this); }

    @Inject(method = "getRecipes", at = @At("RETURN")) private void rdpl$stripDisabled(EntityPlayer player, CallbackInfoReturnable<MerchantRecipeList> cir) {
        MerchantRecipeList trades = cir.getReturnValue();
        if (trades != null && ContentDisabled.any()) { trades.removeIf(MixinEntityVillager::rdpl$disabled); }
    }

    @Unique private static boolean rdpl$disabled(MerchantRecipe trade) { return ContentDisabled.disabled(trade.getItemToBuy()) || ContentDisabled.disabled(trade.getSecondItemToBuy()) || ContentDisabled.disabled(trade.getItemToSell()); }
}
