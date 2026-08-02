package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentToolTypes {
    private static final Map<String, Profile> TOOLS = new LinkedHashMap<>();
    private static final Map<String, EntityEquipmentSlot> SLOTS = new LinkedHashMap<>();

    private ContentToolTypes() {}

    static {
        TOOLS.put("pickaxe", new Profile("pickaxe", 1.0F, -2.8F, Sets.newHashSet(
                Blocks.COBBLESTONE, Blocks.STONE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MOSSY_COBBLESTONE,
                Blocks.NETHERRACK, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_ORE,
                Blocks.GOLD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
                Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.ICE, Blocks.PACKED_ICE, Blocks.RAIL)));
        TOOLS.put("axe", new Profile("axe", 6.0F, -3.2F, Sets.newHashSet(
                Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN,
                Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE)));
        TOOLS.put("shovel", new Profile("shovel", 1.5F, -3.0F, Sets.newHashSet(
                Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM,
                Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.CONCRETE_POWDER)));
        TOOLS.put("sword", new Profile("sword", 3.0F, -2.4F, Collections.emptySet()));

        SLOTS.put("helmet", EntityEquipmentSlot.HEAD);
        SLOTS.put("head", EntityEquipmentSlot.HEAD);
        SLOTS.put("chestplate", EntityEquipmentSlot.CHEST);
        SLOTS.put("chest", EntityEquipmentSlot.CHEST);
        SLOTS.put("leggings", EntityEquipmentSlot.LEGS);
        SLOTS.put("legs", EntityEquipmentSlot.LEGS);
        SLOTS.put("boots", EntityEquipmentSlot.FEET);
        SLOTS.put("feet", EntityEquipmentSlot.FEET);
    }

    @Nullable public static Profile tool(String name, Object context) {
        Profile profile = TOOLS.get(name == null ? "" : name.toLowerCase(Locale.ROOT));
        if (profile != null) { return profile; }

        ContentLog.LOGGER.error("Unknown toolClass '{}' in {}, the item is skipped. Known classes are {}", name, context, TOOLS.keySet());
        return null;
    }

    @Nullable public static EntityEquipmentSlot slot(String name, Object context) {
        EntityEquipmentSlot slot = SLOTS.get(name == null ? "" : name.toLowerCase(Locale.ROOT));
        if (slot != null) { return slot; }

        ContentLog.LOGGER.error("Unknown armor slot '{}' in {}, the item is skipped. Known slots are {}", name, context, SLOTS.keySet());
        return null;
    }

    public static final class Profile {
        public final String toolClass;
        public final float damage;
        public final float speed;
        public final Set<Block> effective;

        private Profile(String toolClass, float damage, float speed, Set<Block> effective) {
            this.toolClass = toolClass;
            this.damage = damage;
            this.speed = speed;
            this.effective = effective;
        }
    }
}
