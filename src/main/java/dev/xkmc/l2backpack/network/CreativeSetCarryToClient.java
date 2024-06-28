package dev.xkmc.l2backpack.network;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record CreativeSetCarryToClient(
		ItemStack stack
) implements SerialPacketBase<CreativeSetCarryToClient> {

	@Override
	public void handle(Player player) {
		player.containerMenu.setCarried(stack);
	}

}
