package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentBlock;
import mctmods.resourcedatapackloader.content.block.ContentBlockDoor;
import mctmods.resourcedatapackloader.content.block.ContentBlockFalling;
import mctmods.resourcedatapackloader.content.block.ContentBlockFence;
import mctmods.resourcedatapackloader.content.block.ContentBlockFenceGate;
import mctmods.resourcedatapackloader.content.block.ContentBlockFlower;
import mctmods.resourcedatapackloader.content.block.ContentBlockFluid;
import mctmods.resourcedatapackloader.content.block.ContentBlockLeaves;
import mctmods.resourcedatapackloader.content.block.ContentBlockLog;
import mctmods.resourcedatapackloader.content.block.ContentBlockPane;
import mctmods.resourcedatapackloader.content.block.ContentBlockSlab;
import mctmods.resourcedatapackloader.content.block.ContentBlockWall;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.item.ContentItemPotion;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;
import mctmods.resourcedatapackloader.content.util.ContentBiomeTints;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class ContentModels {
    private static final String INVENTORY = "inventory";
    private static final String FLUID = "fluid";

    private ContentModels() {}

    @SubscribeEvent public static void registerModels(ModelRegistryEvent event) {
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            Block block = entry.getValue();
            BlockDef def = ContentRegistry.blockDef(entry.getKey());
            if (def == null) { continue; }
            Item item = Item.getItemFromBlock(block);
            PropertyVariant property = property(block);
            if (block instanceof ContentBlockDoor) {
                ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
                Item doorItem = ((ContentBlockDoor) block).getDoorItem();
                if (doorItem != null) { ModelLoader.setCustomModelResourceLocation(doorItem, 0, new ModelResourceLocation(entry.getKey(), INVENTORY)); }
                continue;
            }
            if (block instanceof ContentBlockFenceGate) { ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build()); }
            if (property == null) {
                if (item != Items.AIR) { ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(entry.getKey(), INVENTORY)); }
                continue;
            }
            ContentStateMapper mapper = new ContentStateMapper(def, property, entry.getKey(), hidden(block));
            ModelLoader.setCustomStateMapper(block, mapper);
            if (item == Items.AIR) { continue; }
            for (BlockVariant variant : def.visible) {
                ModelResourceLocation model = def.itemModelFromFile
                        ? new ModelResourceLocation(entry.getKey() + "/" + variant.name, INVENTORY)
                        : new ModelResourceLocation(entry.getKey(), mapper.variantFor(ContentStates.of(block, variant.meta)));
                ModelLoader.setCustomModelResourceLocation(item, variant.meta, model);
            }
        }
        for (Map.Entry<ResourceLocation, ContentBlockFluid> entry : ContentRegistry.registeredFluidBlocks()) {
            ModelResourceLocation location = fluidLocation(entry.getKey());
            ModelLoader.setCustomStateMapper(entry.getValue(), new StateMapperBase() {
                @Override @Nonnull protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) { return location; }
            });
        }
        for (Map.Entry<ResourceLocation, Item> entry : ContentRegistry.registeredItems()) {
            ItemDef def = ContentRegistry.itemDef(entry.getKey());
            if (def == null) { continue; }
            Item item = entry.getValue();
            if (!item.getHasSubtypes()) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(entry.getKey(), INVENTORY));
                continue;
            }
            for (ItemVariant variant : def.visible) {
                ModelLoader.setCustomModelResourceLocation(item, variant.meta, new ModelResourceLocation(def.registryName + "/" + variant.name, INVENTORY));
            }
        }
    }

    @SubscribeEvent public static void registerItemColors(ColorHandlerEvent.Item event) {
        for (Map.Entry<ResourceLocation, Item> entry : ContentRegistry.registeredItems()) {
            if (!(entry.getValue() instanceof ContentItemPotion)) { continue; }
            event.getItemColors().registerItemColorHandler((stack, layer) -> layer > 0 ? -1 : PotionUtils.getColor(stack), entry.getValue());
        }
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            BlockDef def = ContentRegistry.blockDef(entry.getKey());
            if (def == null || def.tint.isEmpty()) { continue; }
            Item item = Item.getItemFromBlock(entry.getValue());
            if (item == Items.AIR) { continue; }
            int fixed = ContentBiomeTints.fixed(def.tint, entry.getKey());
            String mode = ContentBiomeTints.mode(def.tint);
            event.getItemColors().registerItemColorHandler((stack, layer) -> fixed == ContentTypes.NO_COLOR ? ContentBiomeTints.biome(mode, null, null) : fixed, item);
        }
    }

    @SubscribeEvent public static void registerFluidSprites(TextureStitchEvent.Pre event) {
        for (Map.Entry<ResourceLocation, ContentBlockFluid> entry : ContentRegistry.registeredFluidBlocks()) {
            FluidDef def = ContentRegistry.fluidDef(entry.getKey());
            if (def == null) { continue; }
            event.getMap().registerSprite(def.still);
            event.getMap().registerSprite(def.flowing);
        }
    }

    @SubscribeEvent public static void bakeFluidModels(ModelBakeEvent event) {
        for (Map.Entry<ResourceLocation, ContentBlockFluid> entry : ContentRegistry.registeredFluidBlocks()) {
            FluidDef def = ContentRegistry.fluidDef(entry.getKey());
            if (def == null || def.getResolved() == null) { continue; }
            IBakedModel baked = new ModelFluid(def.getResolved()).bake(TRSRTransformation.identity(), Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
            event.getModelRegistry().putObject(fluidLocation(entry.getKey()), baked);
        }
    }

    private static ModelResourceLocation fluidLocation(ResourceLocation key) { return new ModelResourceLocation(key, FLUID); }

    private static Set<String> hidden(Block block) {
        if (block instanceof ContentBlockWall) { return ContentBlockWall.HIDDEN; }
        if (block instanceof ContentBlockLeaves) { return ContentBlockLeaves.HIDDEN; }
        return Collections.emptySet();
    }

    @SubscribeEvent public static void registerBlockColors(ColorHandlerEvent.Block event) {
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            BlockDef def = ContentRegistry.blockDef(entry.getKey());
            if (def == null || def.tint.isEmpty()) { continue; }
            int fixed = ContentBiomeTints.fixed(def.tint, entry.getKey());
            String mode = ContentBiomeTints.mode(def.tint);
            event.getBlockColors().registerBlockColorHandler(
                    (state, world, pos, layer) -> fixed == ContentTypes.NO_COLOR ? ContentBiomeTints.biome(mode, world, pos) : fixed,
                    entry.getValue());
        }
    }

    @Nullable private static PropertyVariant property(Block block) {
        if (block instanceof ContentBlockLog) { return ((ContentBlockLog) block).getVariantProperty(); }
        if (block instanceof ContentBlockLeaves) { return ((ContentBlockLeaves) block).getVariantProperty(); }
        if (block instanceof ContentBlock) { return ((ContentBlock) block).getVariantProperty(); }
        if (block instanceof ContentBlockFlower) { return ((ContentBlockFlower) block).getVariantProperty(); }
        if (block instanceof ContentBlockFalling) { return ((ContentBlockFalling) block).getVariantProperty(); }
        if (block instanceof ContentBlockSlab) { return (PropertyVariant) ((ContentBlockSlab) block).getVariantProperty(); }
        if (block instanceof ContentBlockFence) { return ((ContentBlockFence) block).getVariantProperty(); }
        if (block instanceof ContentBlockPane) { return ((ContentBlockPane) block).getVariantProperty(); }
        if (block instanceof ContentBlockWall) { return ((ContentBlockWall) block).getVariantProperty(); }
        return null;
    }
}
