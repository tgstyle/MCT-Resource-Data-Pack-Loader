package mctmods.resourcedatapackloader.client.gui.hud;

import mctmods.resourcedatapackloader.client.util.ClientDiet;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.gui.PouchInventory;
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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class PackTooltips {
    private static final int COMPOUND = 10;
    private static final String ITEMS = "Items";
    private static final long WATER_TICKS = 20L;
    @Nullable private static BlockPos waterPos;
    @Nullable private static Block waterBlock;
    private static int waterKind;
    private static long waterTime;
    private static ContentGrowWater.Status waterStatus = ContentGrowWater.Status.NONE;

    private PackTooltips() {}

    private static String translated(String line) { return I18n.hasKey(line) ? I18n.format(line) : line; }

    private static String text(String line) { return TextFormatting.GRAY + translated(line); }

    private static void weight(ItemStack stack, List<String> tooltip) {
        WeightDef settings = ContentWeights.settings();
        if (Config.content.vanillaClients || !settings.tooltip || !ContentWeights.any() || stack.isEmpty()) { return; }
        long single = ContentWeights.of(stack);
        long held = CarriedWeight.single(stack, settings) - single;
        if (single > 0L) {
            String each = ClientWeights.format(single);
            tooltip.add(TextFormatting.GRAY + (stack.getCount() > 1 ? I18n.format("rdpl.weight.stack", each, ClientWeights.format((single + held) * stack.getCount())) : I18n.format("rdpl.weight.line", each)));
        }
        if (held > 0L) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.weight.contents", ClientWeights.format(held))); }
    }

    private static void kept(ItemStack stack, List<String> tooltip) {
        NBTTagCompound held = stack.getSubCompound(PouchInventory.HELD);
        if (held != null && ContentContainerBlock.holdsKept(stack) && held.getTagList(ITEMS, COMPOUND).tagCount() > 0) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.container.kept", held.getTagList(ITEMS, COMPOUND).tagCount())); }
    }

    private static void diet(ItemStack stack, List<String> tooltip) {
        if (Config.content.vanillaClients || !ContentDiet.tooltip() || ClientDiet.waiting() || !(stack.getItem() instanceof ItemFood)) { return; }
        String kind = ContentDiet.counted(stack);
        if (kind.isEmpty()) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.diet.tooltip.ignored")); }
        else if (ClientDiet.ate(kind)) { tooltip.add(TextFormatting.GREEN + I18n.format("rdpl.diet.tooltip.eaten")); }
        else { tooltip.add(TextFormatting.YELLOW + I18n.format("rdpl.diet.tooltip.new")); }
    }

    @Nullable private static Block plant(ItemStack stack, World world, BlockPos pos) {
        Item item = stack.getItem();
        Block block = BlockMatch.blockOf(item, world, pos);
        return item instanceof IPlantable || ContentGrowWater.sapling(block) ? block : null;
    }

    private static String scaled(float scale) { return scale == Math.rint(scale) ? String.valueOf((int) scale) : String.valueOf(scale); }

    private static ContentGrowWater.Status waterStatus(World world, BlockPos pos, Block block, int kind, GrowSwitchDef here) {
        long now = world.getTotalWorldTime();
        if (!pos.equals(waterPos) || block != waterBlock || kind != waterKind || now < waterTime || now - waterTime >= WATER_TICKS) {
            waterStatus = ContentGrowWater.status(world, pos, block, kind, here);
            waterPos = pos;
            waterBlock = block;
            waterKind = kind;
            waterTime = now;
        }
        return waterStatus;
    }

    private static void water(World world, BlockPos pos, Block block, int kind, GrowSwitchDef here, List<String> tooltip) {
        WaterDef water = here.water();
        if (water == null) { return; }
        ContentGrowWater.Status status = waterStatus(world, pos, block, kind, here);
        if (status == ContentGrowWater.Status.FOUND) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility.water.found")); }
        else if (status == ContentGrowWater.Status.WAIVED) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility.water.waived")); }
        else if (status == ContentGrowWater.Status.MISSING && water.breaks) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility.water.breaks", water.within())); }
        else if (status == ContentGrowWater.Status.MISSING) { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility.water.missing", water.within(), scaled(here.scale(kind, block, world, pos) * water.scale))); }
    }

    private static void fertility(ItemStack stack, List<String> tooltip) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (Config.content.vanillaClients || player == null || stack.isEmpty()) { return; }
        BlockPos pos = new BlockPos(player);
        Block block = plant(stack, player.world, pos);
        if (block == null) { return; }
        GrowSwitchDef here = ContentEnvironments.growth(player.world, pos);
        if (here.tooltip() == GrowSwitchDef.TOOLTIP_OFF) { return; }
        int kind = ContentGrowWater.kindOf(block);
        tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility", scaled(here.scale(kind, block, player.world, pos))));
        water(player.world, pos, block, kind, here, tooltip);
        int dimension = player.world.provider.getDimension();
        SeasonDef seasons = here.tooltip() == GrowSwitchDef.TOOLTIP_YEAR ? ContentSeasons.def(dimension) : null;
        if (seasons == null) { return; }
        for (List<String> entry : seasons.order) {
            String name = entry.get(0);
            tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.tooltip.fertility.season", name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1), scaled(ContentEnvironments.growthOf(dimension, name).scale(kind, block, player.world, pos))));
        }
    }

    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();
        weight(stack, tooltip);
        kept(stack, tooltip);
        diet(stack, tooltip);
        fertility(stack, tooltip);
        if (ContentTooltips.none()) { return; }
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            List<String> lines = ContentTooltips.forEnchantment(entry.getKey());
            int at = lines.isEmpty() ? -1 : tooltip.indexOf(entry.getKey().getTranslatedName(entry.getValue()));
            if (at < 0) { continue; }
            for (String line : lines) { tooltip.add(++at, text(line)); }
        }
        for (String line : ContentTooltips.forItem(stack)) { tooltip.add(text(line)); }
        affixes(stack, tooltip);
    }

    private static void affixes(ItemStack stack, List<String> tooltip) {
        NBTTagCompound mark = stack.getSubCompound(ContentAffixes.TAG);
        if (mark == null) { return; }
        NBTTagList affixes = mark.getTagList(ContentAffixes.AFFIXES, COMPOUND);
        for (int at = 0; at < affixes.tagCount(); at++) {
            String name = affixes.getCompoundTagAt(at).getString(ContentAffixes.NAME);
            if (!name.isEmpty()) { tooltip.add(text(name)); }
        }
        NBTTagList sockets = mark.getTagList(ContentAffixes.SOCKETS, COMPOUND);
        for (int at = 0; at < sockets.tagCount(); at++) {
            NBTTagCompound socket = sockets.getCompoundTagAt(at);
            if (socket.getString(ContentAffixes.GEM).isEmpty()) { tooltip.add(text("rdpl.socket.empty")); }
            else { tooltip.add(TextFormatting.GRAY + I18n.format("rdpl.socket.filled", translated(socket.getString(ContentAffixes.NAME)))); }
        }
    }
}
