package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.backpack.BackpackMenu;
import dev.xkmc.l2backpack.content.click.DrawerQuickInsert;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.init.registrate.BackpackMenus;
import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class WorldChestContainer extends BaseContainerMenu<WorldChestContainer> implements DrawerQuickInsert {

	public static WorldChestContainer fromNetwork(MenuType<WorldChestContainer> type, int windowId, Inventory inv) {
		//TODO
		return new WorldChestContainer(windowId, inv, new SimpleContainer(27), null, null);
	}

	protected final Player player;

	@Nullable
	protected final StorageContainer storage;

	@Nullable
	private final WorldChestBlockEntity activeChest;

	public WorldChestContainer(int windowId, Inventory inventory, SimpleContainer cont,
							   @Nullable StorageContainer storage,
							   @Nullable WorldChestBlockEntity entity) {
		super(BackpackMenus.MT_WORLD_CHEST.get(), windowId, inventory, BackpackMenu.MANAGERS[2], menu -> cont, false);
		this.player = inventory.player;
		this.addSlot("grid", stack -> true);
		this.storage = storage;
		this.activeChest = entity;
	}

	@ServerOnly
	public int getColor() {
		assert storage != null;
		return storage.color;
	}

	@ServerOnly
	public UUID getOwner() {
		assert storage != null;
		return storage.id;
	}

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		if (activeChest != null) {
			if (!this.activeChest.stillValid(player)) {
				return false;
			}
		}
		return storage == null || storage.isValid();
	}

	public ItemStack quickMoveStack(Player pl, int id) {
		ItemStack stack = this.slots.get(id).getItem();
		if (quickMove(pl, this, stack, id)) {
			this.slots.get(id).setChanged();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean quickMove(Player pl, AbstractContainerMenu menu, ItemStack stack, int id) {
		int n = this.container.getContainerSize();
		boolean moved;
		if (id >= 36) {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 0, 36, true);
		} else {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 36, 36 + n, false);
		}
		return moved;
	}

}
