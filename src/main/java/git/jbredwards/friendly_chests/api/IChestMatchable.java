package git.jbredwards.friendly_chests.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public interface IChestMatchable
{
    default boolean chestMatches(@Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull IBlockState other, @Nonnull BlockPos otherPos) {
        return Block.isEqualTo(state.getBlock(), other.getBlock());
    }

    static boolean chestMatches(@Nonnull BlockChest chest, @Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull IBlockState other, @Nonnull BlockPos otherPos) {
        return ((IChestMatchable)chest).chestMatches(world, state, pos, other, otherPos);
    }
}
