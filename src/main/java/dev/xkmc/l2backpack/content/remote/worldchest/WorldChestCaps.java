package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.BackpackCap;
import dev.xkmc.l2backpack.content.capability.InvBackpackCap;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldChestCaps extends InvBackpackCap implements ICapabilityProvider {

	private final ItemStack stack;
	private final LazyOptional<WorldChestCaps> holder = LazyOptional.of(() -> this);

	public WorldChestCaps(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public PickupMode getMode() {
		return BackpackCap.getMode(stack);
	}

	@Override
	public int getSignature() {
		if (stack.getItem() instanceof WorldChestItem item) {
			int color = item.color.ordinal();
			var opt = WorldChestItem.getOwner(stack);
			if (opt.isPresent()) {
				return opt.get().hashCode() ^ color ^ 0x55AA;
			}
		}
		return 0;
	}

	@Override
	public IItemHandlerModifiable getInv(PickupTrace trace) {
		if (stack.getItem() instanceof WorldChestItem item) {
			var opt = item.getContainer(stack, trace.player.serverLevel());
			if (opt.isPresent()) {
				var storage = opt.get();
				return new WorldChestInvWrapper(storage.container, storage.id);
			}
		}
		return null;
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == BackpackCap.TOKEN) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

}
