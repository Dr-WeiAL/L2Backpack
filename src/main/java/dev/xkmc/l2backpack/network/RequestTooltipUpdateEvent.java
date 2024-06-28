package dev.xkmc.l2backpack.network;

import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.UUID;

public record RequestTooltipUpdateEvent(
		Item item, UUID id
) implements SerialPacketBase<RequestTooltipUpdateEvent> {

	@Override
	public void handle(Player pl) {
		if (!(pl instanceof ServerPlayer player)) return;
		int count = DrawerAccess.of(player.level(), id, item).getCount();
		L2Backpack.HANDLER.toClientPlayer(new RespondTooltipUpdateEvent(item, count, id), player);
	}


}
