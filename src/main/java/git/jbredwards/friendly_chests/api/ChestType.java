package git.jbredwards.friendly_chests.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 *
 * @since 1.0.0
 * @author jbred
 *
 */
public enum ChestType implements IStringSerializable
{
    SINGLE,
    LEFT,
    RIGHT,
    /**
     * The default ChestType for Vanilla 1.12.2. If a chest is set in the world with this value, it subsequently runs
     * {@link BlockChest#checkForSurroundingChests checkForSurroundingChests} to get its ChestType. An example of chests
     * that do this are old dungeon chests.
     * <p>
     * Note: Friendly Chests uses ASM to modify the placement of Vanilla chests and many modded chests for finer control
     * over its ChestType, without using {@link ChestType#UNDEFINED}.
     * </p>
     *
     * @since 2.0.0
     */
    UNDEFINED;

    /**
     * Friendly Chests uses ASM to apply this to {@link BlockChest} at runtime.
     * @since 1.0.0
     */
    @Nonnull
    public static final PropertyEnum<ChestType> TYPE = PropertyEnum.create("type",
            ChestType.class, ImmutableList.of(UNDEFINED, SINGLE, LEFT, RIGHT));

    /**
     * Inherited from {@link IStringSerializable}, unused by Friendly Chests.
     * @since 1.0.0
     */
    @Nonnull
    @Override
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * @return This ChestType's index value.
     * @since 2.0.0
     */
    public int index() {
        return ordinal();
    }

    /**
     * @return A ChestType by its index value.
     * @since 1.0.1
     */
    @Nonnull
    public static ChestType fromIndex(final int index) {
        return fromOrdinal(index);
    }

    /**
     * @return A ChestType by its ordinal value.
     * @since 1.0.0
     */
    @Nonnull
    public static ChestType fromOrdinal(final int ordinal) {
        return values()[ordinal % values().length];
    }

    /**
     * @return The provided chest's ChestType.
     * @since 2.0.0
     */
    @Nonnull
    public static ChestType get(@Nonnull final IBlockState state) {
        return state.getValue(TYPE);
    }

    /**
     * @return Whether this ChestType has an opposite value.
     * @since 2.0.0
     */
    public boolean hasOpposite() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * @return The opposite value of this ChestType.
     * @throws UnsupportedOperationException If this has no opposite value.
     * @since 1.0.0
     */
    @Nonnull
    public ChestType getOpposite() {
        switch(this) {
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
        }

        throw new UnsupportedOperationException("Chest type does not have an \"opposite\" component: " + getName());
    }

    /**
     * @return Whether this ChestType is connected to another chest.
     * @since 2.0.0
     */
    public boolean hasSideAttached() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * @return The side of this block that's connected to a neighboring chest.
     * @throws UnsupportedOperationException If the chest type is single or undefined.
     * @since 2.0.0
     */
    @Nonnull
    public EnumFacing getSideAttached(@Nonnull final EnumFacing chestFacing) {
        switch(this) {
            case LEFT:  return chestFacing.rotateY();
            case RIGHT: return chestFacing.rotateYCCW();
        }

        throw new UnsupportedOperationException("Chest type does not have an attached side: " + getName());
    }

    /**
     * @return The side of this block that's connected to a neighboring chest.
     * @throws UnsupportedOperationException If the chest type is single or undefined.
     * @since 2.0.0
     */
    @Nonnull
    public EnumFacing getSideAttached(@Nonnull final IBlockState state) {
        return getSideAttached(state.getValue(BlockChest.FACING));
    }

    /**
     * @return The side of this block that's connected to a neighboring chest.
     * @throws UnsupportedOperationException If the chest type is single or undefined.
     * @since 1.0.0
     */
    @Nonnull
    public static EnumFacing getDirectionToAttached(@Nonnull final IBlockState state) {
        return get(state).getSideAttached(state);
    }

    /**
     * @return The side of this block that's connected to a neighboring chest, as an array.
     * @since 2.0.0
     */
    @Nonnull
    public static EnumFacing[] getDirectionsToAttached(@Nonnull final IBlockState state) {
        @Nonnull final ChestType type = get(state);
        return type.hasSideAttached() ? new EnumFacing[] {type.getSideAttached(state)} : new EnumFacing[0];
    }

    /**
     * Use {@link ChestType#getSideAttached} instead.
     * @return The side of this block that's connected to a neighboring chest.
     * @throws UnsupportedOperationException If the chest type is single or undefined.
     * @since 1.0.1
     */
    @Deprecated
    @Nonnull
    public EnumFacing rotate(@Nonnull final EnumFacing chestFacing) {
        return getSideAttached(chestFacing);
    }
}
