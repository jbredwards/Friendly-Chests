package git.jbredwards.friendly_chests.mod.asm.transformers.vanilla;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerVanillaDoubleChestItemHandler implements IASMClassTransformer
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull final String name, @Nonnull final String transformedName, @Nonnull final byte[] basicClass) {
        /*
         * New code:
         * // Get inventory from state instead of all neighbors.
         * @ASMOverwrite
         * public static VanillaDoubleChestItemHandler get(TileEntityChest chest)
         * {
         *     return Hooks.get(chest);
         * }
         */
        return transformClassNode(basicClass, classNode -> overwriteMethod(classNode, "get", "get", "()Lnet/minecraftforge/items/VanillaDoubleChestItemHandler;", adapter -> adapter.loadArg(0)));
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static VanillaDoubleChestItemHandler get(@Nonnull final TileEntityChest chest) {
            if(!chest.hasWorld() || !chest.getWorld().isBlockLoaded(chest.getPos())) return null; // Still loading.
            else if(chest.getBlockType() instanceof BlockChest) { // Fix thaumcraft conflict.
                @Nonnull final IBlockState state = chest.getWorld().getBlockState(chest.getPos());
                @Nonnull final ChestType type = ChestType.get(state);

                if(type.hasSideAttached()) {
                    @Nonnull final EnumFacing attachedSide = type.getSideAttached(state);
                    @Nullable final TileEntity other = chest.getWorld().getTileEntity(chest.getPos().offset(attachedSide));

                    if(other instanceof TileEntityChest) return new VanillaDoubleChestItemHandler(chest, (TileEntityChest)other, attachedSide.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE);
                }
            }

            return VanillaDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE;
        }
    }
}
