package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientDiet;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.items.ContentContainerBlockItem;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowWater;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.soulbound.ContentSoulbound;
import mctmods.resourcedatapackloader.common.syncdata.DataCheck;
import mctmods.resourcedatapackloader.common.syncdata.DataReader;
import mctmods.resourcedatapackloader.common.tooltips.ContentTooltips;
import mctmods.resourcedatapackloader.common.weights.CarriedWeight;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.core.config.Config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class PackTooltips {
    private static final long WATER_TICKS = 20L;
    private static BlockPos waterPos;
    private static Block waterBlock;
    private static int waterKind;
    private static long waterTime;
    private static ContentGrowWater.Status waterStatus = ContentGrowWater.Status.NONE;

    private PackTooltips() {}

    private static Component text(String line) { return (Language.getInstance().has(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static void describe(ItemEnchantments enchantments, ItemStack stack, @Nullable HolderLookup.Provider access, List<Component> tooltip) {
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            List<ContentTooltips.Rule> rules = entry.getKey().unwrapKey().map(key -> ContentTooltips.forEnchantment(key.identifier())).orElse(List.of());
            int name = rules.isEmpty() ? -1 : tooltip.indexOf(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
            if (name < 0) { continue; }
            int cursor = name + 1;
            for (ContentTooltips.Rule rule : rules) {
                if (!shown(rule)) { continue; }
                int before = tooltip.size();
                int next = place(tooltip, rule, stack, access, name + 1, cursor);
                if (rule.position() == ContentTooltips.NO_POSITION) { cursor = next; }
                else if (next - rule.lines().size() == cursor) { cursor = next; }
                else if (next - rule.lines().size() < cursor) { cursor += tooltip.size() - before; }
            }
        }
    }

    private static boolean shown(ContentTooltips.Rule rule) { return (!rule.shift() || Minecraft.getInstance().hasShiftDown()) && (rule.data().isEmpty() || DataCheck.met(rule.data(), DataReader.client())); }

    private static Component line(ContentTooltips.Line line, ItemStack stack, @Nullable HolderLookup.Provider access) {
        String filled = line.render(stack, access, translated(line.raw()));
        return filled == null ? text(line.raw()) : Component.literal(filled).withStyle(ChatFormatting.GRAY);
    }

    private static int place(List<Component> tooltip, ContentTooltips.Rule rule, ItemStack stack, @Nullable HolderLookup.Provider access, int from, int fallback) {
        int at = rule.position() == ContentTooltips.NO_POSITION ? fallback : from + rule.position();
        boolean replace = rule.replace();
        if (at >= tooltip.size()) {
            at = tooltip.size();
            replace = false;
        }
        for (ContentTooltips.Line line : rule.lines()) {
            if (replace) {
                tooltip.set(at, line(line, stack, access));
                replace = false;
            } else {
                tooltip.add(at, line(line, stack, access));
            }
            at++;
        }
        return at;
    }

    private static String translated(String line) { return Language.getInstance().has(line) ? Language.getInstance().getOrDefault(line) : line; }

    private static void affixes(ItemStack stack, List<Component> tooltip) {
        CompoundTag mark = ContentAffixes.mark(stack);
        if (mark == null) { return; }
        ListTag affixes = mark.getListOrEmpty(ContentAffixes.AFFIXES);
        for (int at = 0; at < affixes.size(); at++) {
            String name = affixes.getCompoundOrEmpty(at).getStringOr(ContentAffixes.NAME, "");
            if (!name.isEmpty()) { tooltip.add(text(name)); }
        }
        ListTag sockets = mark.getListOrEmpty(ContentAffixes.SOCKETS);
        for (int at = 0; at < sockets.size(); at++) {
            CompoundTag socket = sockets.getCompoundOrEmpty(at);
            if (socket.getStringOr(ContentAffixes.GEM, "").isEmpty()) { tooltip.add(text("rdpl.socket.empty")); }
            else { tooltip.add(Component.translatable("rdpl.socket.filled", translated(socket.getStringOr(ContentAffixes.NAME, ""))).withStyle(ChatFormatting.GRAY)); }
        }
    }

    private static Component grey(String key, String... values) { return Component.translatable(key, (Object[]) values).withStyle(ChatFormatting.GRAY); }

    private static void weight(ItemStack stack, HolderLookup.Provider access, List<Component> tooltip) {
        WeightDef settings = ContentWeights.settings();
        if (Config.content.vanillaClients() || !settings.tooltip || !ContentWeights.any() || stack.isEmpty()) { return; }
        long single = ContentWeights.of(stack);
        long held = CarriedWeight.single(stack, settings, access) - single;
        if (single > 0L) {
            String each = ClientWeights.format(single);
            tooltip.add(stack.getCount() > 1 ? grey("rdpl.weight.stack", each, ClientWeights.format((single + held) * stack.getCount())) : grey("rdpl.weight.line", each));
        }
        if (held > 0L) { tooltip.add(grey("rdpl.weight.contents", ClientWeights.format(held))); }
    }

    private static void kept(ItemStack stack, List<Component> tooltip) {
        if (!ContentContainerBlockItem.holdsKept(stack)) { return; }
        long held = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItemCopyStream().count();
        if (held > 0L) { tooltip.add(grey("rdpl.container.kept", String.valueOf(held))); }
    }

    private static void diet(ItemStack stack, List<Component> tooltip) {
        if (Config.content.vanillaClients() || !ContentDiet.tooltip() || ClientDiet.waiting() || !stack.has(DataComponents.FOOD)) { return; }
        String kind = ContentDiet.counted(stack);
        if (kind.isEmpty()) { tooltip.add(Component.translatable("rdpl.diet.tooltip.ignored").withStyle(ChatFormatting.GRAY)); }
        else if (ClientDiet.ate(kind)) { tooltip.add(Component.translatable("rdpl.diet.tooltip.eaten").withStyle(ChatFormatting.GREEN)); }
        else { tooltip.add(Component.translatable("rdpl.diet.tooltip.new").withStyle(ChatFormatting.YELLOW)); }
    }

    @Nullable private static Block plant(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem item)) { return null; }
        Block block = item.getBlock();
        return block instanceof CropBlock || block instanceof StemBlock || block instanceof NetherWartBlock || ContentGrowWater.sapling(block) ? block : null;
    }

    private static String scaled(float scale) { return scale == Math.rint(scale) ? String.valueOf((int) scale) : String.valueOf(scale); }

    private static ContentGrowWater.Status waterStatus(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef here) {
        long now = level.getGameTime();
        if (!pos.equals(waterPos) || block != waterBlock || kind != waterKind || now < waterTime || now - waterTime >= WATER_TICKS) {
            waterStatus = ContentGrowWater.status(level, pos, block, kind, here);
            waterPos = pos;
            waterBlock = block;
            waterKind = kind;
            waterTime = now;
        }
        return waterStatus;
    }

    private static void water(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef here, List<Component> tooltip) {
        WaterDef water = here.water();
        if (water == null) { return; }
        String within = String.valueOf(water.within());
        switch (waterStatus(level, pos, block, kind, here)) {
            case FOUND -> tooltip.add(grey("rdpl.tooltip.fertility.water.found"));
            case MISSING -> tooltip.add(water.breaks() ? grey("rdpl.tooltip.fertility.water.breaks", within) : grey("rdpl.tooltip.fertility.water.missing", within, scaled(here.scale(kind, block) * water.scale())));
            case WAIVED -> tooltip.add(grey("rdpl.tooltip.fertility.water.waived"));
            case NONE -> {}
        }
    }

    private static void fertility(ItemStack stack, List<Component> tooltip) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (Config.content.vanillaClients() || player == null || stack.isEmpty()) { return; }
        Block block = plant(stack);
        if (block == null) { return; }
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        GrowSwitchDef here = ContentEnvironments.growth(level, pos);
        if (here.tooltip() == GrowSwitchDef.TOOLTIP_OFF) { return; }
        int kind = ContentGrowWater.kindOf(block);
        tooltip.add(grey("rdpl.tooltip.fertility", scaled(here.scale(kind, block))));
        water(level, pos, block, kind, here, tooltip);
        Identifier dimension = level.dimension().identifier();
        SeasonDef seasons = here.tooltip() == GrowSwitchDef.TOOLTIP_YEAR ? ContentSeasons.def(dimension) : null;
        if (seasons == null) { return; }
        for (List<String> entry : seasons.order()) {
            String name = entry.getFirst();
            tooltip.add(grey("rdpl.tooltip.fertility.season", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1), scaled(ContentEnvironments.growthOf(dimension, name).scale(kind, block))));
        }
    }

    private static void soulbound(ItemStack stack, HolderLookup.Provider access, List<Component> tooltip) {
        if (Config.content.vanillaClients() || stack.isEmpty()) { return; }
        for (String line : ContentSoulbound.tooltips(access, stack)) { tooltip.add(text(line)); }
    }

    public static void onTooltip(ItemTooltipEvent event) {
        HolderLookup.Provider access = event.getContext().registries();
        if (access != null) { weight(event.getItemStack(), access, event.getToolTip()); }
        affixes(event.getItemStack(), event.getToolTip());
        kept(event.getItemStack(), event.getToolTip());
        diet(event.getItemStack(), event.getToolTip());
        fertility(event.getItemStack(), event.getToolTip());
        if (access != null) { soulbound(event.getItemStack(), access, event.getToolTip()); }
        if (ContentTooltips.none()) { return; }
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        describe(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), stack, access, tooltip);
        describe(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), stack, access, tooltip);
        for (ContentTooltips.Rule rule : ContentTooltips.forItem(stack)) {
            if (shown(rule)) { place(tooltip, rule, stack, access, 1, tooltip.size()); }
        }
    }
}
