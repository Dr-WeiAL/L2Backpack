package dev.xkmc.l2backpack.content.drawer;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@SerialClass
public class DrawerBlockEntity extends IDrawerBlockEntity {

	@SerialField
	public final DrawerHandler handler = new DrawerHandler(this);

	public DrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public ItemStack getItem() {
		return handler.item;
	}

}
