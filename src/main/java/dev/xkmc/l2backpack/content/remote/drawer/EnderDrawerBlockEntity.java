package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.drawer.IDrawerBlockEntity;
import dev.xkmc.l2backpack.content.remote.common.EnderDrawerAccess;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.UUID;

@SerialClass
public class EnderDrawerBlockEntity extends IDrawerBlockEntity {

	@SerialField
	public UUID ownerId = Util.NIL_UUID;
	@SerialField
	public Component ownerName = Component.empty();
	@SerialField
	public Item item = Items.AIR;
	@SerialField
	public PickupConfig config = PickupConfig.DEF;

	public EnderDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Nullable
	public IItemHandler getItemHandler() {
		if (level == null || this.remove) return null;
		if (level.isClientSide()) {
			return new InvWrapper(new SimpleContainer(64));
		}
		if (ownerId == null) return null;
		return new EnderDrawerItemHandler(getAccess(), true);
	}

	public EnderDrawerAccess getAccess() {
		return EnderDrawerAccess.of(level, ownerId, item);
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
			getAccess().listener.add(this);
		}
	}

	public void removeFromListener() {
		if (added && level != null && !level.isClientSide() && ownerId != null) {
			added = false;
			getAccess().listener.remove(this);
		}
	}

	@Override
	public ItemStack getItem() {
		return item.getDefaultInstance();
	}

}
