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

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An implementation of {@link BlockSourceImpl} that caches the state and tile entity at its position.
 *
 * @since 2.0.0
 * @author jbred
 *
 */
public class BlockSource extends BlockSourceImpl
{
    @Nullable protected IBlockState state;
    @Nullable protected TileEntity tileEntity;
    protected boolean checkedTileEntity;

    public BlockSource(@Nonnull final World worldIn, @Nonnull final BlockPos posIn) { super(worldIn, posIn); }
    public BlockSource(@Nonnull final World worldIn, @Nonnull final BlockPos posIn, @Nonnull final IBlockState stateIn) {
        this(worldIn, posIn);
        state = stateIn;
    }

    public BlockSource(@Nonnull final World worldIn, @Nonnull final BlockPos posIn, @Nullable final TileEntity tileEntityIn) {
        this(worldIn, posIn);
        tileEntity = tileEntityIn;
        checkedTileEntity = true;
    }

    public BlockSource(@Nonnull final World worldIn, @Nonnull final BlockPos posIn, @Nonnull final IBlockState stateIn, @Nullable final TileEntity tileEntityIn) {
        this(worldIn, posIn, tileEntityIn);
        state = stateIn;
    }

    @Nullable
    @Override
    public <T extends TileEntity> T getBlockTileEntity() {
        if(checkedTileEntity) return (T)tileEntity;

        checkedTileEntity = true;
        return (T)(tileEntity = super.getBlockTileEntity());
    }

    @Nonnull
    @Override
    public IBlockState getBlockState() { return state == null ? state = super.getBlockState() : state; }
}
