package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class ContentPotion extends MobEffect {
    private final PotionDef def;

    public ContentPotion(PotionDef def) {
        super(category(def), def.liquidColor());
        this.def = def;
        for (AttributeDef modifier : def.attributes()) {
            Attribute attribute = ContentAttributes.find(modifier.attribute(), def.key());
            if (attribute == null) { continue; }
            try { addAttributeModifier(attribute, modifier.uuid(), modifier.amount(), AttributeModifier.Operation.fromValue(modifier.operation())); }
            catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) { ContentLog.LOGGER.error("Attribute modifier for {} has an unusable uuid '{}' or operation {}, skipping it", def.key(), modifier.uuid(), modifier.operation()); }
        }
    }

    private static MobEffectCategory category(PotionDef def) {
        if (def.badEffect()) { return MobEffectCategory.HARMFUL; }
        return def.beneficial() ? MobEffectCategory.BENEFICIAL : MobEffectCategory.NEUTRAL;
    }

    @Override public boolean isInstantenous() { return def.instant(); }
}
