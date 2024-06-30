package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = L2Backpack.MODID, bus = EventBusSubscriber.Bus.GAME)
public class LoadContainerEvents {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void playerLeftClick(@NotNull PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof ContentTransfer.Quad load) {
			if (!event.getLevel().isClientSide()) {
				ContentTransfer.leftClick(load, event.getLevel(), event.getPos(), event.getItemStack(), event.getEntity(), event.getFace());
			}
			event.setCanceled(true);
		}
	}

}
