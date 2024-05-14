package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.common.DrawerQuickInsert;
import dev.xkmc.l2backpack.content.insert.OverlayInsertItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DrawerInteractToServer extends SerialPacketBase {

	public enum Type {
		INSERT, TAKE, QUICK_MOVE
	}

	public enum Callback {
		REGULAR, SUPPRESS, SCRAMBLE
	}

	@SerialClass.SerialField
	public Type type;

	@SerialClass.SerialField
	public int wid, slot, limit;

	@SerialClass.SerialField
	public ItemStack stack;

	@SerialClass.SerialField
	public Callback suppress;


	@Deprecated
	public DrawerInteractToServer() {

	}

	public DrawerInteractToServer(Type type, int wid, int slot, ItemStack carried, Callback suppress, int limit) {
		this.type = type;
		this.wid = wid;
		this.slot = slot;
		this.stack = carried;
		this.suppress = suppress;
		this.limit = limit;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != wid) return;
		if (wid != 0 && !menu.getSlot(slot).allowModification(player)) return;
		ItemStack storage = wid == 0 ? player.getInventory().getItem(slot) : menu.getSlot(slot).getItem();
		if (!(storage.getItem() instanceof OverlayInsertItem drawerItem)) return;
		drawerItem.serverTrigger(storage, player);
		ItemStack carried = menu.getCarried();
		if (player.isCreative() && wid == 0) {
			carried = stack;
		}
		if (type == Type.TAKE) {
			ItemStack stack = drawerItem.takeItem(storage, player);
			if (player.isCreative() && wid == 0) {
				carried = stack;
			} else {
				menu.setCarried(stack);
				if (suppress == Callback.SUPPRESS) menu.setRemoteCarried(stack.copy());
			}
		} else if (type == Type.QUICK_MOVE) {
			ItemStack stack = drawerItem.takeItem(storage, player);
			if (menu instanceof DrawerQuickInsert ins) {
				ins.quickMove(player, menu, stack, slot);
				if (!stack.isEmpty()) {
					drawerItem.attemptInsert(storage, stack, player);
				}
			}
		} else {
			if (limit == 0) {
				drawerItem.attemptInsert(storage, carried, player);
			} else {
				ItemStack split = carried.split(limit);
				drawerItem.attemptInsert(storage, split, player);
				carried.grow(split.getCount());
			}
			if (suppress == Callback.SUPPRESS) menu.setRemoteCarried(menu.getCarried().copy());
		}
		if (wid != 0) {
			menu.getSlot(slot).setChanged();
		}
		if (player.isCreative() && wid == 0) {
			L2Backpack.HANDLER.toClientPlayer(new CreativeSetCarryToClient(carried), player);
		} else if (suppress == Callback.SCRAMBLE) scramble(menu);
	}

	private static void scramble(AbstractContainerMenu menu) {
		ItemStack carried = menu.getCarried();
		if (carried.isEmpty()) {
			menu.setRemoteCarried(new ItemStack(Items.FARMLAND, 65));
		} else {
			menu.setRemoteCarried(ItemStack.EMPTY);
		}
		menu.broadcastChanges();
	}

}
