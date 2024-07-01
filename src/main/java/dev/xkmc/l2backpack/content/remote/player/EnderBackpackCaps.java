package dev.xkmc.l2backpack.content.remote.player;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class EnderBackpackCaps extends InvPickupCap<InvWrapper> {

	private final ItemStack stack;

	public EnderBackpackCaps(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.get(stack);
	}

	@Override
	public int getSignature() {
		return 0;
	}

	@Override
	public @Nullable InvWrapper getInv(PickupTrace trace) {
		return trace.player == null ? null : new InvWrapper(trace.player.getEnderChestInventory());
	}


}
