package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.click.DrawerQuickInsert;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2core.util.ServerOnly;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.UUID;

public abstract class BaseBagMenu<T extends BaseBagMenu<T>> extends BaseContainerMenu<T> implements DrawerQuickInsert {

	protected final Player player;
	public final PlayerSlot<?> item_slot;
	protected final UUID uuid;
	protected final IItemHandlerModifiable handler;

	public BaseBagMenu(MenuType<T> type, int windowId, Inventory inventory, SpriteManager manager,
					   PlayerSlot<?> hand, UUID uuid, int row) {
		super(type, windowId, inventory, manager, menu -> new SimpleContainer(0), false);
		this.item_slot = hand;
		this.uuid = uuid;
		this.player = inventory.player;
		ItemStack stack = getStack();
		if (stack.getItem() instanceof BaseBagItem) {
			var inv = stack.getCapability(Capabilities.ItemHandler.ITEM);
			if (player instanceof ServerPlayer sp && inv instanceof BaseBagItemHandler bag) {
				bag.attachEnv(sp, hand);
			}
			this.handler = (IItemHandlerModifiable) inv;
		} else {
			handler = new InvWrapper(new SimpleContainer(row * 9));//TODO
		}
		this.addSlot("grid");
		if (!player.level().isClientSide()) {
			BaseBagItem.checkLootGen(getStack(), player);
		}
	}

	protected void addSlot(String name) {
		this.getLayout().getSlot(name, (x, y) -> new BagSlot(handler, this.added++, x, y), this::addSlot);
	}

	private ItemStack stack_cache = ItemStack.EMPTY;

	@ServerOnly
	@Override
	public boolean stillValid(Player player) {
		ItemStack oldStack = stack_cache;
		ItemStack newStack = getStackRaw();
		if (getStackRaw().isEmpty() || oldStack != newStack) {
			return false;
		}
		return getStack().getCapability(Capabilities.ItemHandler.ITEM) == handler;
	}

	public ItemStack getStack() {
		ItemStack stack = getStackRaw();
		if (stack.isEmpty()) return stack_cache;
		return stack;
	}

	private ItemStack getStackRaw() {
		ItemStack stack = item_slot.getItem(player);
		if (player.level().isClientSide()) return stack;
		var id = LBItems.DC_CONT_ID.get(stack);
		if (id == null || !id.equals(uuid)) return ItemStack.EMPTY;
		stack_cache = stack;
		return stack;
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
		int n = this.handler.getSlots();
		boolean moved;
		if (id >= 36) {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 0, 36, true);
		} else {
			moved = DrawerQuickInsert.moveItemStackTo(pl, this, stack, 36, 36 + n, false);
		}
		return moved;
	}

}
