package git.jbredwards.friendly_chests.mod.asm.transformers.modded;

import git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TransformerAtumItem implements IASMClassTransformer
{
    @Override
    public void transform(@Nonnull final ClassNode classNode) {
        classNode.methods.removeIf(method -> method.name.equals("placeBlockAt"));
    }
}
