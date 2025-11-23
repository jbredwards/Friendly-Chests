package git.jbredwards.friendly_chests.mod.asm;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import git.jbredwards.friendly_chests.mod.asm.transformers.modded.*;
import git.jbredwards.friendly_chests.mod.asm.transformers.vanilla.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@IFMLLoadingPlugin.Name("Friendly Chests Plugin")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
public final class ASMHandler implements IFMLLoadingPlugin
{
    public ASMHandler() throws ClassNotFoundException {
        // Preload nested MethodVisitor class, to prevent a certain possible JVM crash.
        Class.forName("git.jbredwards.friendly_chests.mod.asm.transformers.IASMClassTransformer$1");
    }

    @Nonnull
    @Override
    public String[] getASMTransformerClass() { return new String[] {"git.jbredwards.friendly_chests.mod.asm.ASMHandler$Transformer"}; }
    public static final class Transformer implements IClassTransformer
    {
        @Nonnull
        public final Multimap<String, IClassTransformer> plugins = MultimapBuilder.hashKeys().arrayListValues().build();
        public Transformer() {
            //modded
            plugins.put("com.teammetallurgy.atum.blocks.base.BlockChestBase", new TransformerAtumBlockBase());
            plugins.put("com.teammetallurgy.atum.blocks.base.tileentity.TileEntityChestBase", new TransformerQuarkTile());
            plugins.put("com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockSarcophagus", new TransformerAtumBlockSarcophagus());
            plugins.put("com.teammetallurgy.atum.entity.undead.EntityPharaoh", new TransformerAtumEntity());
            plugins.put("com.teammetallurgy.atum.blocks.base.ItemDoubleChest", new TransformerAtumItem());
            plugins.put("noobanidus.mods.lootr.block.LootrChestBlock", new TransformerLootrBlock());
            plugins.put("vazkii.quark.decoration.block.BlockCustomChest", new TransformerQuarkBlock());
            plugins.put("vazkii.quark.decoration.item.ItemChestBlock", new TransformerQuarkItem());
            plugins.put("vazkii.quark.decoration.tile.TileCustomChest", new TransformerQuarkTile());
            //vanilla
            plugins.put("net.minecraft.block.BlockChest", new TransformerBlockChest());
            plugins.put("net.minecraft.tileentity.TileEntityChest", new TransformerTileEntityChest());
            plugins.put("net.minecraftforge.items.VanillaDoubleChestItemHandler", new TransformerVanillaDoubleChestItemHandler());
        }

        @Nullable
        @Override
        public byte[] transform(@Nullable final String name, @Nullable final String transformedName, @Nullable final byte[] basicClass) {
            return basicClass != null ? plugins.get(transformedName).stream().reduce(basicClass, (bc, ct) -> ct.transform(name, transformedName, bc), (b1, b2) -> b2) : null;
        }
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }

    @Nullable
    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(@Nonnull final Map<String, Object> data) {}
}
