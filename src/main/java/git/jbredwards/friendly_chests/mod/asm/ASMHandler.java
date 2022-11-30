package git.jbredwards.friendly_chests.mod.asm;

import git.jbredwards.fluidlogged_api.api.asm.AbstractClassTransformer;
import git.jbredwards.fluidlogged_api.api.asm.BasicLoadingPlugin;
import git.jbredwards.friendly_chests.mod.asm.plugins.forge.*;
import git.jbredwards.friendly_chests.mod.asm.plugins.modded.*;
import git.jbredwards.friendly_chests.mod.asm.plugins.vanilla.*;

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
            plugins.put("vazkii.quark.decoration.block.BlockCustomChest", new PluginQuarkBlockChest());
            plugins.put("vazkii.quark.decoration.item.ItemChestBlock", new PluginQuarkItemChest());
            plugins.put("vazkii.quark.decoration.tile.TileCustomChest", new PluginQuarkTileChest());
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
