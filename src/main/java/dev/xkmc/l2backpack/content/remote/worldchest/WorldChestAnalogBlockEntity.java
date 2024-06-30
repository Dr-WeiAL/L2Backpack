package dev.xkmc.l2backpack.content.remote.worldchest;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.xkmc.l2backpack.content.remote.common.AnalogTrigger;
import dev.xkmc.l2modularblock.impl.BlockEntityBlockMethodImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WorldChestAnalogBlockEntity<T extends BlockEntity> extends BlockEntityBlockMethodImpl<T> {

	public WorldChestAnalogBlockEntity(BlockEntityEntry<T> type, Class<T> cls) {
		super(type, cls);
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		BlockEntity e = worldIn.getBlockEntity(pos);
		if (e instanceof WorldChestBlockEntity be) {
			AnalogTrigger.trigger(worldIn, be.ownerId);
		}
		return super.getAnalogOutputSignal(blockState, worldIn, pos);
	}

	@Override
	public InteractionResult onClick(BlockState state, Level level, BlockPos pos, Player pl, InteractionHand hand, BlockHitResult result) {
		return InteractionResult.PASS;
	}

}
