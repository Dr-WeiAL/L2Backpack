package dev.xkmc.l2backpack.content.bag;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BagUtils {

	public static void placeItemBackInInventory(Inventory inventory, ItemStack stack) {
		while (true) {
			if (!stack.isEmpty()) {
				int i = inventory.getSlotWithRemainingSpace(stack);
				if (i == -1) {
					i = getFreeSlot(inventory);
				}

				if (i != -1) {
					int j = stack.getMaxStackSize() - inventory.getItem(i).getCount();
					if (inventory.add(i, stack.split(j)) && inventory.player instanceof ServerPlayer) {
						((ServerPlayer) inventory.player).connection.send(new ClientboundContainerSetSlotPacket(-2, 0, i, inventory.getItem(i)));
					}
					continue;
				}

				inventory.player.drop(stack, false);
			}
			return;
		}
	}

	private static int getFreeSlot(Inventory inv) {
		for (int i = 9; i < inv.items.size(); ++i) {
			if (inv.items.get(i).isEmpty()) {
				return i;
			}
		}
		for (int i = 0; i < 9; ++i) {
			if (inv.items.get(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

}
