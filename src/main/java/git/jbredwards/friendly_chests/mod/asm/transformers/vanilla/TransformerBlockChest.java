package git.jbredwards.friendly_chests.mod.asm.transformers.vanilla;

import git.jbredwards.friendly_chests.api.BlockSource;
import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerBlockChest implements IASMClassTransformer
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull final String name, @Nonnull final String transformedName, @Nonnull final byte[] basicClass) {
        return transformClassNode(basicClass, classNode -> {
            classNode.interfaces.add("git/jbredwards/friendly_chests/api/IChestMatchable");
            classNode.methods.removeIf(method -> method.name.equals(DEOBFUSCATED ? "canPlaceBlockAt" : "func_176196_c"));
            /*
             * New code:
             * // Improve post-placement validity check, by having it use ChestType.
             * @ASMOverwrite
             * public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state)
             * {
             *     return Hooks.checkForSurroundingChests(worldIn, pos, state);
             * }
             */
            overwriteMethod(classNode, "checkForSurroundingChests", "func_176455_e", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", adapter -> {
                adapter.loadArg(0);
                adapter.loadArg(1);
                adapter.loadArg(2);
            });
            /*
             * New code:
             * // Add ChestType state property.
             * @ASMOverwrite
             * public BlockStateContainer createBlockState()
             * {
             *     return Hooks.createBlockState(this);
             * }
             */
            overwriteMethod(classNode, "createBlockState", "func_180661_e", "()Lnet/minecraft/block/state/BlockStateContainer;", adapter -> {
                adapter.loadThis();
            });
            /*
             * New code:
             * // Serialize ChestType property.
             * @ASMOverwrite
             * public int getMetaFromState(IBlockState state)
             * {
             *     return Hooks.getMetaFromState(state);
             * }
             */
            overwriteMethod(classNode, "getMetaFromState", "func_176201_c", "(Lnet/minecraft/block/state/IBlockState;)I", adapter -> {
                adapter.loadArg(0);
            });
            /*
             * New code:
             * // Deserialize ChestType property.
             * @ASMOverwrite
             * public IBlockState getStateFromMeta(int meta)
             * {
             *     return Hooks.getStateFromMeta(this, meta);
             * }
             */
            overwriteMethod(classNode, "getStateFromMeta", "func_176203_a", "(I)Lnet/minecraft/block/state/IBlockState;", adapter -> {
                adapter.loadThis();
                adapter.loadArg(0);
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
             * // Get bounding box from state instead of the world.
             * @ASMOverwrite
             * public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
             * {
             *     return Hooks.getBoundingBox(state);
             * }
             */
            overwriteMethod(classNode, "getBoundingBox", "func_185496_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/AxisAlignedBB;", adapter -> {
                adapter.loadArg(0);
            });
            /*
             * New code:
             * // Get container from state instead of all neighbors.
             * @ASMOverwrite
             * public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking)
             * {
             *     return Hooks.getContainer(this, worldIn, pos, allowBlocking);
             * }
             */
            overwriteMethod(classNode, "getContainer", "func_189418_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/world/ILockableContainer;", adapter -> {
                adapter.loadThis();
                adapter.loadArg(0);
                adapter.loadArg(1);
                adapter.loadArg(2);
            });
            /*
             * New code:
             * // Check state instead of all neighbors.
             * @ASMOverwrite
             * public boolean isDoubleChest(World worldIn, BlockPos pos)
             * {
             *     return Hooks.isDoubleChest(this, worldIn, pos);
             * }
             */
            overwriteMethod(classNode, "isDoubleChest", "func_176454_e", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", adapter -> {
                adapter.loadThis();
                adapter.loadArg(0);
                adapter.loadArg(1);
            });
            /*
             * New code:
             * // Update state here when a neighbor disappears.
             * @ASMOverwrite
             * public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
             * {
             *     Hooks.neighborChanged(this, state, worldIn, pos);
             * }
             */
            overwriteMethod(classNode, "neighborChanged", "func_189540_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V", adapter -> {
                adapter.loadThis();
                adapter.loadArg(0);
                adapter.loadArg(1);
                adapter.loadArg(2);
            });
            /*
             * New code:
             * // Update block if it's undefined or invalid.
             * @ASMOverwrite
             * public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
             * {
             *     Hooks.onBlockAdded(this, worldIn, pos, state);
             * }
             */
            overwriteMethod(classNode, "onBlockAdded", "func_180633_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", adapter -> {
                adapter.loadThis();
                adapter.loadArg(0);
                adapter.loadArg(1);
                adapter.loadArg(2);
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
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nonnull
        public static IBlockState checkForSurroundingChests(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
            if(world.isRemote) return state;

            @Nullable final IBlockState replacement = sidelessCheckForSurroundingChests(world, pos, state);
            if(replacement != null) {
                world.setBlockState(pos, replacement);
                return replacement;
            }

            return state;
        }

        @Nonnull
        public static BlockStateContainer createBlockState(@Nonnull final BlockChest block) {
            return new BlockStateContainer(block, BlockChest.FACING, ChestType.TYPE);
        }

        public static int getMetaFromState(@Nonnull final IBlockState state) {
            return state.getValue(BlockChest.FACING).getHorizontalIndex() | ChestType.get(state).index() << 2;
        }

        @Nonnull
        public static IBlockState getStateFromMeta(@Nonnull final BlockChest block, final int meta) {
            return block.getDefaultState()
                    .withProperty(BlockChest.FACING, EnumFacing.byHorizontalIndex(meta & 3))
                    .withProperty(ChestType.TYPE, ChestType.fromIndex(meta >> 2));
        }

        @Nonnull
        public static IBlockState getStateForPlacement(@Nonnull final BlockChest block, @Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing side, @Nonnull final EntityLivingBase placer) {
            final boolean isSneaking = placer.isSneaking();

            @Nonnull EnumFacing facing = placer.getHorizontalFacing().getOpposite();
            @Nonnull ChestType type = ChestType.SINGLE;

            if(side.getAxis().isHorizontal() && isSneaking) {
                @Nullable final EnumFacing sideToAttach = getDirectionToAttach(block, world, pos, side.getOpposite());
                if(sideToAttach != null && sideToAttach.getAxis() != side.getAxis()) {
                    facing = sideToAttach;
                    type = sideToAttach.rotateYCCW() == side.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
                }
            }

            if(type == ChestType.SINGLE && !isSneaking) {
                @Nullable final EnumFacing left = getDirectionToAttach(block, world, pos, facing.rotateY());
                if(left != null && facing != left.getOpposite()) type = ChestType.LEFT;
                else {
                    @Nullable final EnumFacing right = getDirectionToAttach(block, world, pos, facing.rotateYCCW());
                    if(right != null && facing != right.getOpposite()) type = ChestType.RIGHT;
                }
            }

            return block.getDefaultState().withProperty(BlockChest.FACING, facing).withProperty(ChestType.TYPE, type);
        }

        //helper
        @Nullable
        public static EnumFacing getDirectionToAttach(@Nonnull final BlockChest block, @Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing facing) {
            @Nonnull final IBlockSource chest = new BlockSource(world, pos, block.getDefaultState(), null);
            @Nonnull final IBlockSource other = new BlockSource(world, pos.offset(facing));

            return ((IChestMatchable)block).chestMatches(chest, other) && ((IChestMatchable)block).canChestConnectTo(chest, other) ? other.getBlockState().getValue(BlockChest.FACING) : null;
        }

        @Nonnull
        public static AxisAlignedBB getBoundingBox(@Nonnull final IBlockState state) {
            @Nonnull final ChestType type = ChestType.get(state);
            if(!type.hasSideAttached()) return BlockChest.NOT_CONNECTED_AABB;
            else switch(type.getSideAttached(state)) {
                case NORTH: return BlockChest.NORTH_CHEST_AABB;
                case SOUTH: return BlockChest.SOUTH_CHEST_AABB;
                case EAST: return BlockChest.EAST_CHEST_AABB;
                case WEST: return BlockChest.WEST_CHEST_AABB;
                default: throw new IllegalStateException("What the fuck?");
            }
        }

        @Nullable
        public static ILockableContainer getContainer(@Nonnull final BlockChest block, @Nonnull final World world, @Nonnull final BlockPos pos, final boolean allowBlocking) {
            @Nullable final TileEntity tile = world.getTileEntity(pos);
            if(!(tile instanceof TileEntityChest) || !allowBlocking && block.isBlocked(world, pos)) return null;

            @Nonnull final IBlockState chest = world.getBlockState(pos);
            @Nonnull final ChestType type = ChestType.get(chest);

            if(!type.hasSideAttached()) return (ILockableContainer)tile;
            @Nonnull final EnumFacing sideAttached = type.getSideAttached(chest);
            @Nonnull final BlockPos attached = pos.offset(sideAttached);

            if(!allowBlocking && block.isBlocked(world, attached)) return null;
            @Nullable final TileEntity neighbor = world.getTileEntity(attached);

            if(!(neighbor instanceof TileEntityChest)) return null;
            return sideAttached.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE
                    ? new InventoryLargeChest("container.chestDouble", (ILockableContainer)tile, (ILockableContainer)neighbor)
                    : new InventoryLargeChest("container.chestDouble", (ILockableContainer)neighbor, (ILockableContainer)tile);
        }

        public static boolean isDoubleChest(@Nonnull final BlockChest block, @Nonnull final World world, @Nonnull final BlockPos pos) {
            @Nonnull final IBlockState state = world.getBlockState(pos);
            return state.getBlock() == block && ChestType.get(state).hasSideAttached();
        }

        public static void neighborChanged(@Nonnull final BlockChest block, @Nonnull final IBlockState state, @Nonnull final World world, @Nonnull final BlockPos pos) {
            @Nullable final TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityChest) tile.updateContainingBlockInfo();

            @Nonnull final ChestType type = ChestType.get(state);
            if(type.hasSideAttached()) {
                @Nonnull final BlockPos otherPos = pos.offset(type.getSideAttached(state));
                @Nonnull final IBlockState other = world.getBlockState(otherPos);

                if(!IChestMatchable.chestMatches(block, world, state, pos, other, otherPos)
                || state.getValue(BlockChest.FACING) != other.getValue(BlockChest.FACING)
                || type.getOpposite() != ChestType.get(other))

                    // Neighboring chest block is no longer connected.
                    world.setBlockState(pos, state.withProperty(ChestType.TYPE, ChestType.SINGLE));
            }
        }

        public static void onBlockAdded(@Nonnull final BlockChest block, @Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
            block.checkForSurroundingChests(worldIn, pos, state);

            @Nonnull final IBlockSource chest = new BlockSource(worldIn, pos);
            for(@Nonnull final EnumFacing side : EnumFacing.HORIZONTALS) {
                @Nonnull final IBlockSource other = new BlockSource(worldIn, pos.offset(side));
                if(((IChestMatchable)block).chestMatches(chest, other)) block.checkForSurroundingChests(worldIn, other.getBlockPos(), other.getBlockState());
            }
        }

        public static void onBlockPlacedBy(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final ItemStack stack) {
            if(stack.hasDisplayName()) {
                @Nullable final TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TileEntityChest) ((TileEntityChest)tile).setCustomName(stack.getDisplayName());
            }
        }

        // helper
        @Nullable
        public static IBlockState sidelessCheckForSurroundingChests(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
            @Nonnull final IBlockSource chest = new BlockSource(world, pos, state);
            @Nonnull final ChestType type = ChestType.get(state);
            switch(type) {
                case LEFT:
                case RIGHT: {
                    @Nonnull final EnumFacing facing = state.getValue(BlockChest.FACING);
                    @Nonnull final EnumFacing attached = type.getSideAttached(facing);
                    @Nonnull final IBlockSource other = new BlockSource(world, pos.offset(attached));
                    // Assume this chest is part of world gen, and that its neighbor has not generated yet.
                    if(!((IChestMatchable)state.getBlock()).chestMatches(chest, other)) return null;
                    // Neighbor is already connected (or will be), no operation.
                    @Nonnull final ChestType neighborType = ChestType.get(other.getBlockState());
                    if(!neighborType.hasSideAttached() || neighborType.getSideAttached(other.getBlockState()) == attached.getOpposite()) {
                        return null;
                    }
                }
                default: {
                    @Nonnull final IBlockSource north = new BlockSource(world, pos.north());
                    final boolean northMatches = ((IChestMatchable)state.getBlock()).chestMatches(chest, north);
                    if(northMatches) {
                        @Nonnull final ChestType neighborType = ChestType.get(north.getBlockState());
                        if(neighborType.hasSideAttached() && neighborType.getSideAttached(north.getBlockState()) == EnumFacing.SOUTH) {
                            return state.withProperty(BlockChest.FACING, north.getBlockState().getValue(BlockChest.FACING)).withProperty(ChestType.TYPE, neighborType.getOpposite());
                        }
                    }

                    @Nonnull final IBlockSource south = new BlockSource(world, pos.south());
                    final boolean southMatches = ((IChestMatchable)state.getBlock()).chestMatches(chest, south);
                    if(southMatches) {
                        @Nonnull final ChestType neighborType = ChestType.get(south.getBlockState());
                        if(neighborType.hasSideAttached() && neighborType.getSideAttached(south.getBlockState()) == EnumFacing.NORTH) {
                            return state.withProperty(BlockChest.FACING, south.getBlockState().getValue(BlockChest.FACING)).withProperty(ChestType.TYPE, neighborType.getOpposite());
                        }
                    }

                    @Nonnull final IBlockSource east = new BlockSource(world, pos.east());
                    final boolean eastMatches = ((IChestMatchable)state.getBlock()).chestMatches(chest, east);
                    if(eastMatches) {
                        @Nonnull final ChestType neighborType = ChestType.get(east.getBlockState());
                        if(neighborType.hasSideAttached() && neighborType.getSideAttached(east.getBlockState()) == EnumFacing.WEST) {
                            return state.withProperty(BlockChest.FACING, east.getBlockState().getValue(BlockChest.FACING)).withProperty(ChestType.TYPE, neighborType.getOpposite());
                        }
                    }

                    @Nonnull final IBlockSource west = new BlockSource(world, pos.west());
                    final boolean westMatches = ((IChestMatchable)state.getBlock()).chestMatches(chest, west);
                    if(westMatches) {
                        @Nonnull final ChestType neighborType = ChestType.get(west.getBlockState());
                        if(neighborType.hasSideAttached() && neighborType.getSideAttached(west.getBlockState()) == EnumFacing.EAST) {
                            return state.withProperty(BlockChest.FACING, west.getBlockState().getValue(BlockChest.FACING)).withProperty(ChestType.TYPE, neighborType.getOpposite());
                        }
                    }

                    if(type == ChestType.SINGLE) return null;
                    else if(northMatches && !ChestType.get(north.getBlockState()).hasSideAttached()) return getStateWithFacing(state, north.getBlockState(), EnumFacing.EAST, ChestType.RIGHT);
                    else if(southMatches && !ChestType.get(south.getBlockState()).hasSideAttached()) return getStateWithFacing(state, south.getBlockState(), EnumFacing.EAST, ChestType.LEFT);
                    else if(eastMatches && !ChestType.get(east.getBlockState()).hasSideAttached()) return getStateWithFacing(state, east.getBlockState(), EnumFacing.SOUTH, ChestType.RIGHT);
                    else if(westMatches && !ChestType.get(west.getBlockState()).hasSideAttached()) return getStateWithFacing(state, west.getBlockState(), EnumFacing.SOUTH, ChestType.LEFT);
                    else return state.withProperty(ChestType.TYPE, ChestType.SINGLE);
                }
            }
        }

        // helper
        @Nonnull
        private static IBlockState getStateWithFacing(@Nonnull final IBlockState chest, @Nonnull final IBlockState other, @Nonnull final EnumFacing fallback, @Nonnull final ChestType fallbackType) {
            @Nonnull final EnumFacing currentFacing = chest.getValue(BlockChest.FACING);
            @Nonnull final EnumFacing neighborFacing = other.getValue(BlockChest.FACING);

            if(currentFacing.getAxis() == fallback.getAxis()) return chest
                    .withProperty(ChestType.TYPE, currentFacing == fallback ? fallbackType : fallbackType.getOpposite());

            else if(neighborFacing.getAxis() == fallback.getAxis()) return chest
                    .withProperty(ChestType.TYPE, neighborFacing == fallback ? fallbackType : fallbackType.getOpposite())
                    .withProperty(BlockChest.FACING, neighborFacing);

            else return chest.withProperty(ChestType.TYPE, fallbackType).withProperty(BlockChest.FACING, fallback);
        }
    }
}
