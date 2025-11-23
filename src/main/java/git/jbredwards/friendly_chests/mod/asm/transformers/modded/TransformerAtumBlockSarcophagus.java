package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockSarcophagus;
import com.teammetallurgy.atum.blocks.stone.limestone.chest.tileentity.TileEntitySarcophagus;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import git.jbredwards.friendly_chests.mod.asm.transformers.vanilla.TransformerBlockChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.VarInsnNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerAtumBlockSarcophagus implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
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
         * Old code:
         * for (EnumFacing horizontal : EnumFacing.HORIZONTALS)
         * {
         *     ...
         * }
         *
         * New code:
         * // Use state to find neighbor sarcophagus, instead of using all neighbors.
         * for (EnumFacing horizontal : git.jbredwards.friendly_chests.api.ChestType.getDirectionsToAttached(this.world.getBlockState(sarcophagusPos)))
         * {
         *     ...
         * }
         */
        lockHorizontals(classNode, "onBlockActivated", "func_180639_a", instructions -> {
            instructions.add(new VarInsnNode(ALOAD, 3));
        });
        /*
         * Old code:
         * for (EnumFacing horizontal : EnumFacing.HORIZONTALS)
         * {
         *     ...
         * }
         *
         * New code:
         * // Use state to find neighbor sarcophagus, instead of using all neighbors.
         * for (EnumFacing horizontal : git.jbredwards.friendly_chests.api.ChestType.getDirectionsToAttached(this.world.getBlockState(sarcophagusPos)))
         * {
         *     ...
         * }
         */
        lockHorizontals(classNode, "onBlockPlacedBy", "func_180633_a", instructions -> {
            instructions.add(new VarInsnNode(ALOAD, 3));
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static ILockableContainer getContainer(@Nonnull final BlockSarcophagus block, @Nonnull final World world, @Nonnull final BlockPos pos, final boolean allowBlocking) {
            @Nullable final TileEntity tile = world.getTileEntity(pos);
            return tile instanceof TileEntitySarcophagus && ((TileEntitySarcophagus)tile).isOpenable ? TransformerBlockChest.Hooks.getContainer(block, world, pos, allowBlocking, "atum.container.sarcophagus") : null;
        }
    }
}
