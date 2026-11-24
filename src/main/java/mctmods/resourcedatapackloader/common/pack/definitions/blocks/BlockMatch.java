package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;

public final class BlockMatch {
    @Nullable private final Block block;
    @Nullable private final String oreName;
    @Nullable private volatile Set<Block> resolved;

    private BlockMatch(@Nullable Block block, @Nullable String oreName) {
        this.block = block;
        this.oreName = oreName;
    }

    public static BlockMatch of(Block block) { return new BlockMatch(block, null); }

    public static BlockMatch group(String oreName) { return new BlockMatch(null, oreName); }

    public boolean group() { return this.oreName != null; }

    public static boolean anyMatch(List<BlockMatch> matches, Block block, World world, BlockPos pos) {
        for (BlockMatch match : matches) {
            if (match.matches(block, world, pos)) { return true; }
        }
        return false;
    }

    public boolean matches(Block candidate, World world, BlockPos pos) {
        if (this.oreName == null) { return this.block == candidate; }
        Set<Block> found = this.resolved;
        if (found == null) {
            found = resolve(this.oreName, world, pos);
            this.resolved = found;
        }
        return found.contains(candidate);
    }

    private static Set<Block> resolve(String oreName, World world, BlockPos pos) {
        Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ItemStack stack : OreDictionary.getOres(oreName)) {
            Block block = blockOf(stack.getItem(), world, pos);
            if (block != Blocks.AIR) { blocks.add(block); }
        }
        return blocks;
    }

    public static Block blockOf(Item item, @Nullable World world, BlockPos pos) { return item instanceof IPlantable ? ((IPlantable) item).getPlant(world, pos).getBlock() : Block.getBlockFromItem(item); }
}
