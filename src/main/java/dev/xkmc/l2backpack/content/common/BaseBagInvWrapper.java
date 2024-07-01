package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BaseBagInvWrapper extends InvPickupCap<BaseBagItemHandler> {

	private final ItemStack stack;
	private final BaseBagItemHandler handler;

	public BaseBagInvWrapper(ItemStack stack) {
		this.stack = stack;
		this.handler = new BaseBagItemHandler(stack);
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

	@Override
	public @Nullable BaseBagItemHandler getInv(PickupTrace trace) {
		return handler;
	}

}