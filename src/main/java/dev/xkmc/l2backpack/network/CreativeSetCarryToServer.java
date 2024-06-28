package dev.xkmc.l2backpack.network;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record CreativeSetCarryToServer(
		ItemStack stack
) implements SerialPacketBase<CreativeSetCarryToServer> {

	@Override
	public void handle(Player player) {
		if (!player.getAbilities().instabuild) return;
		player.containerMenu.setCarried(stack);
	}

}
