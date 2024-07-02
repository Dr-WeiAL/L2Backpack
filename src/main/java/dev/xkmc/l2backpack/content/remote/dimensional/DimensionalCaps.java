package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.world.item.ItemStack;

public class DimensionalCaps extends InvPickupCap<DimensionalInvWrapper> {

	private final ItemStack stack;

	public DimensionalCaps(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

	@Override
	public int getSignature() {
		if (stack.getItem() instanceof DimensionalItem item) {
			int color = item.color.ordinal();
			var id = LBItems.DC_OWNER_ID.get(stack);
			if (id != null) {
				return id.hashCode() ^ color ^ 0x55AA;
			}
		}
		return 0;
	}

	@Override
	public DimensionalInvWrapper getInv(PickupTrace trace) {
		if (stack.getItem() instanceof DimensionalItem item) {
			var opt = item.getContainer(stack, trace.level);
			if (opt.isPresent()) {
				var storage = opt.get();
				return new DimensionalInvWrapper(storage.container, storage.id);
			}
		}
		return null;
	}

}
