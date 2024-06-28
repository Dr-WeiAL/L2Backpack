package dev.xkmc.l2backpack.content.click;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface VanillaQuickInsert {

	void l2backpack$quickMove(ServerPlayer player, AbstractContainerMenu menu, ItemStack stack, int slot);

}
