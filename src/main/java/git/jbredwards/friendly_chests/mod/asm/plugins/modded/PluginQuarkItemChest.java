package git.jbredwards.friendly_chests.mod.asm.plugins.modded;

import git.jbredwards.fluidlogged_api.api.asm.IASMPlugin;
import git.jbredwards.friendly_chests.api.ChestType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.tree.ClassNode;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.tile.TileCustomChest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class PluginQuarkItemChest implements IASMPlugin
{
    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        overrideMethod(classNode, method -> method.name.equals("placeBlockAt"),
            "placeBlockAt", "(Lnet/minecraft/item/ItemBlock;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/block/state/IBlockState;)Z", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
                generator.visitVarInsn(ALOAD, 4);
                generator.visitVarInsn(ALOAD, 5);
                generator.visitVarInsn(ALOAD, 9);
            }
        );

        return false;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static boolean placeBlockAt(@Nonnull ItemBlock item, @Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, @Nonnull IBlockState newState) {
            final VariedChests.ChestType myType = ((BlockCustomChest)item.getBlock()).getCustomType(stack);
            final boolean isSneaking = player.isSneaking();
            EnumFacing facing = player.getHorizontalFacing().getOpposite();
            ChestType type = ChestType.SINGLE;

            if(side.getAxis().isHorizontal() && isSneaking) {
                final EnumFacing sideToAttach = getDirectionToAttach(myType, item.getBlock(), world, pos, side.getOpposite());
                if(sideToAttach != null && sideToAttach.getAxis() != side.getAxis()) {
                    facing = sideToAttach;
                    type = sideToAttach.rotateYCCW() == side.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
                }
            }

            if(type == ChestType.SINGLE && !isSneaking) {
                final EnumFacing left = getDirectionToAttach(myType, item.getBlock(), world, pos, facing.rotateY());
                if(left != null && facing != left.getOpposite()) type = ChestType.LEFT;
                else {
                    final EnumFacing right = getDirectionToAttach(myType, item.getBlock(), world, pos, facing.rotateYCCW());
                    if(right != null && facing != right.getOpposite()) type = ChestType.RIGHT;
                }
            }

            newState = newState.withProperty(BlockChest.FACING, facing).withProperty(ChestType.TYPE, type);
            if(!world.setBlockState(pos, newState, 11)) return false;
            final IBlockState state = world.getBlockState(pos);

            //change neighbor data
            if(type != ChestType.SINGLE) {
                final EnumFacing chestFacing = state.getValue(BlockChest.FACING);
                final BlockPos offset = pos.offset(type.rotate(chestFacing));
                final IBlockState attachedTo = world.getBlockState(offset);

                //update neighbor chest
                if(attachedTo.getValue(ChestType.TYPE) == ChestType.SINGLE)
                    world.setBlockState(offset, attachedTo
                            .withProperty(BlockChest.FACING, chestFacing)
                            .withProperty(ChestType.TYPE, type.getOpposite()));
            }

            //set tile entity data
            if(state.getBlock() == item.getBlock()) {
                ItemBlock.setTileEntityNBT(world, player, pos, stack);
                final TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TileCustomChest) ((TileCustomChest)tile).chestType = myType;

                item.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
                if(player instanceof EntityPlayerMP)
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }

            return true;
        }

        //helper
        @Nullable
        public static EnumFacing getDirectionToAttach(@Nonnull VariedChests.ChestType myType, @Nonnull Block block, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
            final IBlockState state = world.getBlockState(pos.offset(facing));
            return Block.isEqualTo(block, state.getBlock()) && myType == ((BlockCustomChest)block).getCustomType(world, pos.offset(facing))
                    && state.getValue(ChestType.TYPE) == ChestType.SINGLE ? state.getValue(BlockChest.FACING) : null;
        }
    }
}
