package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TransformerLootrBlock implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        /*
         * New code:
         * // Lootr chests should never be connected.
         * @ASMOverwrite
         * public boolean chestMatches(World world, IBlockState state, BlockPos pos, IBlockState other, BlockPos otherPos)
         * {
         *     return false;
         * }
         */
        overwriteMethod(classNode, "chestMatches", "chestMatches", "(Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)Z", adapter -> {});
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean chestMatches() {
            return false;
        }
    }
}
