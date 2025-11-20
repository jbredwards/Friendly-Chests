package git.jbredwards.friendly_chests.mod.asm.transformers.vanilla;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerTileEntityChest implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        /*
         * New code:
         * // Update neighboring chests if they're no longer connected.
         * @ASMOverwrite
         * protected TileEntityChest getAdjacentChest(EnumFacing side)
         * {
         *     return Hooks.getAdjacentChest(this, side);
         * }
         */
        overwriteMethod(classNode, "getAdjacentChest", "func_174911_a", "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/tileentity/TileEntityChest;", adapter -> {
            adapter.loadThis();
            adapter.loadArg(0);
        });
        /*
         * New code:
         * // Return old metadata values for rendering.
         * @ASMOverwrite
         * public int getBlockMetadata()
         * {
         *     return Hooks.getBlockMetadata(this, super.getBlockMetadata());
         * }
         */
        overwriteMethod(classNode, "getBlockMetadata", "func_145832_p", "()I", adapter -> {
            adapter.loadThis();
            adapter.loadThis();
            adapter.visitMethodInsn(INVOKESPECIAL, classNode.superName, DEOBFUSCATED ? "getBlockMetadata" : "func_145832_p", "()I", false);
        });
        /*
         * New code:
         * // Only match chests that are connected to this one.
         * @ASMOverwrite
         * private boolean isChestAt(BlockPos posIn)
         * {
         *     return Hooks.isChestAt(this, newState);
         * }
         */
        overwriteMethod(classNode, "isChestAt", "func_174912_b", "(Lnet/minecraft/util/math/BlockPos;)Z", adapter -> {
            adapter.loadThis();
            adapter.loadArg(0);
        });
        /*
         * New code:
         * // Ensure that modded chest tile entities don't refresh when their state changes.
         * @ASMOverwrite
         * public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
         * {
         *     return Hooks.shouldRefresh(oldState, newState);
         * }
         */
        overwriteMethod(classNode, "shouldRefresh", "shouldRefresh", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)Z", adapter -> {
            adapter.loadArg(2);
            adapter.loadArg(3);
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static TileEntityChest getAdjacentChest(@Nonnull final TileEntityChest tile, @Nonnull final EnumFacing direction) {
            if(!tile.hasWorld()) return null;
            @Nonnull final BlockPos offset = tile.getPos().offset(direction);
            @Nullable final TileEntity neighbor = tile.getWorld().getTileEntity(offset);

            if(neighbor instanceof TileEntityChest) {
                if(isChestAt(tile, offset)) {
                    ((TileEntityChest)neighbor).setNeighbor(tile, direction);
                    return (TileEntityChest)neighbor;
                }

                // Is a chest tile, but not connected, update neighbor.
                else switch(direction.getOpposite()) {
                    case NORTH: ((TileEntityChest)neighbor).adjacentChestZNeg = null; break;
                    case SOUTH: ((TileEntityChest)neighbor).adjacentChestZPos = null; break;
                    case EAST: ((TileEntityChest)neighbor).adjacentChestXPos = null; break;
                    case WEST: ((TileEntityChest)neighbor).adjacentChestXNeg = null; break;
                }
            }

            return null;
        }

        public static int getBlockMetadata(@Nonnull final TileEntityChest tile, final int realMeta) {
            return tile.getWorld().isRemote && tile.getBlockType() instanceof BlockChest ? EnumFacing.byHorizontalIndex(realMeta & 3).getIndex() : realMeta;
        }

        public static boolean isChestAt(@Nonnull final TileEntityChest tile, @Nonnull final BlockPos pos) {
            if(!tile.hasWorld() || !(tile.getBlockType() instanceof BlockChest)) return false;
            tile.getBlockMetadata(); // Set internal metadata value.

            @Nonnull final ChestType type = ChestType.fromOrdinal(tile.blockMetadata >> 2);
            if(!type.hasSideAttached() || !tile.getPos().offset(type.getSideAttached(EnumFacing.byHorizontalIndex(tile.blockMetadata & 3))).equals(pos)) return false;

            @Nonnull final IBlockState neighbor = tile.getWorld().getBlockState(pos);
            if(!IChestMatchable.chestMatches((BlockChest)tile.getBlockType(), tile.getWorld(), tile.getBlockType().getStateFromMeta(tile.blockMetadata), tile.getPos(), neighbor, pos)) return false;

            @Nonnull final ChestType neighborType = ChestType.get(neighbor);
            return neighborType.hasOpposite() && neighborType.getOpposite() == type;
        }

        public static boolean shouldRefresh(@Nonnull final IBlockState oldState, @Nonnull final IBlockState newState) {
            return oldState.getBlock() instanceof BlockChest ? oldState.getBlock() != newState.getBlock() : oldState != newState;
        }
    }
}
