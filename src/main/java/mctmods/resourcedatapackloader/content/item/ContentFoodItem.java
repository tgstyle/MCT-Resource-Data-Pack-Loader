package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentFoodItem extends Item {
    private final ItemDef def;
    @Nullable private final MobEffectInstance effect;

    public ContentFoodItem(ItemDef def, @Nullable MobEffectInstance effect, Properties properties) {
        super(properties);
        this.def = def;
        this.effect = effect;
    }

    @Override @Nonnull public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entity) {
        ItemStack left = super.finishUsingItem(stack, level, entity);
        if (def.cooldown() > 0 && entity instanceof Player player) { player.getCooldowns().addCooldown(this, def.cooldown()); }
        return left;
    }

    @Override public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) { ContentEffects.tooltip(effect, tooltip); }
}
