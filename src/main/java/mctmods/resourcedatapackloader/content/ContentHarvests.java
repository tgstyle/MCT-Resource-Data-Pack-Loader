package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.world.CropHarvest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashMap;
import java.util.Map;

public final class ContentHarvests {
    private static final String CROPS = "crops";
    private static final String AGE = "age";
    private static final String REPLANT = "replant";
    private static final Map<Block, Rule> BY_BLOCK = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentHarvests() {}

    private static final class Rule {
        final int age;
        final boolean replant;

        Rule(int age, boolean replant) {
            this.age = age;
            this.replant = replant;
        }
    }

    private static void reload() {
        BY_BLOCK.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.HARVESTS, "crop harvests", ContentHarvests::read);
        if (!BY_BLOCK.isEmpty()) { Summary.info("content.harvests", "Loaded right-click harvest for " + BY_BLOCK.size() + " crop block(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has(CROPS)) {
            ContentLog.LOGGER.error("Crop harvest {} names no crops, ignoring it", key);
            return;
        }
        Rule rule = new Rule(Json.getInt(json, AGE, -1), Json.getBoolean(json, REPLANT, true));
        for (JsonElement element : Json.getJsonArray(json, CROPS)) {
            ResourceLocation name = new ResourceLocation(Json.getString(element, CROPS));
            if (!ForgeRegistries.BLOCKS.containsKey(name)) {
                ContentLog.LOGGER.error("Crop harvest {} names the block {}, which nothing registers, skipping it", key, name);
                continue;
            }
            BY_BLOCK.put(ForgeRegistries.BLOCKS.getValue(name), rule);
        }
    }

    @SuppressWarnings("ConstantConditions") @SubscribeEvent public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (event.getHand() != EnumHand.MAIN_HAND || player.isSneaking() || player.isSpectator()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (BY_BLOCK.isEmpty()) { return; }
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Rule rule = BY_BLOCK.get(state.getBlock());
        if (rule == null || !CropHarvest.ripe(state, rule.age) || !world.isBlockModifiable(player, pos)) { return; }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (world.isRemote) { return; }
        CropHarvest.harvest(world, pos, state, player, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand()), rule.replant);    }
}
