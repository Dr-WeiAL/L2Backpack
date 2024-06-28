package dev.xkmc.l2backpack.events;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.content.remote.common.WorldStorageCapability;
import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Backpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {

	@SubscribeEvent
	public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel level) {
			if (level.dimension() == Level.OVERWORLD) {
				event.addCapability(new ResourceLocation(L2Backpack.MODID, "world_storage"),
						new WorldStorageCapability(level));
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		ItemStack stack = event.getItem().getItem();
		ItemStack copy = stack.copy();
		int count = stack.getCount();
		tryInsertItem(player, stack);
		if (count != stack.getCount()) {
			copy.shrink(stack.getCount());
			player.take(event.getItem(), copy.getCount());
			CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), copy);

		}
	}

	/**
	 * if item inserted, stack is modified
	 */
	public static void tryInsertItem(ServerPlayer player, ItemStack stack) {
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		chest.getCapability(PickupModeCap.TOKEN).resolve().ifPresent(
				cap -> cap.doPickup(stack, new PickupTrace(false, player)));
		if (stack.isEmpty()) return;
		CuriosCompat.getSlot(player, e -> {
			if (stack.isEmpty()) return false;
			e.getCapability(PickupModeCap.TOKEN).resolve().ifPresent(
					cap -> cap.doPickup(stack, new PickupTrace(false, player)));
			return false;
		});
	}

}
