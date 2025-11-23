package git.jbredwards.friendly_chests.mod.common.datafixer;

import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import git.jbredwards.friendly_chests.mod.common.capability.IFriendlyChestCapability;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * fix old chests on chunk load
 * @author jbred
 *
 */
@Mod.EventBusSubscriber(modid = "friendly_chests")
public final class ChestDataFixer
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    static void applyFix(@Nonnull final ChunkEvent.Load event) {
        @Nonnull final Chunk chunk = event.getChunk();
        @Nullable final IFriendlyChestCapability chunkCap = IFriendlyChestCapability.get(chunk);

        if(chunkCap == null || chunkCap.isFixed()) return;
        @Nonnull final World world = event.getWorld();

        //noinspection unchecked
        for(@Nonnull final Map.Entry<BlockPos, TileEntityChest> entry : chunk.getTileEntityMap().entrySet().stream()
        .filter(entry -> entry.getValue() instanceof TileEntityChest && !entry.getValue().isInvalid()).toArray(Map.Entry[]::new)) {
            @Nonnull final BlockPos pos = entry.getKey();
            @Nullable final IFriendlyChestCapability cap = IFriendlyChestCapability.get(entry.getValue());

            if(cap != null && !cap.isFixed()) {
                @Nonnull IBlockState state = chunk.getBlockState(pos);
                if(state.getBlock() instanceof BlockChest) {
                    //fix facing data
                    @Nonnull EnumFacing facing = EnumFacing.byIndex(state.getBlock().getMetaFromState(state));
                    if(facing.getAxis() == EnumFacing.Axis.Y) facing = EnumFacing.NORTH;
                    state = state.withProperty(BlockChest.FACING, facing);
                    //fix connected data
                    @Nonnull ChestType type = ChestType.SINGLE;
                    if(fixSide(state, world, chunk, pos, facing, ChestType.RIGHT)) type = ChestType.RIGHT;
                    else if(fixSide(state, world, chunk, pos, facing, ChestType.LEFT)) type = ChestType.LEFT;
                    state = state.withProperty(ChestType.TYPE, type);
                    //set fixed state
                    chunk.setBlockState(pos, state);
                }

                cap.setFixed(true);
            }
        }

        chunkCap.setFixed(true);
    }

    static boolean fixSide(@Nonnull IBlockState state, @Nonnull World world, @Nonnull Chunk chunk, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull ChestType type) {
        final BlockPos otherPos = pos.offset(type.getSideAttached(facing));
        final Chunk otherChunk = chunk.isAtLocation(otherPos.getX() >> 4, otherPos.getZ() >> 4) ? chunk :
                world.getChunkProvider().getLoadedChunk(otherPos.getX() >> 4, otherPos.getZ() >> 4);

        //continue if neighboring chunk is loaded
        if(otherChunk != null) {
            IBlockState otherState = otherChunk.getBlockState(otherPos);
            if(IChestMatchable.chestMatches((BlockChest)state.getBlock(), world, state, pos, otherState, otherPos)) {
                final IFriendlyChestCapability cap = IFriendlyChestCapability.get(otherChunk.getTileEntity(otherPos, Chunk.EnumCreateEntityType.CHECK));
                //fix neighbor state if applicable
                if(cap != null && !cap.isFixed()) {
                    otherChunk.setBlockState(otherPos, otherState
                            .withProperty(BlockChest.FACING, facing)
                            .withProperty(ChestType.TYPE, type.getOpposite()));

                    cap.setFixed(true);
                    return true;
                }
            }
        }

        return false;
    }
}
