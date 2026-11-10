package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientDiet;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.items.ContentContainerBlockItem;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowWater;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.tooltips.ContentTooltips;
import mctmods.resourcedatapackloader.common.weights.CarriedWeight;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.core.config.Config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class PackTooltips {
    private PackTooltips() {}

    private static Component text(String line) { return (I18n.exists(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static void describe(ItemEnchantments enchantments, List<Component> tooltip) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            List<String> lines = entry.getKey().unwrapKey().map(key -> ContentTooltips.forEnchantment(key.location())).orElse(List.of());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
    }

    private static String translated(String line) { return I18n.exists(line) ? I18n.get(line) : line; }

    private static void weight(ItemStack stack, List<Component> tooltip) {
        WeightDef settings = ContentWeights.settings();
        if (Config.content.vanillaClients() || !settings.tooltip || !ContentWeights.any() || stack.isEmpty()) { return; }
        Player player = Minecraft.getInstance().player;
        if (player == null) { return; }
        long single = ContentWeights.of(stack);
        long held = CarriedWeight.single(player, stack, settings) - single;
        if (single > 0L) {
            String each = ClientWeights.format(single);
            tooltip.add(Component.literal(stack.getCount() > 1 ? I18n.get("rdpl.weight.stack", each, ClientWeights.format((single + held) * stack.getCount())) : I18n.get("rdpl.weight.line", each)).withStyle(ChatFormatting.GRAY));
        }
        if (held > 0L) { tooltip.add(Component.literal(I18n.get("rdpl.weight.contents", ClientWeights.format(held))).withStyle(ChatFormatting.GRAY)); }
    }

    private static void kept(ItemStack stack, List<Component> tooltip) {
        if (!ContentContainerBlockItem.holdsKept(stack)) { return; }
        long held = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().count();
        if (held > 0L) { tooltip.add(Component.translatable("rdpl.container.kept", held).withStyle(ChatFormatting.GRAY)); }
    }

    private static void diet(ItemStack stack, List<Component> tooltip) {
        if (Config.content.vanillaClients() || !ContentDiet.tooltip() || ClientDiet.waiting() || stack.getFoodProperties(null) == null) { return; }
        String kind = ContentDiet.counted(stack);
        if (kind.isEmpty()) { tooltip.add(Component.translatable("rdpl.diet.tooltip.ignored").withStyle(ChatFormatting.GRAY)); }
        else if (ClientDiet.ate(kind)) { tooltip.add(Component.translatable("rdpl.diet.tooltip.eaten").withStyle(ChatFormatting.GREEN)); }
        else { tooltip.add(Component.translatable("rdpl.diet.tooltip.new").withStyle(ChatFormatting.YELLOW)); }
    }

    private static final int WATER_CACHE_TICKS = 20;
    private static BlockPos waterPos;
    private static Block waterBlock;
    private static int waterKind;
    private static long waterTime;
    private static ContentGrowWater.Status waterFound = ContentGrowWater.Status.NONE;

    private static boolean plant(Block block) { return block instanceof CropBlock || block instanceof StemBlock || block instanceof NetherWartBlock || ContentGrowWater.sapling(block); }

    private static String scaled(float scale) { return scale == Math.rint(scale) ? String.valueOf((int) scale) : String.valueOf(scale); }

    private static ContentGrowWater.Status waterStatus(Player player, BlockPos pos, GrowSwitchDef here, Block block, int kind) {
        long time = player.level().getGameTime();
        if (!pos.equals(waterPos) || block != waterBlock || kind != waterKind || time < waterTime || time - waterTime >= WATER_CACHE_TICKS) {
            waterPos = pos;
            waterBlock = block;
            waterKind = kind;
            waterTime = time;
            waterFound = ContentGrowWater.status(player.level(), pos, block, kind, here);
        }
        return waterFound;
    }

    private static void water(Player player, BlockPos pos, GrowSwitchDef here, Block block, int kind, List<Component> tooltip) {
        WaterDef water = here.water();
        if (water == null) { return; }
        ContentGrowWater.Status status = waterStatus(player, pos, here, block, kind);
        if (status == ContentGrowWater.Status.FOUND) { tooltip.add(Component.translatable("rdpl.tooltip.fertility.water.found").withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.WAIVED) { tooltip.add(Component.translatable("rdpl.tooltip.fertility.water.waived").withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.MISSING && water.breaks) { tooltip.add(Component.translatable("rdpl.tooltip.fertility.water.breaks", water.within).withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.MISSING) { tooltip.add(Component.translatable("rdpl.tooltip.fertility.water.missing", water.within, scaled(here.scale(kind, block) * water.scale)).withStyle(ChatFormatting.GRAY)); }
    }

    private static void fertility(ItemStack stack, List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (Config.content.vanillaClients() || player == null || stack.isEmpty()) { return; }
        Block block = Block.byItem(stack.getItem());
        if (!plant(block)) { return; }
        BlockPos pos = player.blockPosition();
        GrowSwitchDef here = ContentEnvironments.growth(player.level(), pos);
        if (here.tooltip() == GrowSwitchDef.TOOLTIP_OFF) { return; }
        int kind = ContentGrowWater.kindOf(block);
        tooltip.add(Component.translatable("rdpl.tooltip.fertility", scaled(here.scale(kind, block))).withStyle(ChatFormatting.GRAY));
        water(player, pos, here, block, kind, tooltip);
        ResourceLocation dimension = player.level().dimension().location();
        SeasonState seasons = here.tooltip() == GrowSwitchDef.TOOLTIP_YEAR ? ContentSeasons.state(dimension) : null;
        if (seasons == null) { return; }
        for (String name : seasons.names()) {
            tooltip.add(Component.translatable("rdpl.tooltip.fertility.season", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1), scaled(ContentEnvironments.growthOf(dimension, name).scale(kind, block))).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void onTooltip(ItemTooltipEvent event) {
        weight(event.getItemStack(), event.getToolTip());
        kept(event.getItemStack(), event.getToolTip());
        diet(event.getItemStack(), event.getToolTip());
        fertility(event.getItemStack(), event.getToolTip());
        affixes(event.getItemStack(), event.getToolTip());
        lines(event);
    }

    private static void affixes(ItemStack stack, List<Component> tooltip) {
        CompoundTag mark = ContentAffixes.mark(stack);
        if (mark == null) { return; }
        ListTag affixes = mark.getList(ContentAffixes.AFFIXES, Tag.TAG_COMPOUND);
        for (int at = 0; at < affixes.size(); at++) {
            String name = affixes.getCompound(at).getString(ContentAffixes.NAME);
            if (!name.isEmpty()) { tooltip.add(text(name)); }
        }
        ListTag sockets = mark.getList(ContentAffixes.SOCKETS, Tag.TAG_COMPOUND);
        for (int at = 0; at < sockets.size(); at++) {
            CompoundTag socket = sockets.getCompound(at);
            if (socket.getString(ContentAffixes.GEM).isEmpty()) { tooltip.add(text("rdpl.socket.empty")); }
            else { tooltip.add(Component.translatable("rdpl.socket.filled", translated(socket.getString(ContentAffixes.NAME))).withStyle(ChatFormatting.GRAY)); }
        }
    }

    private static void lines(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        describe(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        describe(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), tooltip);
        for (String line : ContentTooltips.forItem(stack)) { tooltip.add(text(line)); }
    }
}
