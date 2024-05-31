package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;

public class BlockPickupCap extends InvPickupCap<WorldChestInvWrapper> {

	private final WorldChestBlockEntity be;
	private final StorageContainer storage;
	private final PickupConfig config;

	public BlockPickupCap(WorldChestBlockEntity be, StorageContainer storage, PickupConfig config) {
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
		var opt = be.owner_id;
		return opt == null ? 0 : opt.hashCode() ^ color ^ 0x55AA;
	}

	@Override
	public WorldChestInvWrapper getInv(PickupTrace trace) {
		return new WorldChestInvWrapper(storage.container, storage.id);
	}

}
