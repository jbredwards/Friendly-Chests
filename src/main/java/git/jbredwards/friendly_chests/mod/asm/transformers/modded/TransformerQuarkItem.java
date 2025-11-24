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

package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.api.BlockSource;
import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import git.jbredwards.friendly_chests.mod.asm.transformers.vanilla.TransformerBlockChest;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.ClassNode;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.item.ItemChestBlock;
import vazkii.quark.decoration.tile.TileCustomChest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerQuarkItem implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        /*
         * New code:
         * // Update neighbors using the correct tile data.
         * @ASMOverwrite
         * public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState)
         * {
         *     return Hooks.placeBlockAt(this, stack, player, world, pos, side, newState);
         * }
         */
        overwriteMethod(classNode, "placeBlockAt", "placeBlockAt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFLnet/minecraft/block/state/IBlockState;)Z", adapter -> {
            adapter.loadThis();
            adapter.loadArg(0);
            adapter.loadArg(1);
            adapter.loadArg(2);
            adapter.loadArg(3);
            adapter.loadArg(4);
            adapter.loadArg(8);
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean placeBlockAt(@Nonnull final ItemChestBlock item, @Nonnull final ItemStack stack, @Nonnull final EntityPlayer player, @Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing side, @Nonnull final IBlockState defaultState) {
            @Nonnull final VariedChests.ChestType myType = ((BlockCustomChest)item.getBlock()).getCustomType(stack);
            @Nonnull final Pair<EnumFacing, ChestType> properties = TransformerBlockChest.Hooks.getPropertiesForPlacement(player, side, facing -> {
                @Nonnull final TileCustomChest fakeTile = new TileCustomChest();
                fakeTile.chestType = myType;

                @Nonnull final IBlockSource chest = new BlockSource(world, pos, defaultState, fakeTile);
                @Nonnull final IBlockSource other = new BlockSource(world, pos.offset(facing));
                return ((IChestMatchable)defaultState.getBlock()).chestMatches(chest, other) && !ChestType.get(other.getBlockState()).hasSideAttached() ? other.getBlockState().getValue(BlockChest.FACING) : null;
            });

            @Nonnull final IBlockState newState = defaultState.withProperty(BlockChest.FACING, properties.getLeft()).withProperty(ChestType.TYPE, properties.getRight());
            if(!world.setBlockState(pos, newState, Constants.BlockFlags.DEFAULT_AND_RERENDER)) return false;
            // Set block twice, to prevent updated neighbors from replacing this.
            world.setBlockState(pos, newState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
            @Nonnull final IBlockState state = world.getBlockState(pos);
            // Set tile entity data.
            if(state.getBlock() == item.getBlock()) {
                ItemChestBlock.setTileEntityNBT(world, player, pos, stack);
                @Nullable final TileEntity tile = world.getTileEntity(pos);

                if(tile instanceof TileCustomChest) ((TileCustomChest)tile).chestType = myType;
                TransformerBlockChest.Hooks.onBlockAdded((BlockCustomChest)item.getBlock(), world, pos, state);

                item.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
                if(player instanceof EntityPlayerMP) CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }

            return true;
        }
    }
}
