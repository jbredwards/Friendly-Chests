package git.jbredwards.friendly_chests.mod;

import git.jbredwards.friendly_chests.Tags;
import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import git.jbredwards.friendly_chests.mod.common.datafixer.ChestCapabilityDataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public final class FriendlyChests
{
    @Nonnull
    public static final String MOD_ID = Tags.MOD_ID;

    @Mod.EventHandler
    static void preInit(@Nonnull FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(IFriendlyChestCapability.class);
        CapabilityManager.INSTANCE.register(
                IFriendlyChestCapability.class,
                IFriendlyChestCapability.Storage.INSTANCE,
                IFriendlyChestCapability.Impl::new);
    }

    @Mod.EventHandler
    static void init(@Nonnull FMLInitializationEvent event) {
        FMLCommonHandler.instance().getDataFixer()
                .init(MOD_ID, ChestCapabilityDataFixer.INSTANCE.getFixVersion())
                .registerFix(FixTypes.BLOCK_ENTITY, ChestCapabilityDataFixer.INSTANCE);
    }
}
