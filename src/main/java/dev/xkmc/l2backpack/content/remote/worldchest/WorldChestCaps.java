package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.world.item.ItemStack;

public class WorldChestCaps extends InvPickupCap<WorldChestInvWrapper> {

	private final ItemStack stack;

	public WorldChestCaps(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

	@Override
	public int getSignature() {
		if (stack.getItem() instanceof WorldChestItem item) {
			int color = item.color.ordinal();
			var id = LBItems.DC_OWNER_ID.get(stack);
			if (id != null) {
				return id.hashCode() ^ color ^ 0x55AA;
			}
		}
		return 0;
	}

	@Override
	public WorldChestInvWrapper getInv(PickupTrace trace) {
		if (stack.getItem() instanceof WorldChestItem item) {
			var opt = item.getContainer(stack, trace.level);
			if (opt.isPresent()) {
				var storage = opt.get();
				return new WorldChestInvWrapper(storage.container, storage.id);
			}
		}
		return null;
	}

}
