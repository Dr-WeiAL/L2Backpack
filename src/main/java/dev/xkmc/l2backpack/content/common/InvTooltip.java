package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.bag.AbstractBag;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record InvTooltip(TooltipInvItem item, ItemStack stack) implements TooltipComponent {

	public static Optional<TooltipComponent> get(BaseBagItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		var cont = LBItems.BACKPACK_CONTENT.get(stack);
		if (cont != null) {
			return Optional.of(new InvTooltip(item, stack));
		}
		return Optional.empty();
	}

	public static Optional<TooltipComponent> get(EnderBackpackItem item, ItemStack stack) {
		if (Screen.hasShiftDown()) {
			return Optional.empty();
		}
		return Optional.of(new InvTooltip(item, stack));
	}


	public static Optional<TooltipComponent> get(AbstractBag item, ItemStack stack) {
		if (Screen.hasAltDown()) {
			return Optional.of(new InvTooltip(item, stack));
		}
		return Optional.empty();
	}

}
