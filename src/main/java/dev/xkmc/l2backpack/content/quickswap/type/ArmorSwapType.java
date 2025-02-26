package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.quickswap.entry.*;
import dev.xkmc.l2backpack.init.data.BackpackConfig;
import dev.xkmc.l2library.base.overlay.OverlayUtil;
import dev.xkmc.l2library.base.overlay.SelectionSideBar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorSwapType extends QuickSwapType
		implements ISideInfoRenderer, ISingleSwapAction, ISetSwapAction {

	public ArmorSwapType(String name, int index) {
		super(name, index);
	}

	@Override
	public boolean activePopup() {
		return BackpackConfig.CLIENT.popupArmorOnSwitch.get();
	}

	@Override
	public ItemStack getSignatureItem(Player player) {
		return ItemStack.EMPTY;
	}

	private boolean maySwapOut(ItemStack stack) {
		return stack.getItem().canFitInsideContainerItems() &&
				!(stack.getItem() instanceof BaseBagItem);
	}

	private EquipmentSlot getSlot(int i) {
		return EquipmentSlot.values()[5 - i];
	}

	@Override
	public void swapSingle(Player player, ISingleSwapHandler handler) {
		ItemStack stack = handler.getStack();
		if (stack.isEmpty()) return;
		EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
		if (!maySwapOut(player.getItemBySlot(slot))) return;
		handler.replace(player.getItemBySlot(slot));
		player.setItemSlot(slot, stack);
	}

	@Override
	public void swapSet(Player player, ISetSwapHandler handler) {
		for (int i = 0; i < 4; i++) {
			EquipmentSlot e = getSlot(i);
			if (!maySwapOut(player.getItemBySlot(e)))
				continue;
			ItemStack stack = handler.getStack(i);
			handler.replace(i, player.getItemBySlot(e));
			player.setItemSlot(e, stack);
		}
	}

	@Override
	public boolean isAvailable(Player player, ISwapEntry<?> token) {
		if (token instanceof SingleSwapEntry single) {
			ItemStack stack = single.stack();
			if (stack.isEmpty()) return false;
			EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
			return maySwapOut(player.getItemBySlot(slot));
		}
		if (token instanceof SetSwapEntry) {
			for (int i = 0; i < 4; i++) {
				if (isAvailable(player, token, i))
					return true;
			}
			return false;
		}
		return false;
	}


	public boolean isAvailable(Player player, ISwapEntry<?> token, int index) {
		EquipmentSlot e = getSlot(index);
		ItemStack old = player.getItemBySlot(e);
		ItemStack cur = token.asList().get(index);
		return maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
	}

	public void renderSide(SelectionSideBar.Context ctx, int x, int y, Player player, ISwapEntry<?> token) {
		if (token instanceof SingleSwapEntry single) {
			ItemStack hover = single.stack();
			EquipmentSlot target = LivingEntity.getEquipmentSlotForItem(hover);
			for (int i = 0; i < 4; i++) {
				EquipmentSlot slot = getSlot(i);
				ItemStack stack = player.getItemBySlot(slot);
				ItemStack targetStack = player.getItemBySlot(target);
				renderArmorSlot(ctx.g(), x, y, 64, target == slot, !maySwapOut(targetStack));
				ctx.renderItem(stack, x, y);
				y += 18;
			}
		}
		if (token instanceof SetSwapEntry set) {
			for (int i = 0; i < 4; i++) {
				EquipmentSlot e = getSlot(i);
				ItemStack old = player.getItemBySlot(e);
				ItemStack cur = set.asList().get(i);
				boolean avail = maySwapOut(old) && (!old.isEmpty() || !cur.isEmpty());
				renderArmorSlot(ctx.g(), x, y, 64, !old.isEmpty() || !cur.isEmpty(), !avail);
				ctx.renderItem(old, x, y);
				y += 18;
			}
		}
	}

	private static void renderArmorSlot(GuiGraphics g, int x, int y, int a, boolean target, boolean invalid) {
		OverlayUtil.fillRect(g, x, y, 16, 16, color(255, 255, 255, a));
		if (target) {
			if (invalid) {
				OverlayUtil.drawRect(g, x, y, 16, 16, color(220, 70, 70, 255));
			} else {
				OverlayUtil.drawRect(g, x, y, 16, 16, color(70, 150, 185, 255));
			}
		}
	}

}
