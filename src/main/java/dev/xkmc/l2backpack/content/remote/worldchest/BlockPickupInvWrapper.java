package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockPickupInvWrapper extends WorldChestInvWrapper {

	private final BlockPickupCap cap;
	private final ServerLevel level;

	public BlockPickupInvWrapper(ServerLevel level, WorldChestBlockEntity be, StorageContainer storage, PickupConfig config) {
		super(storage.container, storage.id);
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
