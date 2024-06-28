package dev.xkmc.l2backpack.content.quickswap.scabbard;

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

public class ScabbardMenu extends BaseBagMenu<ScabbardMenu> {

	public static final SpriteManager MANAGERS = new SpriteManager(L2Backpack.MODID, "backpack_1");

	public static ScabbardMenu fromNetwork(MenuType<ScabbardMenu> type, int windowId, Inventory inv, RegistryFriendlyByteBuf buf) {
		PlayerSlot<?> slot = PlayerSlot.read(buf);
		UUID id = buf.readUUID();
		return new ScabbardMenu(windowId, inv, slot, id, null);
	}

	public ScabbardMenu(int windowId, Inventory inventory, PlayerSlot<?> hand, UUID uuid, @Nullable Component title) {
		super(BackpackMenus.MT_TOOL.get(), windowId, inventory, MANAGERS, hand, uuid, 1);
	}

}
