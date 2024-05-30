package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.compat.CuriosCompat;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.events.BackpackSlotClickListener;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class TweakerTool extends Item implements IBagTool {

	public TweakerTool(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		ItemStack bag = player.getItemBySlot(EquipmentSlot.CHEST);
		if (!BackpackSlotClickListener.canOpen(bag)) {
			var opt = CuriosCompat.getSlot(player, BackpackSlotClickListener::canOpen);
			if (opt.isPresent()) {
				bag = opt.get().getFirst();
			}
		}
		if (BackpackSlotClickListener.canOpen(bag)) {
			if (!level.isClientSide()) {
				click(bag);
				if (player instanceof ServerPlayer sp) {
					var msg = bag.getHoverName().copy().append(": ")
							.append(message(PickupConfig.getPickupMode(bag)));
					sp.sendSystemMessage(msg, true);
				}
			}
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	public abstract Component message(PickupConfig config);

}
