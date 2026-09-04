package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.def.AttributeDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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
            Holder<Attribute> attribute = ContentAttributes.find(modifier.attribute(), def.key());
            if (attribute == null) { continue; }
            ResourceLocation id = modifierId(def, modifier);
            if (id == null) { continue; }
            addAttributeModifier(attribute, id, modifier.amount(), AttributeModifier.Operation.BY_ID.apply(modifier.operation()));
        }
    }

    private static ResourceLocation modifierId(PotionDef def, AttributeDef modifier) {
        String path = def.key().getPath() + "/" + modifier.attribute().toLowerCase(java.util.Locale.ROOT).replace(':', '.');
        ResourceLocation id = ResourceLocation.tryBuild(def.key().getNamespace(), path);
        if (id == null) { ContentLog.LOGGER.error("Attribute modifier for {} on '{}' cannot be named, skipping it", def.key(), modifier.attribute()); }
        return id;
    }

    private static MobEffectCategory category(PotionDef def) {
        if (def.badEffect()) { return MobEffectCategory.HARMFUL; }
        return def.beneficial() ? MobEffectCategory.BENEFICIAL : MobEffectCategory.NEUTRAL;
    }

    @Override public boolean isInstantenous() { return def.instant(); }
}
