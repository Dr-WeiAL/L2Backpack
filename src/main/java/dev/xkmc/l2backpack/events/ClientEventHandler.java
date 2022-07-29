package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.arrowbag.ArrowBag;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.backpack.EnderBackpackItem;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.Keys;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import dev.xkmc.l2backpack.network.SlotClickToServer;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class ClientEventHandler {

	public static boolean canOpen(ItemStack stack) {
		return stack.getItem() instanceof BaseBagItem ||
				stack.getItem() instanceof EnderBackpackItem ||
				stack.getItem() instanceof WorldChestItem;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void keyEvent(InputEvent.Key event) {
		if (Minecraft.getInstance().screen == null && Proxy.getClientPlayer() != null && Keys.OPEN.map.isDown()) {
			if (canOpen(Proxy.getClientPlayer().getItemBySlot(EquipmentSlot.CHEST)) ||
					!CuriosCompat.getSlot(Proxy.getClientPlayer(), ClientEventHandler::canOpen).isEmpty())
				L2Backpack.HANDLER.toServer(new SlotClickToServer(-1, -1, -1));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onScreenLeftClick(ScreenEvent.MouseButtonReleased.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (insertItem(event, cont, slot)) return;
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && slot != null) {
				if (slot.getItem().getItem() instanceof BaseDrawerItem &&
						!cont.getMenu().getCarried().isEmpty()) {
					event.setCanceled(true);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onScreenRightClick(ScreenEvent.MouseButtonPressed.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (insertItem(event, cont, slot)) return;
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				if (openBackpack(event, cont, slot)) return;
				if (extractItem(event, cont, slot)) return;
				if (slot != null) {
					if (slot.getItem().getItem() instanceof BaseDrawerItem &&
							!cont.getMenu().getCarried().isEmpty()) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean openBackpack(ScreenEvent.MouseButtonPressed.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null) {
			return false;
		}
		boolean b1 = slot.container == Proxy.getClientPlayer().getInventory();
		boolean b2 = cont.getMenu().containerId > 0;
		if (b1 || b2) {
			int inv = b1 ? slot.getSlotIndex() : -1;
			int ind = inv == -1 ? slot.index : -1;
			int wid = cont.getMenu().containerId;
			if ((inv >= 0 || ind >= 0) && (slot.getItem().getItem() instanceof EnderBackpackItem ||
					slot.getItem().getItem() instanceof WorldChestItem ||
					slot.getItem().getItem() instanceof BackpackItem ||
					slot.getItem().getItem() instanceof ArrowBag)) {
				L2Backpack.HANDLER.toServer(new SlotClickToServer(ind, inv, wid));
				event.setCanceled(true);
				return true;
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean insertItem(ScreenEvent.MouseButtonReleased.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack drawerStack = slot.getItem();
		ItemStack stack = cont.getMenu().getCarried();
		if (drawerStack.getItem() instanceof BaseDrawerItem drawer) {
			if (!stack.isEmpty() && drawer.canSetNewItem(drawerStack)) {
				sendDrawerPacket(DrawerInteractToServer.Type.SET, cont, slot);
				event.setCanceled(true);
				return true;
			}
			if (BaseDrawerItem.canAccept(drawerStack, stack)) {
				sendDrawerPacket(DrawerInteractToServer.Type.INSERT, cont, slot);
				event.setCanceled(true);
				return true;
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean insertItem(ScreenEvent.MouseButtonPressed.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack drawerStack = slot.getItem();
		ItemStack stack = cont.getMenu().getCarried();
		if (drawerStack.getItem() instanceof BaseDrawerItem drawer) {
			if (!stack.isEmpty() && drawer.canSetNewItem(drawerStack)) {
				event.setCanceled(true);
				return true;
			}
			if (BaseDrawerItem.canAccept(drawerStack, stack)) {
				event.setCanceled(true);
				return true;
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static boolean extractItem(ScreenEvent.MouseButtonPressed.Pre event, AbstractContainerScreen<?> cont, @Nullable Slot slot) {
		if (slot == null || !slot.allowModification(Proxy.getClientPlayer())) {
			return false;
		}
		ItemStack stack = cont.getMenu().getCarried();
		ItemStack drawerStack = slot.getItem();
		if (drawerStack.getItem() instanceof BaseDrawerItem drawer && stack.isEmpty()) {
			sendDrawerPacket(DrawerInteractToServer.Type.TAKE, cont, slot);
			event.setCanceled(true);
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private static void sendDrawerPacket(DrawerInteractToServer.Type type, AbstractContainerScreen<?> cont, Slot slot) {
		int index = cont.getMenu().containerId == 0 ? slot.getSlotIndex() : slot.index;
		L2Backpack.HANDLER.toServer(new DrawerInteractToServer(type, cont.getMenu().containerId, index, cont.getMenu().getCarried()));
	}

}