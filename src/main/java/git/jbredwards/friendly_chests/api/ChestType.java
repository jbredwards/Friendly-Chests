package git.jbredwards.friendly_chests.api;

import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public enum ChestType implements IStringSerializable
{
    SINGLE("single", 0),
    LEFT("left", 2),
    RIGHT("right", 1);

    @Nonnull
    public static final PropertyEnum<ChestType> TYPE = PropertyEnum.create("type", ChestType.class);

    @Nonnull
    final String name;
    final int opposite;

    ChestType(@Nonnull String nameIn, int oppositeIn) {
        name = nameIn;
        opposite = oppositeIn;
    }

    @Nonnull
    @Override
    public String getName() { return name; }

    @Nonnull
    public ChestType getOpposite() { return ChestType.values()[opposite]; }

    @Nonnull
    public static ChestType fromIndex(int index) { return values()[index % values().length]; }

    @Nonnull
    public EnumFacing rotate(@Nonnull EnumFacing facing) {
        return this == LEFT ? facing.rotateY() : facing.rotateYCCW();
    }

    @Nonnull
    public static EnumFacing getDirectionToAttached(@Nonnull IBlockState state) {
        return state.getValue(TYPE).rotate(state.getValue(BlockChest.FACING));
    }
}
