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

import com.teammetallurgy.atum.blocks.base.BlockChestBase;
import com.teammetallurgy.atum.blocks.base.tileentity.TileEntityChestBase;
import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockChestSpawner;
import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockLimestoneChest;
import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockSarcophagus;
import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import git.jbredwards.friendly_chests.mod.asm.transformers.vanilla.TransformerBlockChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.VarInsnNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerAtumBlockBase implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        classNode.methods.removeIf(method
                -> method.name.equals(DEOBFUSCATED ? "getBoundingBox" : "func_185496_a")
                || method.name.equals(DEOBFUSCATED ? "getContainer" : "func_189418_a")
                || method.name.equals(DEOBFUSCATED ? "onBlockAdded" : "func_176213_c"));
        /*
         * Old code:
         * for (EnumFacing horizontal : EnumFacing.HORIZONTALS)
         * {
         *     ...
         * }
         *
         * New code:
         * // Use state to find neighbor sarcophagus, instead of using all neighbors.
         * for (EnumFacing horizontal : git.jbredwards.friendly_chests.api.ChestType.getDirectionsToAttached(state))
         * {
         *     ...
         * }
         */
        lockHorizontals(classNode, "harvestBlock", "func_180657_a", instructions -> {
            instructions.add(new VarInsnNode(ALOAD, 4));
        });
        /*
         * New code:
         * // For double chests, only allow placement if there's room.
         * @ASMOverwrite
         * public boolean canPlaceBlockAt(World world, BlockPos pos)
         * {
         *     return Hooks.canPlaceBlockAt(this, world, pos);
         * }
         */
        overwriteMethod(classNode, "canPlaceBlockAt", "func_176196_c", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", adapter -> {
            adapter.loadThis();
            adapter.loadArg(0);
            adapter.loadArg(1);
        });
        /*
         * New code:
         * // Atum chests have different "connecting" properties.
         * @ASMOverwrite
         * public boolean chestMatches(World world, IBlockState state, BlockPos pos, IBlockState other, BlockPos otherPos)
         * {
         *     return Hooks.chestMatches(this, state, other);
         * }
         */
        overwriteMethod(classNode, "chestMatches", "chestMatches", "(Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)Z", adapter -> {
            adapter.loadThis();
            adapter.loadArg(1);
            adapter.loadArg(3);
        });
        /*
         * New code:
         * // Allow for much finer control over chest placement.
         * @ASMOverwrite
         * public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
         * {
         *     return Hooks.getStateForPlacement(this, worldIn, pos, facing, placer);
         * }
         */
        overwriteMethod(classNode, "getStateForPlacement", "func_180642_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFILnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", adapter -> {
            adapter.loadThis();
            adapter.loadArg(0);
            adapter.loadArg(1);
            adapter.loadArg(2);
            adapter.loadArg(7);
        });
        /*
         * New code:
         * // Transfer custom item name to chest if present.
         * @ASMOverwrite
         * public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
         * {
         *     Hooks.onBlockPlacedBy(worldIn, pos, state, stack);
         * }
         */
        overwriteMethod(classNode, "onBlockPlacedBy", "func_180633_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;)V", adapter -> {
            adapter.loadArg(0);
            adapter.loadArg(1);
            adapter.loadArg(2);
            adapter.loadArg(4);
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean canPlaceBlockAt(@Nonnull final BlockChestBase block, @Nonnull final World world, @Nonnull final BlockPos pos) {
            if(!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) return false;
            else if(!(block instanceof BlockSarcophagus)) return true;

            for(@Nonnull final EnumFacing direction : EnumFacing.HORIZONTALS) if(canPlaceSarcophagusAt(world, pos.offset(direction))) return true;
            return false;
        }

        // helper
        private static boolean canPlaceSarcophagusAt(@Nonnull final World world, @Nonnull final BlockPos pos) {
            return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && world.checkNoEntityCollision(new AxisAlignedBB(pos));
        }

        public static boolean chestMatches(@Nonnull final BlockChestBase block, @Nonnull final IBlockState state, @Nonnull final IBlockState other) {
            if(!Block.isEqualTo(state.getBlock(), other.getBlock())) return false;

            // TileEntityChestBase "canBe" values are actually hardcoded, so we can check their values early.
            else if(block instanceof BlockChestSpawner) return false;
            else if(block instanceof BlockLimestoneChest || block instanceof BlockSarcophagus) return true;

            // Unknown block (probably an addon mod).
            throw new UnsupportedOperationException("Unknown Atum block found, report this to Friendly Chests! " + block);
        }

        @Nonnull
        public static IBlockState getStateForPlacement(@Nonnull final BlockChestBase block, @Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing side, @Nonnull final EntityLivingBase placer) {
            if(block instanceof BlockLimestoneChest || block instanceof BlockChestSpawner) return TransformerBlockChest.Hooks.getStateForPlacement(block, world, pos, side, placer);
            else if(block instanceof BlockSarcophagus) {
                @Nonnull final EnumFacing facing = placer.getHorizontalFacing().getOpposite();
                if(canPlaceSarcophagusAt(world, pos.offset(facing.rotateYCCW()))) return block.getDefaultState().withProperty(BlockChest.FACING, facing).withProperty(ChestType.TYPE, ChestType.RIGHT);
                if(canPlaceSarcophagusAt(world, pos.offset(facing.rotateY()))) return block.getDefaultState().withProperty(BlockChest.FACING, facing).withProperty(ChestType.TYPE, ChestType.LEFT);
                // Place sideways, if optimal placements are invalid.
                if(canPlaceSarcophagusAt(world, pos.offset(facing.getOpposite()))) return block.getDefaultState().withProperty(BlockChest.FACING, facing.rotateY()).withProperty(ChestType.TYPE, ChestType.LEFT);
                else return block.getDefaultState().withProperty(BlockChest.FACING, facing.rotateY()).withProperty(ChestType.TYPE, ChestType.RIGHT);
            }

            // Unknown block (probably an addon mod).
            throw new UnsupportedOperationException("Unknown Atum block found, report this to Friendly Chests! " + block);
        }

        public static void onBlockPlacedBy(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final ItemStack stack) {
            @Nullable final TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityChestBase) {
                @Nonnull final TileEntityChestBase tileChest = (TileEntityChestBase)tile;
                if(stack.hasDisplayName()) tileChest.setCustomName(stack.getDisplayName());
                if(!tileChest.canBeSingle && tileChest.canBeDouble) {
                    @Nonnull final ChestType type = ChestType.get(state);
                    world.setBlockState(pos.offset(type.getSideAttached(state)), state.withProperty(ChestType.TYPE, type.getOpposite()), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
            }
        }
    }
}
