/*
 * Copyright (C) <2025 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.friendly_chests.mod;

import com.teammetallurgy.atum.blocks.stone.limestone.chest.BlockSarcophagus;
import git.jbredwards.friendly_chests.Tags;
import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import git.jbredwards.friendly_chests.mod.common.datafixer.ChestCapabilityDataFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies =
"after:atum@[2.0.20,);after:carryon@[1.12.3,);after:quark@[r1.6-179,);")
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

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    static void initClient(@Nonnull final FMLInitializationEvent event) {
        Optional.ofNullable(Loader.instance().getIndexedModList().get(MOD_ID)).ifPresent(mod -> {
            // Remove "disable" button in mod gui.
            ReflectionHelper.setPrivateValue(FMLModContainer.class, (FMLModContainer)mod, ModContainer.Disableable.NEVER, "disableability");
            // Allow this mod's description and credits to be translated.
            @Nullable final String[] creditsKey = new String[1], descKey = new String[1];
            ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener)(manager, condition) -> {
                if(condition.test(VanillaResourceType.LANGUAGES) && mod.getMetadata() != null) {
                    mod.getMetadata().credits = I18n.format(creditsKey[0] == null ? creditsKey[0] = mod.getMetadata().credits : creditsKey[0]).replace("\\n", "\n");
                    mod.getMetadata().description = I18n.format(descKey[0] == null ? descKey[0] = mod.getMetadata().description : descKey[0]);
                }
            });
        });
    }
}
