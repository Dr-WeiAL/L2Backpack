package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.UUID;

public record RespondTooltipUpdateEvent(
		Item item, int count, UUID id
) implements SerialPacketBase<RespondTooltipUpdateEvent> {

	@Override
	public void handle(Player player) {
		TooltipUpdateEvents.updateInfo(item, id, count);
	}

}
