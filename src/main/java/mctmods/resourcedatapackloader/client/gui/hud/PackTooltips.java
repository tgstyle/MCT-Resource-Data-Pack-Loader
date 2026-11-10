package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientDiet;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.items.ContentContainerBlockItem;
import mctmods.resourcedatapackloader.common.items.ContentPouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SeasonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowWater;
import mctmods.resourcedatapackloader.common.tooltips.ContentTooltips;
import mctmods.resourcedatapackloader.common.weights.CarriedWeight;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.core.config.Config;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public final class PackTooltips {
    private static final String ITEMS = "Items";
    private static final long WATER_TICKS = 20L;
    private static BlockPos waterPos;
    private static Block waterBlock;
    private static int waterKind;
    private static long waterTime;
    private static ContentGrowWater.Status waterStatus = ContentGrowWater.Status.NONE;

    private PackTooltips() {}

    private static Component text(String line) { return (I18n.exists(line) ? Component.translatable(line) : Component.literal(line)).withStyle(ChatFormatting.GRAY); }

    private static String translated(String line) { return I18n.exists(line) ? I18n.get(line) : line; }

    private static void weight(ItemStack stack, List<Component> tooltip) {
        WeightDef settings = ContentWeights.settings();
        if (Config.content.vanillaClients() || !settings.tooltip || !ContentWeights.any() || stack.isEmpty()) { return; }
        long single = ContentWeights.of(stack);
        long held = CarriedWeight.single(stack, settings) - single;
        if (single > 0L) {
            String each = ClientWeights.format(single);
            tooltip.add(Component.literal(stack.getCount() > 1 ? I18n.get("rdpl.weight.stack", each, ClientWeights.format((single + held) * stack.getCount())) : I18n.get("rdpl.weight.line", each)).withStyle(ChatFormatting.GRAY));
        }
        if (held > 0L) { tooltip.add(Component.literal(I18n.get("rdpl.weight.contents", ClientWeights.format(held))).withStyle(ChatFormatting.GRAY)); }
    }

    private static void kept(ItemStack stack, List<Component> tooltip) {
        CompoundTag held = stack.getTagElement(ContentPouchInventory.HELD);
        if (held != null && ContentContainerBlockItem.holdsKept(stack) && !held.getList(ITEMS, Tag.TAG_COMPOUND).isEmpty()) { tooltip.add(Component.translatable("rdpl.container.kept", held.getList(ITEMS, Tag.TAG_COMPOUND).size()).withStyle(ChatFormatting.GRAY)); }
    }

    private static void diet(ItemStack stack, List<Component> tooltip) {
        if (Config.content.vanillaClients() || !ContentDiet.tooltip() || ClientDiet.waiting() || stack.getFoodProperties(null) == null) { return; }
        String kind = ContentDiet.counted(stack);
        if (kind.isEmpty()) { tooltip.add(Component.translatable("rdpl.diet.tooltip.ignored").withStyle(ChatFormatting.GRAY)); }
        else if (ClientDiet.ate(kind)) { tooltip.add(Component.translatable("rdpl.diet.tooltip.eaten").withStyle(ChatFormatting.GREEN)); }
        else { tooltip.add(Component.translatable("rdpl.diet.tooltip.new").withStyle(ChatFormatting.YELLOW)); }
    }

    @Nullable private static Block plant(ItemStack stack) {
        Block block = BlockMatch.blockOf(stack.getItem());
        return block instanceof IPlantable || ContentGrowWater.sapling(block) ? block : null;
    }

    private static String scaled(float scale) { return scale == Math.rint(scale) ? String.valueOf((int) scale) : String.valueOf(scale); }

    private static ContentGrowWater.Status waterStatus(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        long now = level.getGameTime();
        if (!pos.equals(waterPos) || block != waterBlock || kind != waterKind || now < waterTime || now - waterTime >= WATER_TICKS) {
            waterStatus = ContentGrowWater.status(level, pos, block, kind, def);
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
        ContentGrowWater.Status status = waterStatus(level, pos, block, kind, here);
        if (status == ContentGrowWater.Status.FOUND) { tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility.water.found")).withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.WAIVED) { tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility.water.waived")).withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.MISSING && water.breaks()) { tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility.water.breaks", water.within())).withStyle(ChatFormatting.GRAY)); }
        else if (status == ContentGrowWater.Status.MISSING) { tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility.water.missing", water.within(), scaled(water.scale() * here.scale(kind, block)))).withStyle(ChatFormatting.GRAY)); }
    }

    private static void fertility(ItemStack stack, List<Component> tooltip) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (Config.content.vanillaClients() || player == null || stack.isEmpty()) { return; }
        Block block = plant(stack);
        if (block == null) { return; }
        BlockPos pos = player.blockPosition();
        GrowSwitchDef here = ContentEnvironments.growth(player.level(), pos);
        if (here.tooltip() == GrowSwitchDef.TOOLTIP_OFF) { return; }
        int kind = ContentGrowWater.kindOf(block);
        tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility", scaled(here.scale(kind, block)))).withStyle(ChatFormatting.GRAY));
        water(player.level(), pos, block, kind, here, tooltip);
        ResourceLocation dimension = player.level().dimension().location();
        SeasonDef seasons = here.tooltip() == GrowSwitchDef.TOOLTIP_YEAR ? ContentSeasons.def(dimension) : null;
        if (seasons == null) { return; }
        for (List<String> entry : seasons.order()) {
            String name = entry.get(0);
            tooltip.add(Component.literal(I18n.get("rdpl.tooltip.fertility.season", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1), scaled(ContentEnvironments.growthOf(dimension, name).scale(kind, block)))).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void onTooltip(ItemTooltipEvent event) {
        weight(event.getItemStack(), event.getToolTip());
        kept(event.getItemStack(), event.getToolTip());
        diet(event.getItemStack(), event.getToolTip());
        fertility(event.getItemStack(), event.getToolTip());
        lines(event);
        affixes(event.getItemStack(), event.getToolTip());
    }

    private static void lines(ItemTooltipEvent event) {
        if (ContentTooltips.none()) { return; }
        List<Component> tooltip = event.getToolTip();
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(event.getItemStack()).entrySet()) {
            List<String> lines = ContentTooltips.forEnchantment(entry.getKey());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(entry.getKey().getFullname(entry.getValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
        for (String line : ContentTooltips.forItem(event.getItemStack())) { tooltip.add(text(line)); }
    }

    private static void affixes(ItemStack stack, List<Component> tooltip) {
        CompoundTag mark = stack.getTagElement(ContentAffixes.TAG);
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
}
