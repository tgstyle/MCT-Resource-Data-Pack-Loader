package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentDrinkItem extends Item {
    private final ItemDef def;
    @Nullable private final MobEffectInstance effect;
    @Nullable private Item container;

    public ContentDrinkItem(ItemDef def, ItemVariant variant, Properties properties) {
        super(properties);
        this.def = def;
        this.effect = ContentEffects.parse(variant.id(), variant.potion());
    }

    public void resolveContainer(@Nullable Item item) { container = item; }

    @Override public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) { return def.useDuration(); }

    @Override @Nonnull public ItemUseAnimation getUseAnimation(@Nonnull ItemStack stack) { return def.eat() ? ItemUseAnimation.EAT : ItemUseAnimation.DRINK; }

    @Override @Nonnull public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override @Nonnull public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entity) {
        if (!level.isClientSide() && effect != null) { entity.addEffect(ContentEffects.copy(effect)); }
        Player player = entity instanceof Player held ? held : null;
        if (player != null && def.cooldown() > 0) { player.getCooldowns().addCooldown(stack, def.cooldown()); }
        if (player != null && player.getAbilities().instabuild) { return stack; }
        stack.shrink(1);
        if (container == null) { return stack; }
        ItemStack left = new ItemStack(container);
        if (stack.isEmpty()) { return left; }
        if (player != null && !player.getInventory().add(left)) { LineCompat.drop(player, left); }
        return stack;
    }

    @Override public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> tooltip, @Nonnull TooltipFlag flag) { ContentEffects.tooltip(effect, tooltip); }
}
