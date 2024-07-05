package dev.xkmc.l2backpack.content.insert;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.network.ClickInteractToServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface OverlayInsertItem {

	boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button,
						 ClickInteractToServer.Callback suppress, int limit);

	boolean mayClientTake();

	default void serverTrigger(ItemStack storage, ServerPlayer player) {

	}

	ItemStack takeItem(ItemStack storage, ServerPlayer player);

	void attemptInsert(ItemStack storage, ItemStack carried, ServerPlayer player);

	default void sendInsertPacket(int cid, ItemStack carried, Slot slot, ClickInteractToServer.Callback suppress, int limit) {
		int index = cid == 0 ? slot.getSlotIndex() : slot.index;
		L2Backpack.HANDLER.toServer(new ClickInteractToServer(ClickInteractToServer.Type.INSERT,
				cid, index, carried, suppress, limit));
	}


}
