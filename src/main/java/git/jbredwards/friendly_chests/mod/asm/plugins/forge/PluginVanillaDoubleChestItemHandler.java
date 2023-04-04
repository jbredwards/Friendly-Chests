package git.jbredwards.friendly_chests.mod.asm.plugins.forge;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import git.jbredwards.friendly_chests.api.ChestType;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class PluginVanillaDoubleChestItemHandler implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        overrideMethod(classNode, method -> method.name.equals("get"),
            "get", "(Lnet/minecraft/tileentity/TileEntityChest;)Lnet/minecraftforge/items/VanillaDoubleChestItemHandler;",
                generator -> generator.visitVarInsn(ALOAD, 0));

        return false;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static VanillaDoubleChestItemHandler get(@Nonnull TileEntityChest chest) {
            if(!chest.hasWorld() || !chest.getWorld().isBlockLoaded(chest.getPos()))
                return null; // Still loading

            if(chest.getBlockType() instanceof BlockChest) { //fix thaumcraft conflict
                final IBlockState state = chest.getWorld().getBlockState(chest.getPos());
                if(state.getValue(ChestType.TYPE) != ChestType.SINGLE) {
                    final EnumFacing attachedSide = ChestType.getDirectionToAttached(state);
                    final TileEntity other = chest.getWorld().getTileEntity(chest.getPos().offset(attachedSide));
                    if(other instanceof TileEntityChest)
                        return new VanillaDoubleChestItemHandler(
                                chest, (TileEntityChest)other,
                                attachedSide != EnumFacing.WEST && attachedSide != EnumFacing.NORTH);
                }
            }

            return VanillaDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE;
        }
    }
}
