package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.events.ClientEventHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yalter.mousetweaks.MouseButton;

public class MouseTweakCompat {

	public static boolean delegateSlotClick(AbstractContainerScreen<?> menu, Slot slot, MouseButton btn, boolean shift, Click click) {
		ItemStack src = slot.getItem();
		ItemStack carried = menu.getMenu().getCarried();
		if (src.getItem() instanceof DrawerItem drawer) {
			if (btn == MouseButton.LEFT && !shift) {
				if (carried.isEmpty()) {
					ClientEventHandler.clientDrawerTake(menu, slot);
				} else {
					ClientEventHandler.clientDrawerInsert(menu, slot);
				}
				return true;
			}
		}
		return false;
	}

	public static ItemStack wrapSlotGet(ItemStack stack) {
		if (stack.getItem() instanceof DrawerItem drawer) {
			int count = DrawerItem.getCount(stack);
			if (count == 0) {
				return ItemStack.EMPTY;
			}
			Item item = BaseDrawerItem.getItem(stack);
			int cap = BaseDrawerItem.getStacking(stack) * BaseDrawerItem.getStackingFactor(stack);
			int max = item.getMaxStackSize();
			if (max == 1) {
				return new ItemStack(item, 1);
			}
			if (cap - count < max / 2) {
				return new ItemStack(item, max + count - cap);
			}
			if (count < max / 2) {
				return new ItemStack(item, count);
			}
			return new ItemStack(item, max / 2);
		}
		return stack;
	}

	public interface Click {

		void click(MouseButton mouseButton);

	}

}
