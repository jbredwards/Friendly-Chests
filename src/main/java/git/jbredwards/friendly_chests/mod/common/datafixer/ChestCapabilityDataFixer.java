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

package git.jbredwards.friendly_chests.mod.common.datafixer;

import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ensure each previously saved tile entity starts with this set to false
 * @author jbred
 *
 */
public enum ChestCapabilityDataFixer implements IFixableData
{
    CHUNK_INSTANCE {
        @Nonnull
        @Override
        public NBTTagCompound fixTagCompound(@Nonnull final NBTTagCompound compound) {
            @Nonnull final NBTTagCompound nbt = compound.getCompoundTag("Level");
            @Nonnull final NBTTagCompound capabilities = nbt.getCompoundTag("ForgeCaps");

            if(!nbt.hasKey("ForgeCaps")) nbt.setTag("ForgeCaps", capabilities);
            if(!capabilities.hasKey(IFriendlyChestCapability.CAPABILITY_ID.toString()))
                capabilities.setInteger(IFriendlyChestCapability.CAPABILITY_ID.toString(), 0);

            return compound;
        }
    },
    TILE_INSTANCE {
        @Nonnull
        @Override
        public NBTTagCompound fixTagCompound(@Nonnull final NBTTagCompound compound) {
            @Nullable final Class<?> clazz = TileEntity.REGISTRY.getObject(new ResourceLocation(compound.getString("id")));
            if(clazz != null && TileEntityChest.class.isAssignableFrom(clazz)) {
                @Nonnull final NBTTagCompound capabilities = compound.getCompoundTag("ForgeCaps");
                if(!compound.hasKey("ForgeCaps")) compound.setTag("ForgeCaps", capabilities);
                if(!capabilities.hasKey(IFriendlyChestCapability.CAPABILITY_ID.toString()))
                    capabilities.setInteger(IFriendlyChestCapability.CAPABILITY_ID.toString(), 0);
            }

            return compound;
        }
    };

    @Override
    public int getFixVersion() { return 101; }
}
