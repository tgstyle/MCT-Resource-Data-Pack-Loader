package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentPotionItem extends PotionItem {
    private final ItemDef def;
    private final ResourceLocation id;
    @Nullable private List<ItemStack> stacks;

    public ContentPotionItem(ItemDef def, ResourceLocation id, Properties properties) {
        super(properties);
        this.def = def;
        this.id = id;
    }

    @Override @Nonnull public Component getName(@Nonnull ItemStack stack) { return Component.translatable(Items.POTION.getDescriptionId(stack)); }

    public List<ItemStack> stacks() {
        if (stacks == null) { stacks = resolve(); }
        return stacks;
    }

    private List<ItemStack> resolve() {
        List<ItemStack> found = new ArrayList<>();
        if (def.potionTypes().isEmpty()) {
            for (Potion potion : ForgeRegistries.POTIONS) {
                ResourceLocation name = ForgeRegistries.POTIONS.getKey(potion);
                if (name != null && name.getNamespace().equals(id.getNamespace())) { found.add(PotionUtils.setPotion(new ItemStack(this), potion)); }
            }
            return found;
        }
        for (String name : def.potionTypes()) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Potion potion = Registered.find(ForgeRegistries.POTIONS, key);
            if (potion == null) {
                ContentLog.LOGGER.warn("Potion bottle {} names potion type '{}', which is not registered, so it is left out. If the pack does define it, check whether content.potions is off", id, name);
                continue;
            }
            found.add(PotionUtils.setPotion(new ItemStack(this), potion));
        }
        return found;
    }
}
