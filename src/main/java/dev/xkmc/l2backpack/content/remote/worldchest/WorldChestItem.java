package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldChestItem extends BlockItem {

	public final DyeColor color;

	public WorldChestItem(DyeColor color, Properties props) {
		super(BackpackBlocks.WORLD_CHEST.get(), props.stacksTo(1).fireResistant());
		this.color = color;
	}

	void refresh(ItemStack stack, Player player) {
		if (!stack.getOrCreateTag().contains("owner_id")) {
			stack.getOrCreateTag().putUUID("owner_id", player.getUUID());
			stack.getOrCreateTag().putString("owner_name", player.getName().getString());
			stack.getOrCreateTag().putLong("password", color.getId());
		}
	}

	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (super.allowedIn(tab)) {
			list.add(new ItemStack(this));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new WorldChestMenuPvd((ServerPlayer) player, stack, this).open();
		} else {
			player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 1);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (context.getPlayer() != null && !context.getPlayer().isCrouching()) {
			ItemStack stack = context.getItemInHand();
			if (!context.getLevel().isClientSide()) {
				new WorldChestMenuPvd((ServerPlayer) context.getPlayer(), stack, this).open();
			} else {
				context.getPlayer().playSound(SoundEvents.ENDER_CHEST_OPEN, 1, 1);
			}
			return InteractionResult.SUCCESS;
		}
		if (!context.getItemInHand().getOrCreateTag().contains("owner_id"))
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("owner_name")) {
				String name = tag.getString("owner_name");
				list.add(LangData.IDS.STORAGE_OWNER.get(name));
			}
		}
		LangData.addInfo(list, LangData.Info.QUICK_ANY_ACCESS, LangData.Info.PLACE, LangData.Info.KEYBIND);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

}
