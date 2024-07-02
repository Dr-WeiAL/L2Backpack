package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;

public class BlockPickupCap extends InvPickupCap<DimensionalInvWrapper> {

	private final DimensionalBlockEntity be;
	private final StorageContainer storage;
	private final PickupConfig config;

	public BlockPickupCap(DimensionalBlockEntity be, StorageContainer storage, PickupConfig config) {
		this.be = be;
		this.storage = storage;
		this.config = config;
	}

	@Override
	public PickupConfig getPickupMode() {
		return config;
	}

	@Override
	public int getSignature() {
		int color = be.color;
		var opt = be.ownerId;
		return opt == null ? 0 : opt.hashCode() ^ color ^ 0x55AA;
	}

	@Override
	public DimensionalInvWrapper getInv(PickupTrace trace) {
		return new DimensionalInvWrapper(storage.get(), storage.id);
	}

}
