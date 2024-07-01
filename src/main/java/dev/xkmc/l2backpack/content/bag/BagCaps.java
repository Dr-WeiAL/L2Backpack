package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.world.item.ItemStack;

public class BagCaps extends InvPickupCap<FastBagItemHandler> {

	private final AbstractBag bag;
	private final ItemStack stack;
	private final BagItemHandler itemHandler;

	public BagCaps(ItemStack stack) {
		this.bag = (AbstractBag) stack.getItem();
		this.stack = stack;
		itemHandler = new BagItemHandler(bag, stack);
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return bag.isValidContent(stack);
	}

	@Override
	public FastBagItemHandler getInv(PickupTrace trace) {
		return itemHandler.toFast();
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

}
