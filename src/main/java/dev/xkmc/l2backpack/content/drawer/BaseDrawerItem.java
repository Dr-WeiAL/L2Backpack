package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface BaseDrawerItem extends PickupBagItem, OverlayInsertItem {

	int MAX_FACTOR = 8, STACKING = 64;

	boolean canAccept(ItemStack drawer, ItemStack stack);

	ItemStack getDrawerContent(ItemStack drawer);

	static int loadFromInventory(int max, int count, Item item, Player player) {
		int ext = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack inv_stack = player.getInventory().items.get(i);
			if (inv_stack.getItem() == item && !inv_stack.hasTag()) {
				int take = Math.min(max - count, inv_stack.getCount());
				count += take;
				ext += take;
				inv_stack.shrink(take);
				if (count == max) break;
			}
		}
		return ext;
	}

	default int getStacking(ItemStack drawer) {
		return STACKING;
	}

	void insert(ItemStack drawer, ItemStack stack, Player player);

	void setItem(ItemStack drawer, ItemStack item, Player player);

	default ItemStack takeItem(ItemStack drawer, ServerPlayer player) {
		ItemStack stack = takeItem(drawer, Integer.MAX_VALUE, player, false);
		if (!stack.isEmpty()) {
			LBTriggers.DRAWER.get().trigger(player, DrawerInteractToServer.Type.TAKE);
		}
		return stack;
	}

	ItemStack takeItem(ItemStack drawer, int max, Player player, boolean simulate);

	boolean canSetNewItem(ItemStack drawer);

	@Override
	default boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button,
								 DrawerInteractToServer.Callback suppress, int limit) {
		if (carried.isEmpty()) return false;
		if (!canAccept(storage, carried)) return false;
		if (perform) sendInsertPacket(cid, carried, slot, suppress, limit);
		return true;
	}

	default boolean mayClientTake() {
		return true;
	}

	@Override
	default void attemptInsert(ItemStack storage, ItemStack carried, ServerPlayer player) {
		if (carried.isEmpty()) return;
		if (!canAccept(storage, carried)) return;
		if (canSetNewItem(storage)) {
			setItem(storage, carried, player);
		}
		insert(storage, carried, player);
		LBTriggers.DRAWER.get().trigger(player, DrawerInteractToServer.Type.INSERT);
	}

	ResourceLocation backgroundLoc();

}
