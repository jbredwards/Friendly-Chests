package git.jbredwards.friendly_chests.mod.asm.plugins.vanilla;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import git.jbredwards.friendly_chests.api.ChestType;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;

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
         * checkForAdjacentChests:
         * New code:
         * //optimize this method and don't check chests every tick
         * public void checkForAdjacentChests()
         * {
         *     Hooks.checkForAdjacentChests(this);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_145979_i" : "checkForAdjacentChests"),
            "checkForAdjacentChests", "(Lnet/minecraft/tileentity/TileEntityChest;)V", generator -> generator.visitVarInsn(ALOAD, 0));
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
        public static void checkForAdjacentChests(@Nonnull TileEntityChest tile) {
            if(tile.hasWorld() && tile.getWorld().isAreaLoaded(tile.getPos(), 1)) {
                tile.getBlockMetadata(); //set internal metadata value
                if(tile.getBlockType() instanceof BlockChest) { //fix thaumcraft conflict
                    final ChestType type = ChestType.fromIndex(tile.blockMetadata >> 2);
                    if(type == ChestType.SINGLE) {
                        tile.adjacentChestChecked = false;
                        tile.adjacentChestZNeg = null;
                        tile.adjacentChestZPos = null;
                        tile.adjacentChestXNeg = null;
                        tile.adjacentChestXPos = null;
                    }

                    //check neighboring adjacent chests
                    else if(!tile.adjacentChestChecked) {
                        tile.adjacentChestZNeg = null;
                        tile.adjacentChestZPos = null;
                        tile.adjacentChestXNeg = null;
                        tile.adjacentChestXPos = null;

                        final EnumFacing connectedSide = type.rotate(EnumFacing.byHorizontalIndex(tile.blockMetadata & 3));
                        final TileEntity neighbor = tile.getWorld().getTileEntity(tile.getPos().offset(connectedSide));

                        if(neighbor instanceof TileEntityChest) {
                            tile.adjacentChestChecked = true;
                            switch(connectedSide) {
                                case NORTH:
                                    tile.adjacentChestZNeg = (TileEntityChest)neighbor;
                                    break;
                                case SOUTH:
                                    tile.adjacentChestZPos = (TileEntityChest)neighbor;
                                    break;
                                case WEST:
                                    tile.adjacentChestXNeg = (TileEntityChest)neighbor;
                                    break;
                                case EAST:
                                    tile.adjacentChestXPos = (TileEntityChest)neighbor;
                            }
                        }
                    }
                }
            }
        }

        public static int fixMetadata(@Nonnull TileEntityChest tile, int meta) {
            return tile.getBlockType() instanceof BlockChest ? EnumFacing.byHorizontalIndex(meta & 3).getIndex() : meta;
        }

        public static boolean shouldRefresh(@Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
            return oldState.getBlock() != newState.getBlock();
        }
    }
}
