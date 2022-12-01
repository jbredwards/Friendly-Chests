package git.jbredwards.friendly_chests.mod.client;

import git.jbredwards.friendly_chests.api.ChestType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber(modid = "friendly_chests", value = Side.CLIENT)
public final class ClientEventHandler
{
    @SideOnly(Side.CLIENT)
    static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers =
            ObfuscationReflectionHelper.getPrivateValue(ModelLoader.class, null, "customStateMappers");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    static void handleBakedModels(@Nonnull ModelRegistryEvent event) {
        for(Block block : ForgeRegistries.BLOCKS) {
            if(block instanceof BlockChest) {
                final @Nullable IStateMapper mapper = customStateMappers.get(block.delegate);
                if(mapper instanceof StateMap) ((StateMap)mapper).ignored.add(ChestType.TYPE);
                else ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(ChestType.TYPE).build());
            }
        }
    }
}
