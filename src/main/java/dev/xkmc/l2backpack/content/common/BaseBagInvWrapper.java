package dev.xkmc.l2backpack.content.common;

import dev.xkmc.l2backpack.content.capability.MergedInvBackpackCap;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.restore.DimensionSourceData;
import dev.xkmc.l2screentracker.screen.source.ItemSourceData;
import dev.xkmc.l2screentracker.screen.source.PlayerSlot;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBagInvWrapper extends MergedInvBackpackCap implements ICapabilityProvider {

	private final ItemStack stack;
	private final BaseBagItem bag;
	private final LazyOptional<BaseBagInvWrapper> holder = LazyOptional.of(() -> this);

	private ListTag cachedTag;
	private List<ItemStack> itemStacksCache;

	public BaseBagInvWrapper(ItemStack stack) {
		this.stack = stack;
		this.bag = (BaseBagItem) stack.getItem();
	}

	@Override
	public int getSlots() {
		return bag.getRows(stack) * 9;
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return getItemList().get(slot);
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!isItemValid(slot, stack))
			return stack;

		validateSlotIndex(slot);

		List<ItemStack> itemStacks = getItemList();

		ItemStack existing = itemStacks.get(slot);

		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			setItemList(itemStacks);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		List<ItemStack> itemStacks = getItemList();
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		ItemStack existing = itemStacks.get(slot);

		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				itemStacks.set(slot, ItemStack.EMPTY);
				setItemList(itemStacks);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				setItemList(itemStacks);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= getSlots())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.isEmpty() || bag.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
		List<ItemStack> itemStacks = getItemList();
		itemStacks.set(slot, stack);
		setItemList(itemStacks);
	}

	private List<ItemStack> getItemList() {
		ListTag rootTag = BaseBagItem.getListTag(stack);
		if (itemStacksCache == null || cachedTag == null || !cachedTag.equals(rootTag))
			itemStacksCache = refreshItemList(rootTag);
		return itemStacksCache;
	}

	private List<ItemStack> refreshItemList(ListTag rootTag) {
		List<ItemStack> list = BaseBagItem.getItems(stack);
		int size = getSlots();
		while (list.size() < size) {
			list.add(ItemStack.EMPTY);
		}
		cachedTag = rootTag;
		return list;
	}

	private void setItemList(List<ItemStack> itemStacks) {
		BaseBagItem.setItems(stack, itemStacks);
		cachedTag = BaseBagItem.getListTag(stack);
		itemStacksCache = null;
		saveCallback();
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return holder.cast();
		}
		if (cap == PickupModeCap.TOKEN) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public PickupConfig getPickupMode() {
		return PickupConfig.getPickupMode(stack);
	}

	@Override
	public int getSignature() {
		return stack.hashCode();
	}

	private CallbackData callbackData = null;

	public void attachEnv(ServerPlayer player, PlayerSlot<?> hand) {
		if (hand.data() instanceof DimensionSourceData data) {
			callbackData = new CallbackData(this, player, data);
		}
	}

	private void saveCallback() {
		if (callbackData == null) return;
		callbackData.setChanged();
	}

	private record CallbackData(BaseBagInvWrapper parent, ServerPlayer player, DimensionSourceData data) {

		private void setChanged() {
			var opt = WorldStorage.get(player.serverLevel())
					.getStorageWithoutPassword(data.uuid(), data.color());
			if (opt.isEmpty()) return;
			var cont = opt.get();
			var slot = cont.container.getItem(data.slot());
			if (parent.stack != slot) {
				parent.callbackData = null;
				return;
			}
			cont.container.setChanged();
		}

	}

}