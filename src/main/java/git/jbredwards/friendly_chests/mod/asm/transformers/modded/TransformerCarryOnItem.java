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

package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import net.minecraft.block.state.IBlockState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TransformerCarryOnItem implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        /*
         * Old code:
         * world.setBlockState(pos2, containedstate.withProperty(prop, containedblock instanceof BlockStairs ? facing2 : facing2.getOpposite()));
         *
         * New code:
         * // Don't allow CarryOn to create illegal chest states.
         * world.setBlockState(pos2, Hooks.asSingle(containedstate.withProperty(prop, containedblock instanceof BlockStairs ? facing2 : facing2.getOpposite())));
         */
        for(@Nonnull final MethodNode method : classNode.methods) {
            if(method.name.equals(DEOBFUSCATED ? "onItemUse" : "func_180614_a")) {
                for(@Nonnull final AbstractInsnNode insn : method.instructions.toArray()) {
                    if(insn.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode)insn).name.equals(DEOBFUSCATED ? "withProperty" : "func_177226_a")) {
                        method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, getHookClass(), "asSingle", "(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", false));
                        return;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nonnull
        public static IBlockState asSingle(@Nonnull final IBlockState state) {
            return state.getBlock() instanceof IChestMatchable ? state.withProperty(ChestType.TYPE, ChestType.SINGLE) : state;
        }
    }
}
