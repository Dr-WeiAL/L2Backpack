package dev.xkmc.l2backpack.init;

import dev.xkmc.l2backpack.content.common.InvClientTooltip;
import dev.xkmc.l2backpack.content.common.InvTooltip;
import dev.xkmc.l2backpack.content.quickswap.common.QuickSwapOverlay;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.render.BackpackModel;
import dev.xkmc.l2backpack.content.render.DrawerCountDeco;
import dev.xkmc.l2backpack.content.render.EnderPreviewOverlay;
import dev.xkmc.l2backpack.content.render.RenderEvents;
import dev.xkmc.l2backpack.init.data.BackpackKeys;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2BackpackClient {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(BackpackItems.QUIVER.get(), new ResourceLocation(L2Backpack.MODID, "arrow"), (stack, level, entity, i) -> Quiver.displayArrow(stack));
		});
	}

	@SubscribeEvent
	public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(InvTooltip.class, InvClientTooltip::new);
	}

	@SubscribeEvent
	public static void registerOverlay(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "arrow_bag", new QuickSwapOverlay());
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "ender_drawer", new EnderPreviewOverlay());
	}

	@SubscribeEvent
	public static void registerDeco(RegisterItemDecorationsEvent event) {
		var deco = new DrawerCountDeco();
		event.register(BackpackItems.DRAWER.get(), deco);
		event.register(BackpackItems.ENDER_DRAWER.get(), deco);
	}

	@SubscribeEvent
	public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> RenderEvents.registerBackpackLayer());
	}

	@SubscribeEvent
	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RenderEvents.BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		for (var e : BackpackKeys.values()) {
			event.register(e.map);
		}
	}

}
