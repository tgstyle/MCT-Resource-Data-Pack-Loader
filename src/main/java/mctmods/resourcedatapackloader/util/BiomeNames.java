package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class BiomeNames {
    private BiomeNames() {}

    @Nullable public static TagKey<Biome> tag(String type) {
        String named = ContentFormats.biomeTag(type);
        return named == null ? null : tagged(named);
    }

    public static boolean matches(List<String> wanted, Holder<Biome> biome) {
        if (wanted.isEmpty()) { return true; }
        ResourceLocation name = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        for (String one : wanted) {
            if (one.startsWith("#")) {
                TagKey<Biome> tag = tag(one.substring(1));
                if (tag != null && biome.is(tag)) { return true; }
            }
            else if (name != null && (one.equals(name.toString()) || one.equals(name.getPath()))) { return true; }
        }
        return false;
    }

    public static String title(Holder<Biome> biome) {
        ResourceLocation name = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        if (name == null) { return ""; }
        StringBuilder title = new StringBuilder();
        for (String word : name.getPath().split("_")) {
            if (word.isEmpty()) { continue; }
            if (!title.isEmpty()) { title.append(' '); }
            title.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return title.toString();
    }

    @Nullable public static TagKey<Biome> tagged(String named) {
        ResourceLocation id = Registered.tagId(named);
        return id == null ? null : TagKey.create(Registries.BIOME, id);
    }

    private static String shownName(ResourceLocation biome) {
        BiomeDef def = ContentBiomes.def(biome);
        return def != null ? def.name() : Lang.vanilla("biome." + biome.getNamespace() + "." + biome.getPath());
    }

    public static boolean named(ResourceLocation biome, Collection<String> names) {
        if (names.isEmpty()) { return false; }
        Set<String> wanted = Settings.lower(names);
        return wanted.contains(biome.toString().toLowerCase(Locale.ROOT)) || wanted.contains(shownName(biome).toLowerCase(Locale.ROOT));
    }

    public static List<String> ids(Collection<String> names) {
        Set<String> out = new LinkedHashSet<>();
        Set<String> wanted = Settings.lower(names);
        for (String name : wanted) {
            if (name.indexOf(':') >= 0) { out.add(name); }
        }
        for (ResourceLocation biome : ContentBiomes.known()) {
            if (wanted.contains(shownName(biome).toLowerCase(Locale.ROOT))) { out.add(biome.toString()); }
        }
        return new ArrayList<>(out);
    }
}
