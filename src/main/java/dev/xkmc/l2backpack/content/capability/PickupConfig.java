package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PickupConfig(PickupMode pickup, DestroyMode destroy) {

	public static final PickupConfig DEF = new PickupConfig(PickupMode.NONE, DestroyMode.NONE);

	public static PickupConfig get(ItemStack stack) {
		return LBItems.DC_PICKUP.getOrDefault(stack, DEF);
	}

	public static void addText(ItemStack stack, List<Component> list) {
		var mode = get(stack);
		list.add(mode.pickup().getTooltip());
		list.add(mode.destroy().getTooltip());
	}

	public static void iterateMode(ItemStack stack) {
		var mode = get(stack);
		stack.set(LBItems.DC_PICKUP, mode.iterateMode());
	}

	public static void iterateDestroy(ItemStack stack) {
		var mode = get(stack);
		stack.set(LBItems.DC_PICKUP, mode.iterateDestroy());
	}

	public PickupConfig iterateMode() {
		PickupMode next = PickupMode.values()[(pickup().ordinal() + 1) % PickupMode.values().length];
		return new PickupConfig(next, destroy);
	}

	public PickupConfig iterateDestroy() {
		DestroyMode next = DestroyMode.values()[(destroy().ordinal() + 1) % DestroyMode.values().length];
		return new PickupConfig(pickup, next);
	}

}
