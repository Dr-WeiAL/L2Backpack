package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2core.base.tile.BaseBlockEntity;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SerialClass
public class WorldChestBlockEntity extends BaseBlockEntity implements MenuProvider, NameSetable, ContainerListener {

	@SerialField
	public UUID ownerId;
	@SerialField
	public Component ownerName;
	@SerialField
	long password;
	@SerialField
	public int color;
	@SerialField
	public PickupConfig config = PickupConfig.DEF;

	private Component name;

	private LazyOptional<IItemHandler> handler;

	public WorldChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (level != null && !this.remove &&
				cap == ForgeCapabilities.ITEM_HANDLER) {
			if (!(level instanceof ServerLevel sl)) {
				return LazyOptional.of(() -> new InvWrapper(new SimpleContainer(27))).cast();
			}
			if (handler == null && level instanceof ServerLevel sl) {
				Optional<StorageContainer> storage = WorldStorage.get(sl).getOrCreateStorage(sl, ownerId, color, password, null, null, 0);

				if (storage.isEmpty()) handler = LazyOptional.empty();
				else if (config == null || config.pickup() == PickupMode.NONE) {
					handler = LazyOptional.of(() -> new WorldChestInvWrapper(storage.get().container, ownerId));
				} else {
					handler = LazyOptional.of(() -> new BlockPickupInvWrapper(sl, this, storage.get(), config));
				}

				handler = storage.isEmpty() ? LazyOptional.empty() : LazyOptional.of(() -> new WorldChestInvWrapper(storage.get().container, ownerId));
			}

			return this.handler.cast();
		}
		return super.getCapability(cap, side);
	}

	public void setColor(int color) {
		if (this.color == color)
			return;
		handler = null;
		this.color = color;
		this.password = color;
		this.setChanged();
	}

	@Override
	public Component getName() {
		if (name != null) return name;
		return ownerName;
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inventory, Player player) {
		if (level == null || ownerId == null) return null;
		Optional<StorageContainer> storage = getAccess();
		if (storage.isEmpty()) return null;
		return new WorldChestContainer(wid, inventory, storage.get().container, storage.get(), this);
	}

	@Override
	public void setCustomName(Component component) {
		name = component;
	}

	public boolean stillValid(Player player) {
		assert level != null;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	private Optional<StorageContainer> getAccess() {
		assert level != null;
		return WorldStorage.get((ServerLevel) level).getOrCreateStorage((ServerLevel) level, ownerId, color, password, null, null, 0);
	}

	private boolean added = false;

	@Override
	public void onChunkUnloaded() {
		removeFromListener();
		super.onChunkUnloaded();
	}

	@Override
	public void setRemoved() {
		removeFromListener();
		super.setRemoved();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		addToListener();
	}

	public void addToListener() {
		if (!added && level != null && !level.isClientSide() && ownerId != null) {
			added = true;
			getAccess().ifPresent(e -> e.container.addListener(this));
		}
	}

	public void removeFromListener() {
		if (added && level != null && !level.isClientSide() && ownerId != null) {
			added = false;
			getAccess().ifPresent(e -> e.container.removeListener(this));
		}
	}

	@Override
	public void containerChanged(Container p_18983_) {
		setChanged();
	}

	public void setPickupMode(PickupConfig click) {
		this.config = click;
		handler = null;
		sync();
		setChanged();
	}
}
