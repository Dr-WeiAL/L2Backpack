package dev.xkmc.l2backpack.content.bag;

import net.minecraft.world.item.ItemStack;

public class EquipmentBag extends AbstractBag {

	public EquipmentBag(Properties props) {
		super(props);
	}

	@Override
	public boolean isValidContent(ItemStack stack) {
		return stack.isDamageableItem();
	}

}
