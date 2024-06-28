package dev.xkmc.l2backpack.content.remote.player;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record EnderSyncPacket(
		ArrayList<Entry> list
) implements SerialPacketBase<EnderSyncPacket> {

	public static EnderSyncPacket of(List<Pair<Integer, ItemStack>> list) {
		return new EnderSyncPacket(new ArrayList<>(list.stream().map(e -> new Entry(e.getFirst(), e.getSecond())).toList()));
	}

	@Override
	public void handle(Player player) {
		for (var e : list) {
			TooltipUpdateEvents.onEnderSync(e.slot(), e.stack());
		}
	}

	public record Entry(int slot, ItemStack stack) {

	}

}
