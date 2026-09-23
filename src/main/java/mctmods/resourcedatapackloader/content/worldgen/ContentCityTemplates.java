package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentCityTemplates {
    private static final String HOUSES_ROOT = "structures/";
    private static final String ENTRANCE = "minecraft:building_entrance";

    private ContentCityTemplates() {}

    static Map<ResourceLocation, ContentVillages.House> vanillaHouses(MinecraftServer server, @Nullable StructureTemplateManager templates) {
        Map<ResourceLocation, ContentVillages.House> found = new LinkedHashMap<>();
        if (templates == null) { return found; }
        for (String type : CityGround.VILLAGE_TYPES) {
            String folder = HOUSES_ROOT + "village/" + type + "/houses";
            for (ResourceLocation file : server.getResourceManager().listResources(folder, path -> path.getPath().endsWith(".nbt")).keySet()) {
                String path = file.getPath().substring(HOUSES_ROOT.length(), file.getPath().length() - ".nbt".length());
                ResourceLocation template = ResourceLocation.fromNamespaceAndPath(file.getNamespace(), path);
                templates.get(template).ifPresent(held -> {
                    Rotation turn = entranceTurn(held);
                    found.put(template, new ContentVillages.House(held.getSize(turn), turn));
                });
            }
        }
        if (!found.isEmpty()) { Summary.info("city.vanilla", "City plots mix the game's own " + found.size() + " village house(s), each district taking the houses of its biome's village type, with any plots the packs define"); }
        return found;
    }

    private static Rotation entranceTurn(StructureTemplate held) {
        for (StructureTemplate.StructureBlockInfo info : held.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.JIGSAW)) {
            if (info.nbt() == null || !ENTRANCE.equals(info.nbt().getString("name"))) { continue; }
            Direction front = JigsawBlock.getFrontFacing(info.state());
            for (Rotation turn : Rotation.values()) {
                if (turn.rotate(front) == Direction.NORTH) { return turn; }
            }
        }
        return Rotation.NONE;
    }

    @Nullable static Vec3i stationBuild(@Nullable StructureTemplateManager templates) {
        String named = ContentCity.stationStructure();
        if (named.isEmpty()) {
            if (ContentCity.subways()) { Summary.info("city.stations", "villageSubwayStation names no build, so subway lines run without stations"); }
            return null;
        }
        ResourceLocation key = ResourceLocation.tryParse(named);
        StructureTemplate held = key == null || templates == null ? null : templates.get(key).orElse(null);
        if (held == null) {
            ContentCity.missingStation(named);
            return null;
        }
        return held.getSize(Rotation.NONE);
    }
}
