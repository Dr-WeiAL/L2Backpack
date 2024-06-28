package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BagCaps extends InvPickupCap<FastBagItemHandler> implements ICapabilityProvider {

	private final AbstractBag bag;
	private final ItemStack stack;
	private final LazyOptional<BagCaps> holder = LazyOptional.of(() -> this);
	private final BagItemHandler itemHandler;
	private final LazyOptional<BagItemHandler> handler;

	public BagCaps(AbstractBag bag, ItemStack stack) {
		this.bag = bag;
		this.stack = stack;
		itemHandler = new BagItemHandler(bag, stack);
		handler = LazyOptional.of(() -> itemHandler);
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
		return PickupConfig.getPickupMode(stack);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == PickupModeCap.TOKEN) {
			return holder.cast();
		}
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return handler.cast();
		}
		return LazyOptional.empty();
	}

}
