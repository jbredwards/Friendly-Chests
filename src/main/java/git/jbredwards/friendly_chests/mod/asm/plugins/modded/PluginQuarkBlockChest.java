package git.jbredwards.friendly_chests.mod.asm.plugins.modded;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.tree.ClassNode;
import vazkii.quark.decoration.block.BlockCustomChest;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class PluginQuarkBlockChest implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        classNode.interfaces.add("git/jbredwards/friendly_chests/api/IChestMatchable");
        classNode.methods.removeIf(method
                -> method.name.equals(obfuscated ? "func_185496_a" : "getBoundingBox")
                || method.name.equals(obfuscated ? "func_180633_a" : "onBlockPlacedBy")
                || method.name.equals(obfuscated ? "func_189418_a" : "getContainer"));
        /*
         * chestMatches:
         * New code:
         * //quark chests have to compare tile entity data
         * @ASMGenerated
         * public boolean chestMatches(World world, IBlockState state, BlockPos pos, IBlockState other, BlockPos otherPos)
         * {
         *     return Hooks.chestMatches(this, world, state, pos, other, otherPos);
         * }
         */
        addMethod(classNode, "chestMatches", "(Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)Z",
            "chestMatches", "(Lvazkii/quark/decoration/block/BlockCustomChest;Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)Z", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
                generator.visitVarInsn(ALOAD, 4);
                generator.visitVarInsn(ALOAD, 5);
            }
        );

        return false;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean chestMatches(@Nonnull BlockCustomChest block, @Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull IBlockState other, @Nonnull BlockPos otherPos) {
            return Block.isEqualTo(block, other.getBlock()) && block.getCustomType(world, pos) == block.getCustomType(world, otherPos);
        }
    }
}
