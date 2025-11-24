/*
 * Copyright (C) <2025 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.friendly_chests.mod.asm.transformers.vanilla;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
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
public final class TransformerVanillaDoubleChestItemHandler implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        /*
         * New code:
         * // Get inventory from state instead of all neighbors.
         * @ASMOverwrite
         * public static VanillaDoubleChestItemHandler get(TileEntityChest chest)
         * {
         *     return Hooks.get(chest);
         * }
         */
        overwriteMethod(classNode, "get", "get", "(Lnet/minecraft/tileentity/TileEntityChest;)Lnet/minecraftforge/items/VanillaDoubleChestItemHandler;", adapter -> adapter.loadArg(0));
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
