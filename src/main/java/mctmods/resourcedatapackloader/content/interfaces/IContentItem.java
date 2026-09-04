package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.util.RomanNumerals;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import java.util.List;
import javax.annotation.Nullable;

public interface IContentItem {

    static void potionTooltip(@Nullable ItemVariant value, List<String> tooltip) {
        if (value == null) { return; }
        PotionEffect effect = value.getResolvedPotion();
        if (effect == null) { return; }
        Potion potion = effect.getPotion();
        if (!potion.isBeneficial()) { return; }
        String name = new TextComponentTranslation(effect.getEffectName()).getFormattedText();
        String level = RomanNumerals.of(effect.getAmplifier());
        tooltip.add(TextFormatting.GREEN + (level.isEmpty() ? name : name + " " + level));
    }
}
