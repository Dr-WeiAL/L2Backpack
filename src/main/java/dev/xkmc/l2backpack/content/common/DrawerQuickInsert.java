package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface DrawerQuickInsert {

	static boolean moveItemStackTo(Player pl, BaseContainerMenu<?> menu, ItemStack stack, int start, int end, boolean reverse) {
		return moveItemStackTo(pl, menu, stack, start, end, reverse, false);
	}

	static boolean moveItemStackTo(Player pl, BaseContainerMenu<?> menu, ItemStack stack, int start, int end, boolean reverse, boolean split) {
		boolean flag = false;
		int i = start;
		if (reverse) i = end - 1;

		while (!stack.isEmpty()) {
			if (reverse) {
				if (i < start) break;
			} else if (i >= end) break;

			Slot slot = menu.slots.get(i);
			if (tryMerge(pl, stack, slot.getItem(), slot)) {
				flag = true;
			}
			if (reverse) --i;
			else ++i;
		}

		if (!stack.isEmpty()) {
			if (reverse) i = end - 1;
			else i = start;


			while (true) {
				if (reverse) {
					if (i < start) {
						break;
					}
				} else if (i >= end) {
					break;
				}

				Slot slot = menu.slots.get(i);
				if (tryTake(pl, stack, slot.getItem(), slot, split)) {
					flag = true;
					break;
				}

				if (reverse) {
					--i;
				} else {
					++i;
				}
			}
		}
		return flag;
	}

	private static boolean tryMerge(Player pl, ItemStack src, ItemStack dst, Slot slot) {
		if (dst.isEmpty()) return false;
		if (pl instanceof ServerPlayer sp && src.getTag() == null) {
			if (dst.getItem() instanceof BaseDrawerItem item) {
				if (item.canSetNewItem(dst)) return false;
				int count = src.getCount();
				item.attemptInsert(dst, src, sp);
				return count != src.getCount();
			}
		}
		if (src.isStackable() && ItemStack.isSameItemSameTags(src, dst)) {
			int j = dst.getCount() + src.getCount();
			int maxSize = Math.min(slot.getMaxStackSize(), src.getMaxStackSize());
			if (j <= maxSize) {
				src.setCount(0);
				dst.setCount(j);
				slot.setChanged();
				return true;
			} else if (dst.getCount() < maxSize) {
				src.shrink(maxSize - dst.getCount());
				dst.setCount(maxSize);
				slot.setChanged();
				return true;
			}
		}
		return false;
	}

	private static boolean tryTake(Player pl, ItemStack src, ItemStack dst, Slot slot, boolean split) {
		if (!dst.isEmpty()) return false;
		if (split && pl instanceof ServerPlayer sp && src.getItem() instanceof BaseDrawerItem item) {
			ItemStack content = item.takeItem(src, slot.getMaxStackSize(), sp, true);
			if (slot.mayPlace(content)) {
				item.takeItem(src, slot.getMaxStackSize(), sp, false);
				slot.setByPlayer(content);
				slot.setChanged();
				return true;
			}
			return false;
		}
		if (slot.mayPlace(src)) {
			if (src.getCount() > slot.getMaxStackSize()) {
				slot.setByPlayer(src.split(slot.getMaxStackSize()));
			} else {
				slot.setByPlayer(src.split(src.getCount()));
			}

			slot.setChanged();
			return true;
		}
		return false;
	}

	boolean quickMove(Player player, AbstractContainerMenu menu, ItemStack stack, int slot);

}
