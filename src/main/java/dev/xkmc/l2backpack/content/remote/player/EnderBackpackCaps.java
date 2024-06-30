package dev.xkmc.l2backpack.content.remote.player;

import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnderBackpackCaps extends InvPickupCap<InvWrapper> implements ICapabilityProvider {

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

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == PickupModeCap.TOKEN) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

}
