/*
 * Copyright (C) <2025 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.friendly_chests.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Have your chest block implement this if it should have custom chest connecting conditions.
 * <p>
 * Note: {@link BlockChest} automatically implements this at runtime.
 * </p>
 *
 * @since 1.0.0
 * @author jbred
 *
 */
public interface IChestMatchable
{
    /**
     * @return Whether this chest matches the other one provided.
     * @since 2.0.0
     */
    default boolean chestMatches(@Nonnull final IBlockSource chest, @Nonnull final IBlockSource other) {
        return chestMatches(chest.getWorld(), chest.getBlockState(), chest.getBlockPos(), other.getBlockState(), other.getBlockPos());
    }

    /**
     * Utility method that returns true if the chest matches the other one provided.
     * @since 1.0.0
     */
    static boolean chestMatches(@Nonnull final BlockChest chest, @Nonnull final World world, @Nonnull final IBlockState state, @Nonnull final BlockPos pos, @Nonnull final IBlockState other, @Nonnull final BlockPos otherPos) {
        return ((IChestMatchable)chest).chestMatches(new BlockSource(world, pos, state), new BlockSource(world, otherPos, other));
    }

    /**
     * Use the new {@link IBlockSource}-version above instead.
     * @since 1.0.0
     */
    @Deprecated
    default boolean chestMatches(@Nonnull final World world, @Nonnull final IBlockState state, @Nonnull final BlockPos pos, @Nonnull final IBlockState other, @Nonnull final BlockPos otherPos) {
        return Block.isEqualTo(state.getBlock(), other.getBlock());
    }
}
