package mctmods.resourcedatapackloader.common.items.derived;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

final class RegistryTypes {
    private static Map<Class<?>, Registry<?>> fixed;

    private RegistryTypes() {}

    @Nullable static Registry<?> find(Class<?> type, @Nullable Type generic) {
        Class<?> element = element(type, generic);
        if (element == null) { return null; }
        if (Enchantment.class.isAssignableFrom(element)) { return dynamic(); }
        for (Map.Entry<Class<?>, Registry<?>> entry : fixed().entrySet()) {
            if (entry.getKey().isAssignableFrom(element)) { return entry.getValue(); }
        }
        return null;
    }

    @Nullable static Object lookup(Class<?> type, @Nullable Type generic, ResourceLocation id) {
        Registry<?> registry = find(type, generic);
        if (registry == null) { return null; }
        Object found = type == Holder.class ? registry.getHolder(id).orElse(null) : registry.getOptional(id).orElse(null);
        if (found == null) { return null; }
        if (type == Supplier.class) { return (Supplier<Object>) () -> found; }
        return type.isInstance(found) ? found : null;
    }

    @Nullable private static Class<?> element(Class<?> type, @Nullable Type generic) {
        if (type != Holder.class && type != Supplier.class) { return type; }
        if (generic instanceof ParameterizedType parameterized && parameterized.getActualTypeArguments()[0] instanceof Class<?> element) { return element; }
        return null;
    }

    @Nullable private static Registry<?> dynamic() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? null : server.registryAccess().registry(Registries.ENCHANTMENT).orElse(null);
    }

    private static synchronized Map<Class<?>, Registry<?>> fixed() {
        if (fixed == null) {
            Map<Class<?>, Registry<?>> table = new LinkedHashMap<>();
            table.put(Item.class, BuiltInRegistries.ITEM);
            table.put(Block.class, BuiltInRegistries.BLOCK);
            table.put(Fluid.class, BuiltInRegistries.FLUID);
            table.put(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
            table.put(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
            table.put(Potion.class, BuiltInRegistries.POTION);
            table.put(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
            table.put(Attribute.class, BuiltInRegistries.ATTRIBUTE);
            table.put(ArmorMaterial.class, BuiltInRegistries.ARMOR_MATERIAL);
            table.put(Instrument.class, BuiltInRegistries.INSTRUMENT);
            table.put(ParticleType.class, BuiltInRegistries.PARTICLE_TYPE);
            fixed = table;
        }
        return fixed;
    }
}
