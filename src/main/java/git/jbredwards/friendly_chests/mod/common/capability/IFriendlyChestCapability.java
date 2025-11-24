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

package git.jbredwards.friendly_chests.mod.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * chunks loaded with this mod installed have this capability set to true.
 * this is used to check which chunks to fix, and which chunks to ignore
 * @author jbred
 *
 */
public interface IFriendlyChestCapability
{
    @SuppressWarnings("ConstantConditions")
    @CapabilityInject(IFriendlyChestCapability.class)
    @Nonnull Capability<IFriendlyChestCapability> CAPABILITY = null;
    @Nonnull ResourceLocation CAPABILITY_ID = new ResourceLocation("friendly_chests", "fixed");

    boolean isFixed();
    void setFixed(boolean fixedIn);

    @Nullable
    static IFriendlyChestCapability get(@Nullable TileEntity tile) {
        return tile != null && tile.hasCapability(CAPABILITY, null) ? tile.getCapability(CAPABILITY, null) : null;
    }

    @Nullable
    static IFriendlyChestCapability get(@Nullable Chunk chunk) {
        return chunk != null && chunk.hasCapability(CAPABILITY, null) ? chunk.getCapability(CAPABILITY, null) : null;
    }

    class Impl implements IFriendlyChestCapability
    {
        boolean fixed = true;

        @Override
        public boolean isFixed() { return fixed; }

        @Override
        public void setFixed(boolean fixedIn) { fixed = fixedIn; }
    }

    enum Storage implements Capability.IStorage<IFriendlyChestCapability>
    {
        INSTANCE;

        @Nonnull
        @Override
        public NBTBase writeNBT(@Nonnull Capability<IFriendlyChestCapability> capability, @Nonnull IFriendlyChestCapability instance, @Nullable EnumFacing side) {
            return new NBTTagInt(instance.isFixed() ? 1 : 0);
        }

        @Override
        public void readNBT(@Nonnull Capability<IFriendlyChestCapability> capability, @Nonnull IFriendlyChestCapability instance, @Nullable EnumFacing side, @Nullable NBTBase nbt) {
            instance.setFixed(nbt instanceof NBTPrimitive && ((NBTPrimitive)nbt).getInt() != 0);
        }
    }

    @SubscribeEvent
    static void attachChunk(@Nonnull AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(CAPABILITY_ID, new CapabilityProvider<>(CAPABILITY));
    }

    @SubscribeEvent
    static void attachTile(@Nonnull AttachCapabilitiesEvent<TileEntity> event) {
        if(event.getObject() instanceof TileEntityChest) event.addCapability(CAPABILITY_ID, new CapabilityProvider<>(CAPABILITY));
    }
}
