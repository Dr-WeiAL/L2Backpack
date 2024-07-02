package dev.xkmc.l2backpack.content.remote.dimensional;

import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DimensionalScreen extends BaseOpenableScreen<DimensionalContainer> {

	public DimensionalScreen(DimensionalContainer cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

}
