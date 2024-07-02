package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockPickupInvWrapper extends DimensionalInvWrapper {

	private final BlockPickupCap cap;
	private final ServerLevel level;

	public BlockPickupInvWrapper(ServerLevel level, DimensionalBlockEntity be, StorageContainer storage, PickupConfig config) {
		super(storage.get(), storage.id);
		this.level = level;
		cap = new BlockPickupCap(be, storage, config);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack copy = stack.copy();
		cap.doPickup(copy, new PickupTrace(simulate, level));
		return copy;
	}

}
