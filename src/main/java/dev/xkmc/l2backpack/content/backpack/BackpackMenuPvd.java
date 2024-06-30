package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class BackpackMenuPvd implements MenuProvider {

	private final ServerPlayer player;
	private final PlayerSlot<?> slot;
	private final ItemStack stack;
	private final BaseBagItem bag;

	public BackpackMenuPvd(ServerPlayer player, PlayerSlot<?> slot, BaseBagItem item, ItemStack stack) {
		this.player = player;
		this.slot = slot;
		this.stack = stack;
		bag = item;
	}

	@Override
	public Component getDisplayName() {
		return stack.getHoverName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID uuid = tag.getUUID("container_id");
		return new BackpackMenu(id, inventory, slot, uuid, bag.getRows(stack), getDisplayName());
	}

	public void writeBuffer(RegistryFriendlyByteBuf buf) {
		CompoundTag tag = stack.getOrCreateTag();
		UUID id = tag.getUUID("container_id");
		slot.write(buf);
		buf.writeUUID(id);
		buf.writeInt(bag.getRows(stack));
	}

	public void open() {
		CompoundTag tag = stack.getOrCreateTag();
		bag.checkInit(stack);
		player.openMenu(this, this::writeBuffer);
	}

}
