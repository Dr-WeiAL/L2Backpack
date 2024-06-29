package dev.xkmc.l2backpack.content.capability;

import net.minecraft.world.item.ItemStack;

public interface PickupModeCap {

	static void register() {
	}

	PickupConfig getPickupMode();

	int doPickup(ItemStack stack, PickupTrace trace);

	int getSignature();

}
