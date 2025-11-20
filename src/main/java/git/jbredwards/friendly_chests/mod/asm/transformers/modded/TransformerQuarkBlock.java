package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.Block;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import org.objectweb.asm.tree.ClassNode;
import vazkii.quark.decoration.tile.TileCustomChest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class TransformerQuarkBlock implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        classNode.methods.removeIf(method
                -> method.name.equals(DEOBFUSCATED ? "checkForSurroundingChests" : "func_176455_e")
                || method.name.equals(DEOBFUSCATED ? "getBoundingBox" : "func_185496_a")
                || method.name.equals(DEOBFUSCATED ? "getContainer" : "func_189418_a")
                || method.name.equals(DEOBFUSCATED ? "onBlockPlacedBy" : "func_180633_a"));
        /*
         * New code:
         * // Quark chests have to compare tile entity data.
         * @ASMOverwrite
         * public boolean chestMatches(IBlockSource chest, IBlockSource other)
         * {
         *     return Hooks.chestMatches(chest, other);
         * }
         */
        overwriteMethod(classNode, "chestMatches", "chestMatches", "(Lnet/minecraft/dispenser/IBlockSource;Lnet/minecraft/dispenser/IBlockSource;)Z", adapter -> {
            adapter.loadArg(0);
            adapter.loadArg(1);
        });
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean chestMatches(@Nonnull final IBlockSource chest, @Nonnull final IBlockSource other) {
            if(!Block.isEqualTo(chest.getBlockState().getBlock(), other.getBlockState().getBlock())) return false;

            @Nullable final TileEntity chestTile = chest.getBlockTileEntity();
            @Nullable final TileEntity otherTile = other.getBlockTileEntity();

            return chestTile instanceof TileCustomChest && otherTile instanceof TileCustomChest && ((TileCustomChest)chestTile).chestType == ((TileCustomChest)otherTile).chestType;
        }
    }
}
