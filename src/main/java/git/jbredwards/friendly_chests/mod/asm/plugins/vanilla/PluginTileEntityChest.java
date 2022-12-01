package git.jbredwards.friendly_chests.mod.asm.plugins.vanilla;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
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
public final class PluginTileEntityChest implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        /*
         * getAdjacentChest:
         * New code:
         * @Nullable
         * protected TileEntityChest getAdjacentChest(EnumFacing side)
         * {
         *     return Hooks.getAdjacentChest(this, side);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_174911_a" : "getAdjacentChest"),
            "getAdjacentChest", "(Lnet/minecraft/tileentity/TileEntityChest;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/tileentity/TileEntityChest;", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
            }
        );
        /*
         * getBlockMetadata:
         * New code:
         * //this method returns the same value it would for vanilla (needed for chest renderer)
         * @ASMGenerator
         * public int getBlockMetadata()
         * {
         *     return Hooks.fixMetadata(super.getBlockMetadata());
         * }
         */
        addMethod(classNode, obfuscated ? "func_145832_p" : "getBlockMetadata", "()I",
            "fixMetadata", "(Lnet/minecraft/tileentity/TileEntityChest;I)I", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 0);
                generator.visitMethodInsn(INVOKESPECIAL, "net/minecraft/tileentity/TileEntity", obfuscated ? "func_145832_p" : "getBlockMetadata", "()I", false);
            }
        );
        /*
         * shouldRefresh:
         * New code:
         * //ensure mods account for getBlockMetadata change
         * @ASMGenerated
         * public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
         * {
         *     return Hooks.shouldRefresh(oldState, newState);
         * }
         */
        addMethod(classNode, "shouldRefresh", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)Z",
            "shouldRefresh", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)Z", generator -> {
                generator.visitVarInsn(ALOAD, 3);
                generator.visitVarInsn(ALOAD, 4);
            }
        );

        return false;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static TileEntityChest getAdjacentChest(@Nonnull TileEntityChest here, @Nonnull EnumFacing sideToCheck) {
            if(here.getBlockType() instanceof BlockChest) {
                final IBlockState state = here.getWorld().getBlockState(here.getPos());
                if(state.getValue(ChestType.TYPE) != ChestType.SINGLE) {
                    final EnumFacing sideConnected = ChestType.getDirectionToAttached(state);
                    if(sideConnected == sideToCheck) {
                        final BlockPos otherPos = here.getPos().offset(sideConnected);
                        if(IChestMatchable.chestMatches((BlockChest)here.getBlockType(), here.getWorld(), state, here.getPos(), here.getWorld().getBlockState(otherPos), otherPos)) {
                            final TileEntity tile = here.getWorld().getTileEntity(otherPos);
                            if(tile instanceof TileEntityChest) {
                                here.setNeighbor(here, sideToCheck.getOpposite());
                                return (TileEntityChest)tile;
                            }
                        }
                    }
                }
            }

            return null;
        }

        public static int fixMetadata(@Nonnull TileEntityChest tile, int meta) {
            return tile.getBlockType() instanceof BlockChest ? EnumFacing.byHorizontalIndex(meta & 3).getIndex() : meta;
        }

        public static boolean shouldRefresh(@Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
            return oldState.getBlock() != newState.getBlock();
        }
    }
}
