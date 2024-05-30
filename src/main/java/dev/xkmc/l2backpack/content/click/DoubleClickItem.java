package dev.xkmc.l2backpack.content.click;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface DoubleClickItem {

	int remainingSpace(ItemStack stack);

	boolean canAbsorb(Slot src, ItemStack stack);

	void mergeStack(ItemStack stack, ItemStack taken);

}
