package dev.xkmc.l2backpack.content.insert;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface InsertOnlyItem extends CapInsertItem {

	@OnlyIn(Dist.CLIENT)
	default boolean mayClientTake() {
		return false;
	}

	@Override
	default ItemStack takeItem(ItemStack storage, ServerPlayer player) {
		return ItemStack.EMPTY;
	}

}
