package git.jbredwards.friendly_chests.mod.asm.plugins.modded;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class PluginLootr implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        /*
         * chestMatches:
         * New code:
         * //lootr chests should never be connected
         * @ASMGenerated
         * public boolean chestMatches(World world, IBlockState state, BlockPos pos, IBlockState other, BlockPos otherPos)
         * {
         *     return false;
         * }
         */
        addMethod(classNode, "chestMatches", "(Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)Z", null, null,
            generator -> generator.visitInsn(ICONST_0));

        return false;
    }
}
