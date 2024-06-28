package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.DestroyMode;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.capability.PickupMode;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2library.base.tile.BaseBlockEntity;
import dev.xkmc.l2modularblock.tile_api.NameSetable;
import dev.xkmc.l2serial.serialization.SerialClass;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SerialClass
public class WorldChestBlockEntity extends BaseBlockEntity implements MenuProvider, NameSetable, ContainerListener {

	@SerialClass.SerialField
	public UUID owner_id;
	@SerialClass.SerialField(toClient = true)
	public String owner_name;
	@SerialClass.SerialField
	long password;
	@SerialClass.SerialField(toClient = true)
	public int color;
	@SerialClass.SerialField(toClient = true)
	public PickupConfig config = new PickupConfig(PickupMode.NONE, DestroyMode.NONE);

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
				Optional<StorageContainer> storage = WorldStorage.get(sl).getOrCreateStorage(sl, owner_id, color, password, null, null, 0);
				handler = storage.isEmpty() ? LazyOptional.empty() : LazyOptional.of(() -> new WorldChestInvWrapper(storage.get().container, owner_id));
			}

			if (handler == null) {
				Optional<StorageContainer> storage = WorldStorage.get((ServerLevel) level)
						.getOrCreateStorage(owner_id, color, password, null, null, 0);
				if (storage.isEmpty()) handler = LazyOptional.empty();
				else if (config == null || config.pickup() == PickupMode.NONE) {
					handler = LazyOptional.of(() -> new WorldChestInvWrapper(storage.get().container, owner_id));
				} else {
					handler = LazyOptional.of(() -> new BlockPickupInvWrapper(sl, this, storage.get(), config));
				}

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
		return name == null ? LangData.IDS.STORAGE_OWNER.get(WorldChestItem.getName(owner_name)) : name;
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int wid, Inventory inventory, Player player) {
		if (level == null || owner_id == null) return null;
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
		return WorldStorage.get((ServerLevel) level).getOrCreateStorage((ServerLevel) level, owner_id, color, password, null, null, 0);
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
		if (!added && level != null && !level.isClientSide() && owner_id != null) {
			added = true;
			getAccess().ifPresent(e -> e.container.addListener(this));
		}
	}

	public void removeFromListener() {
		if (added && level != null && !level.isClientSide() && owner_id != null) {
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
