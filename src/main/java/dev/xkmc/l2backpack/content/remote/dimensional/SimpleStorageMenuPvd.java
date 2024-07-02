package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record SimpleStorageMenuPvd(Component comp, StorageContainer cont) implements MenuProvider {

	@Override
	public Component getDisplayName() {
		return comp;
	}

	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inv, Player player) {
		return new DimensionalContainer(wid, inv, cont.get(), cont, null);
	}

}
