package git.jbredwards.friendly_chests.mod.asm.plugins.modded;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class PluginQuarkTileChest implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        classNode.methods.removeIf(method -> method.name.equals("getCapability"));
        return false;
    }
}
