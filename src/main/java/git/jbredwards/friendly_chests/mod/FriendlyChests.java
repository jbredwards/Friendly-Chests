package git.jbredwards.friendly_chests.mod;

import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockSarcophagus;
import git.jbredwards.friendly_chests.Tags;
import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import git.jbredwards.friendly_chests.mod.common.datafixer.ChestCapabilityDataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies
= "after:atum@[2.0.20,);"
+ "after:quark@[r1.6-179,);")
public final class FriendlyChests
{
    @Nonnull
    public static final String MOD_ID = Tags.MOD_ID;

    @Mod.EventHandler
    static void preInit(@Nonnull final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(IFriendlyChestCapability.class);
        CapabilityManager.INSTANCE.register(IFriendlyChestCapability.class, IFriendlyChestCapability.Storage.INSTANCE, IFriendlyChestCapability.Impl::new);
        // Remove now unneeded logic for Atum's double chest placement.
        if(Loader.isModLoaded("atum")) MinecraftForge.EVENT_BUS.unregister(BlockSarcophagus.class);
    }

    @Mod.EventHandler
    static void init(@Nonnull final FMLInitializationEvent event) {
        @Nonnull final ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(MOD_ID, ChestCapabilityDataFixer.TILE_INSTANCE.getFixVersion());
        fixes.registerFix(FixTypes.BLOCK_ENTITY, ChestCapabilityDataFixer.TILE_INSTANCE);
        fixes.registerFix(FixTypes.CHUNK, ChestCapabilityDataFixer.CHUNK_INSTANCE);
    }
}
