package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DrawerInvWrapper extends InvPickupCap<BaseDrawerInvAccess> {

	private final ItemStack stack;
	private final Function<PickupTrace, BaseDrawerInvAccess> access;

	public DrawerInvWrapper(ItemStack stack, Function<PickupTrace, BaseDrawerInvAccess> access) {
		this.stack = stack;
		this.access = access;
	}

	@Override
	public @Nullable BaseDrawerInvAccess getInv(PickupTrace trace) {
		return access.apply(trace);
	}

	@Override
	public boolean mayStack(BaseDrawerInvAccess inv, int slot, ItemStack stack, PickupConfig config) {
		return inv.mayStack(inv, slot, stack, config);
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

}