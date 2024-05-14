package dev.xkmc.l2backpack.content.render;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public class DrawerCountDeco implements IItemDecorator {

	@Override
	public boolean render(GuiGraphics g, Font font, ItemStack stack, int x, int y) {
		if (stack.getItem() instanceof BaseDrawerItem item) {
			String s = getCount(item, stack);
			if (!s.isEmpty()) {
				g.pose().pushPose();
				if (Screen.hasShiftDown()) {
					drawBG(g, item, x, y);
				}
				int height = getZ();
				int width = font.width(s);
				int x0 = Math.max(3, 17 - width);
				g.pose().translate(x + x0, y + 16, height);
				if (width > 14) {
					float sc = 14f / width;
					g.pose().scale(sc, sc, 1);
				}
				int col = 0xffffff7f;
				g.drawString(font, s, 0, -7, col);
				g.pose().popPose();
				return true;
			}
		}
		return false;
	}

	private void drawBG(GuiGraphics g, BaseDrawerItem item, int x, int y) {
		g.blit(item.backgroundLoc(), x, y, 0, 0, 16, 16, 16, 16);
	}

	private static String getCount(BaseDrawerItem item, ItemStack stack) {
		if (item.canSetNewItem(stack)) {
			return "";
		}
		if (item instanceof DrawerItem) {
			int count = DrawerItem.getCount(stack);
			if (count == 0)
				return "";
			if (count <= 999)
				return "" + count;
			else return count / 1000 + "k";
		}
		if (item instanceof EnderDrawerItem) {
			return Screen.hasShiftDown() ? "?" : "";
		}
		return "";
	}

	private static int getZ() {
		return 250;
	}

}
