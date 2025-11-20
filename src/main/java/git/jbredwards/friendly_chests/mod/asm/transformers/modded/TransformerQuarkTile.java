package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TransformerQuarkTile implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        classNode.methods.removeIf(method -> method.name.equals(DEOBFUSCATED ? "getAdjacentChest" : "func_174911_a") || method.name.equals("getCapability"));
    }
}
