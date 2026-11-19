package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.MemberNames;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

final class MaterialCopies {
    private MaterialCopies() {}

    @Nullable static Object copy(Object value, ResourceLocation owner, String path) {
        String name = (owner.getNamespace() + "_" + owner.getPath() + "_" + path).replaceAll("[^A-Za-z0-9_]", "_");
        Object made = null;
        if (value instanceof Item.ToolMaterial) { made = tool((Item.ToolMaterial) value, name); }
        else if (value instanceof ItemArmor.ArmorMaterial) { made = armor((ItemArmor.ArmorMaterial) value, name); }
        if (made == null) { ContentLog.LOGGER.error("Could not make a material named {} for derived item {}", name, owner); }
        return made;
    }

    static boolean isMaterial(Object value) { return value instanceof Item.ToolMaterial || value instanceof ItemArmor.ArmorMaterial; }

    @Nullable private static Object tool(Item.ToolMaterial base, String name) {
        Item.ToolMaterial made = EnumHelper.addToolMaterial(name, base.getHarvestLevel(), base.getMaxUses(), base.getEfficiency(), base.getAttackDamage(), base.getEnchantability());
        if (made != null) { made.setRepairItem(base.getRepairItemStack()); }
        return made;
    }

    @Nullable private static Object armor(ItemArmor.ArmorMaterial base, String name) {
        int[] reduction = ((int[]) read(base, "damageReductionAmountArray")).clone();
        ItemArmor.ArmorMaterial made = EnumHelper.addArmorMaterial(name, (String) read(base, "name"), (Integer) read(base, "maxDamageFactor"), reduction, (Integer) read(base, "enchantability"), (SoundEvent) read(base, "soundEvent"), (Float) read(base, "toughness"));
        if (made != null) { made.setRepairItem(base.getRepairItemStack()); }
        return made;
    }

    private static Object read(Object from, String name) {
        Field field = MemberNames.field(from.getClass(), name);
        if (field == null) { throw new IllegalStateException(from.getClass().getName() + " has no field " + name); }
        try {
            field.setAccessible(true);
            return field.get(from);
        }
        catch (ReflectiveOperationException e) { throw new IllegalStateException("Could not read " + name + " of " + from.getClass().getName() + ": " + e, e); }
    }
}
