package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SerialClass
public class DrawerHandler implements IDrawerHandler {

	@SerialField
	public ItemStack item = ItemStack.EMPTY;

	@SerialField
	public int count = 0, stacking = 1;

	@SerialField
	public PickupConfig config = PickupConfig.DEF;

	private final DrawerBlockEntity parent;

	public DrawerHandler(DrawerBlockEntity parent) {
		this.parent = parent;
	}

	@Override
	public int getSlots() {
		return 2;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		if (slot != 0) {
			return ItemStack.EMPTY;
		}
		return item.copyWithCount(count);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int max = stacking * stack.getMaxStackSize();
		if (count >= max) {
			return stack;
		}
		if (item.isEmpty()) {
			int toInsert = Math.min(max, stack.getCount());
			if (!simulate) {
				item = stack.copyWithCount(1);
				count = toInsert;
				parent.sync();
			}
			if (toInsert == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			ItemStack ans = stack.copy();
			ans.setCount(stack.getCount() - toInsert);
			return ans;
		}
		if (!ItemStack.isSameItemSameComponents(item, stack)) {
			return stack;
		}
		int toInsert = Math.min(max - count, stack.getCount());
		if (!simulate) {
			item = stack.copyWithCount(1);
			count += toInsert;
			parent.sync();
		}
		if (toInsert == stack.getCount()) {
			return ItemStack.EMPTY;
		}
		ItemStack ans = stack.copy();
		ans.setCount(stack.getCount() - toInsert);
		return ans;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (item.isEmpty() || count == 0) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, count);
		ItemStack ans = item.copyWithCount(toExtract);
		if (!simulate) {
			count -= toExtract;
			if (count == 0) {
				item = ItemStack.EMPTY;
			}
			parent.sync();
		}
		return ans;
	}

	@Override
	public int getSlotLimit(int slot) {
		return stacking * item.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return item.isEmpty() || ItemStack.isSameItemSameComponents(stack, item);
	}

}
