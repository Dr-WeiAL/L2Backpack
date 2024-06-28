package dev.xkmc.l2backpack.content.backpack;

import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.registrate.BackpackMenus;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.UUID;

public class BackpackMenu extends BaseBagMenu<BackpackMenu> {

	public static final SpriteManager[] MANAGERS = new SpriteManager[BackpackItem.MAX_ROW];

	static {
		for (int i = 0; i < BackpackItem.MAX_ROW; i++) {
			MANAGERS[i] = new SpriteManager(L2Backpack.MODID, "backpack_" + (i + 1));
		}
	}

	public static BackpackMenu fromNetwork(MenuType<BackpackMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		int row = buf.readInt();
		return new BackpackMenu(windowId, inv, slot, id, row, null);
	}

	public BackpackMenu(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, int row, @Nullable Component title) {
		super(BackpackMenus.MT_BACKPACK.get(), windowId, inventory, MANAGERS[row - 1], hand, uuid, row);
	}

}
