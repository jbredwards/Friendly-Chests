package git.jbredwards.friendly_chests.mod;

import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import git.jbredwards.friendly_chests.mod.common.datafixer.ChestCapabilityDataFixer;
import net.minecraft.util.datafix.FixTypes;
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
@Mod(modid = "friendly_chests", name = "Friendly Chests", version = "1.0.0")
public final class FriendlyChests
{
    @Mod.EventHandler
    static void preInit(@Nonnull FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(
                IFriendlyChestCapability.class,
                IFriendlyChestCapability.Storage.INSTANCE,
                IFriendlyChestCapability.Impl::new);
    }

    @Mod.EventHandler
    static void init(@Nonnull FMLInitializationEvent event) {
        FMLCommonHandler.instance().getDataFixer()
                .init("friendly_chests", ChestCapabilityDataFixer.INSTANCE.getFixVersion())
                .registerFix(FixTypes.BLOCK_ENTITY, ChestCapabilityDataFixer.INSTANCE);
    }
}
