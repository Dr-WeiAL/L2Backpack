package dev.xkmc.l2backpack.content.quickswap.type;

import dev.xkmc.l2backpack.content.quickswap.entry.ISwapEntry;
import dev.xkmc.l2backpack.init.data.LBConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

public class ArrowSwapType extends MatcherSwapType {

	public ArrowSwapType(String name, int index) {
		super(name, index, true);
	}

	@Override
	public boolean activePopup() {
		return LBConfig.CLIENT.popupArrowOnSwitch.get();
	}

	@Override
	public boolean match(ItemStack stack) {
		return stack.getItem() instanceof ProjectileWeaponItem;
	}

	@Override
	public boolean isAvailable(Player player, ISwapEntry<?> token) {
		ItemStack stack = token.getStack();
		ItemStack bowStack = getSignatureItem(player);
		if (bowStack.getItem() instanceof ProjectileWeaponItem bow) {
			return !stack.isEmpty() && bow.getAllSupportedProjectiles().test(stack);
		}
		return false;
	}

}
