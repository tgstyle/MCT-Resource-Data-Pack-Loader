package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBlockDoor extends BlockDoor implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    @Nullable private Item door;

    public ContentBlockDoor(BlockDef def) {
        super(def.material);
        this.def = def;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        ContentSetup.properties(this, def);
        setTranslationKey(def.registryName + "." + variant.name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(variant.hardness);
        setResistance(variant.resistance / def.explosionResistanceDivisor);
        setLightLevel(variant.light / 15.0F);
    }

    @Override public BlockDef getDef() { return def; }

    @Nullable public Item getDoorItem() { return door; }

    @Override @Nullable public Item createItem() {
        ItemDoor item = new ItemDoor(this);
        item.setTranslationKey(def.registryName + "." + def.at(0).name);
        ContentSetup.apply(item, def.creativeTab);
        this.door = item;
        return item;
    }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
        if (door == null || state.getValue(HALF) == EnumDoorHalf.UPPER) { return Items.AIR; }
        return door;
    }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return door == null ? ItemStack.EMPTY : new ItemStack(door);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
