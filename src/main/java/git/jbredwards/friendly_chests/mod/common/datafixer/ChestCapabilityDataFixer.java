package git.jbredwards.friendly_chests.mod.common.datafixer;

import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;

import javax.annotation.Nonnull;

/**
 * ensure each previously saved tile entity starts with this set to false
 * @author jbred
 *
 */
public enum ChestCapabilityDataFixer implements IFixableData
{
    INSTANCE;

    @Override
    public int getFixVersion() { return 101; }

    @Nonnull
    @Override
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
        final Class<?> clazz = TileEntity.REGISTRY.getObject(new ResourceLocation(compound.getString("id")));
        if(clazz != null && TileEntityChest.class.isAssignableFrom(clazz)) {
            final NBTTagCompound capabilities = compound.getCompoundTag("ForgeCaps");
            if(!compound.hasKey("ForgeCaps")) compound.setTag("ForgeCaps", capabilities);
            if(!capabilities.hasKey(IFriendlyChestCapability.CAPABILITY_ID.toString()))
                capabilities.setInteger(IFriendlyChestCapability.CAPABILITY_ID.toString(), 0);
        }

        return compound;
    }
}
