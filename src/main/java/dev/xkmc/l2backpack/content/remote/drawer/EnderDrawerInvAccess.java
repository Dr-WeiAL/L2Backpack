package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerInvAccess;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.remote.common.DrawerAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public record EnderDrawerInvAccess(ItemStack drawerStack, EnderDrawerItem drawerItem,
								   ServerLevel level, @Nullable ServerPlayer player) implements BaseDrawerInvAccess {

	@Override
	public int getStoredCount() {
		return DrawerAccess.of(level, drawerStack()).getCount();
	}

	@Override
	public boolean isEmpty() {
		return BaseDrawerItem.getItem(drawerStack) == Items.AIR;
	}

	@Override
	public void setStoredCount(int count) {
		DrawerAccess.of(level, drawerStack()).setCount(count);
	}

}
