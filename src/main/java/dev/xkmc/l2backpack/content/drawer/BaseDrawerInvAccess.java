package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public interface BaseDrawerInvAccess extends IItemHandlerModifiable {

	BaseDrawerItem drawerItem();

	ItemStack drawerStack();

	ServerPlayer player();

	int getStoredCount();

	boolean isEmpty();

	default ItemStack getStoredItem() {
		return drawerItem().getDrawerContent(drawerStack());
	}

	default ItemStack getStoredStack() {
		if (isEmpty() || getStoredCount() == 0) return ItemStack.EMPTY;
		return getStoredItem().copyWithCount(getStoredCount());
	}

	default void setStoredItem(ItemStack item) {
		drawerItem().setItem(drawerStack(), item, player());
	}

	void setStoredCount(int count);

	default boolean isItemValid(ItemStack stack) {
		return drawerItem().canAccept(drawerStack(), stack);
	}

	@Override
	default int getSlots() {
		return 1;
	}

	@Override
	default int getSlotLimit(int slot) {
		return drawerItem().getStacking(drawerStack(), getStoredItem());
	}

	default int getMax(ItemStack stack) {
		return drawerItem().getStacking(drawerStack(), stack);
	}

	@Override
	default boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return isItemValid(stack);
	}

	@Override
	default void setStackInSlot(int slot, @NotNull ItemStack stack) {
		setStoredItem(stack);
		setStoredCount(stack.getCount());
	}

	@Override
	default @NotNull ItemStack getStackInSlot(int slot) {
		return getStoredStack();
	}

	@Override
	default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return stack;
		if (!isItemValid(stack)) return stack;
		boolean empty = isEmpty();
		int current = empty ? 0 : getStoredCount();
		int input = stack.getCount();
		int allow = Math.min(getMax(stack) - current, input);
		if (!simulate) {
			if (empty)
				setStoredItem(stack);
			setStoredCount(current + allow);
		}
		if (input == allow) {
			return ItemStack.EMPTY;
		}
		return stack.copyWithCount(input - allow);
	}

	@Override
	default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return drawerItem().takeItem(drawerStack(), amount, player(), simulate);
	}

	default boolean mayStack(BaseDrawerInvAccess inv, int slot, ItemStack stack, PickupConfig config) {
		return (config.pickup() == PickupMode.ALL || !isEmpty()) && isItemValid(stack);
	}

}
