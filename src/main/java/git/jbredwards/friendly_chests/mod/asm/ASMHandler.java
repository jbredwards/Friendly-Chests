package git.jbredwards.friendly_chests.mod.asm;

import git.jbredwards.fluidlogged_api.api.asm.AbstractClassTransformer;
import git.jbredwards.fluidlogged_api.api.asm.BasicLoadingPlugin;
import git.jbredwards.friendly_chests.mod.asm.plugins.forge.PluginVanillaDoubleChestItemHandler;
import git.jbredwards.friendly_chests.mod.asm.plugins.vanilla.PluginBlockChest;
import git.jbredwards.friendly_chests.mod.asm.plugins.vanilla.PluginTileEntityChest;
import git.jbredwards.friendly_chests.mod.asm.plugins.vanilla.PluginTileEntityChestRenderer;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@BasicLoadingPlugin.Name("Friendly Chests Plugin")
@BasicLoadingPlugin.MCVersion("1.12.2")
@BasicLoadingPlugin.SortingIndex(1001)
public final class ASMHandler implements BasicLoadingPlugin
{
    @SuppressWarnings("unused")
    public static final class Transformer extends AbstractClassTransformer
    {
        public Transformer() {
            //modded
            //plugins.put("", )
            //vanilla
            plugins.put("net.minecraft.block.BlockChest", new PluginBlockChest());
            plugins.put("net.minecraft.tileentity.TileEntityChest", new PluginTileEntityChest());
            //forge
            plugins.put("net.minecraftforge.items.VanillaDoubleChestItemHandler", new PluginVanillaDoubleChestItemHandler());
        }

        @Nonnull
        @Override
        public String getPluginName() { return "Friendly Chests Plugin"; }
    }
}
