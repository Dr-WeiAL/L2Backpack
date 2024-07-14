package dev.xkmc.l2backpack.compat;

import dev.xkmc.l2backpack.content.remote.player.EnderSyncCap;
import dev.xkmc.l2backpack.content.remote.player.EnderTickEvent;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;

public class SophisticatedCompat {

	public static void init() {
		PlayerInventoryProvider.get().addPlayerInventoryHandler("ender",
				l -> PlayerInventoryHandler.SINGLE_IDENTIFIER,
				(player, id) -> getEnderSize(player),
				(player, id, slot) -> getEnderInv(player, slot),
				false, false, false, false);
	}

	private static int getEnderSize(Player player) {
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getContainerSize();
		}
		return EnderSyncCap.HOLDER.get(player).getItems().size();
	}

	private static ItemStack getEnderInv(Player player, int index) {
		if (player instanceof ServerPlayer) {
			return player.getEnderChestInventory().getItem(index);
		}
		return EnderSyncCap.HOLDER.get(player).getItems().get(index);
	}

	@SubscribeEvent
	public static void onEnderTick(EnderTickEvent event) {
		var player = event.getPlayer();
		if (player.isSpectator() || player.isDeadOrDying()) return;
		event.getStack().getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent((wrapper) ->
				wrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach((upgrade) ->
						upgrade.tick(player, player.level(), player.blockPosition())));
	}

}
