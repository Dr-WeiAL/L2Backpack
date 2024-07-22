package dev.xkmc.l2backpack.content.insert;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LBConfig;
import dev.xkmc.l2backpack.network.ClickInteractToServer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface InsertOnlyItem extends CapInsertItem {

	@Override
	default boolean clientInsert(ItemStack storage, ItemStack carried, int cid, Slot slot, boolean perform, int button, ClickInteractToServer.Callback suppress, int limit) {
		if (!LBConfig.CLIENT.allowBackpackInsert(button)) return false;
		return CapInsertItem.super.clientInsert(storage, carried, cid, slot, perform, button, suppress, limit);
	}

	@Override
	default void sendInsertPacket(int cid, ItemStack carried, Slot slot, ClickInteractToServer.Callback suppress, int limit) {
		int index = cid == 0 ? slot.getSlotIndex() : slot.index;
		var method = Screen.hasAltDown() ? ClickInteractToServer.Type.PICKUP : ClickInteractToServer.Type.INSERT;
		L2Backpack.HANDLER.toServer(new ClickInteractToServer(method, cid, index, carried, suppress, limit));
	}

	default boolean mayClientTake() {
		return false;
	}

	@Override
	default ItemStack takeItem(ItemStack storage, ServerPlayer player) {
		return ItemStack.EMPTY;
	}

}
