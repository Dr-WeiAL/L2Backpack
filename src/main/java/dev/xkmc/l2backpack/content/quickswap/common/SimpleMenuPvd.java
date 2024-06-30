package dev.xkmc.l2backpack.content.quickswap.common;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class SimpleMenuPvd implements MenuProvider {

	public interface BagMenuFactory {

		AbstractContainerMenu create(int id, Inventory inventory, PlayerSlot<?> slot, UUID uuid, Component title);

	}

	private final ServerPlayer player;
	private final PlayerSlot<?> slot;
	private final ItemStack stack;
	private final BaseBagItem bag;
	private final BagMenuFactory factory;

	public SimpleMenuPvd(ServerPlayer player, PlayerSlot<?> slot, BaseBagItem item, ItemStack stack, BagMenuFactory factory) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
		this.bag = item;
		this.factory = factory;
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return factory.create(id, inventory, slot, LBItems.DC_CONT_ID.getOrDefault(stack, Util.NIL_UUID), getDisplayName());
	}

	public void writeBuffer(RegistryFriendlyByteBuf buf) {
		slot.write(buf);
		buf.writeUUID(LBItems.DC_CONT_ID.getOrDefault(stack, Util.NIL_UUID));
	}

	public void open() {
		bag.checkInit(stack);
		player.openMenu(this, this::writeBuffer);
	}

}
