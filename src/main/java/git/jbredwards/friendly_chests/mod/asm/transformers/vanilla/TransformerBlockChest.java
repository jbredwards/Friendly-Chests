package git.jbredwards.friendly_chests.mod.asm.transformers.vanilla;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

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
             * // Get bounding box from state instead of the world.
             * @ASMOverwrite
             * public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
             * {
             *     return Hooks.getBoundingBox(state);
             * }
             */
            overwriteMethod(classNode, DEOBFUSCATED ? "getBoundingBox" : "func_185496_a", "", adapter -> {
                adapter.visitVarInsn(ALOAD, 1);
                adapter.visitMethodInsn(INVOKESTATIC, getHookClass(), "getBoundingBox", "(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/AxisAlignedBB;", false);
            });
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nonnull
        public static AxisAlignedBB getBoundingBox(@Nonnull final IBlockState state) {
            @Nonnull final ChestType chestType = ChestType.get(state);
            if(!chestType.hasSideAttached()) return BlockChest.NOT_CONNECTED_AABB;
            else switch(chestType.getSideAttached(state.getValue(BlockChest.FACING))) {
                case NORTH: return BlockChest.NORTH_CHEST_AABB;
                case SOUTH: return BlockChest.SOUTH_CHEST_AABB;
                case EAST: return BlockChest.EAST_CHEST_AABB;
                case WEST: return BlockChest.WEST_CHEST_AABB;
                default: return BlockChest.NOT_CONNECTED_AABB;
            }
        }
    }
}
