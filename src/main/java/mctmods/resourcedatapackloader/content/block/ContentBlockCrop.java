package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBlockCrop extends BlockCrops implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    private ItemStack seed = new ItemStack(Items.WHEAT_SEEDS);
    private ItemStack crop = new ItemStack(Items.WHEAT);

    public ContentBlockCrop(BlockDef def) {
        this.def = def;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        ContentSetup.properties(this, def);
        setTranslationKey(def.registryName + "." + variant.name);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(variant.hardness);
    }

    public void resolve(ItemStack seedStack, ItemStack cropStack) {
        if (!seedStack.isEmpty()) { this.seed = seedStack.copy(); }
        if (!cropStack.isEmpty()) { this.crop = cropStack.copy(); }
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return null; }

    @Override public int getMaxAge() { return def.cropMaxAge; }

    @Override @Nonnull protected Item getSeed() { return seed.getItem(); }

    @Override @Nonnull protected Item getCrop() { return crop.getItem(); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return seed.copy(); }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        int age = getAge(state);
        if (age < getMaxAge()) {
            drops.add(seed.copy());
            return;
        }
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        drops.add(crop.copy());
        for (int i = 0; i < 3 + fortune; i++) {
            if (rand.nextInt(2 * getMaxAge()) <= age) { drops.add(seed.copy()); }
        }
    }
}
