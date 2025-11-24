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

import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TransformerAtumEntity implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
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
        lockHorizontals(classNode, "onDeath", "func_70645_a", instructions -> {
            instructions.add(new VarInsnNode(ALOAD, 0));
            instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/entity/Entity", DEOBFUSCATED ? "world" : "field_70170_p", "Lnet/minecraft/world/World;"));
            instructions.add(new VarInsnNode(ALOAD, 2));
            instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/World", DEOBFUSCATED ? "getBlockState" : "func_180495_p", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false));
        });
    }
}
