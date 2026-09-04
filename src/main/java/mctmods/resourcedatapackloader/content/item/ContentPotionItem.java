package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ContentPotionItem extends PotionItem {
    private final ItemDef def;
    private final ResourceLocation id;
    @Nullable private List<ItemStack> stacks;

    public ContentPotionItem(ItemDef def, ResourceLocation id, Properties properties) {
        super(properties);
        this.def = def;
        this.id = id;
    }

    public List<ItemStack> stacks() {
        if (stacks == null) { stacks = resolve(); }
        return stacks;
    }

    private List<ItemStack> resolve() {
        List<ItemStack> found = new ArrayList<>();
        if (def.potionTypes().isEmpty()) {
            for (Holder<Potion> potion : BuiltInRegistries.POTION.holders().toList()) {
                if (potion.unwrapKey().map(key -> key.location().getNamespace().equals(id.getNamespace())).orElse(false)) { found.add(filled(potion)); }
            }
            return found;
        }
        for (String name : def.potionTypes()) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Holder<Potion> potion = Registered.holder(BuiltInRegistries.POTION, key);
            if (potion == null) {
                ContentLog.LOGGER.warn("Potion bottle {} names potion type '{}', which is not registered, so it is left out. If the pack does define it, check whether content.potions is off", id, name);
                continue;
            }
            found.add(filled(potion));
        }
        return found;
    }

    private ItemStack filled(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }
}
