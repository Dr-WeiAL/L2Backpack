package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.content.drawer.DrawerInvWrapper;
import dev.xkmc.l2backpack.content.remote.common.EnderDrawerAccess;
import dev.xkmc.l2backpack.content.render.BaseItemRenderer;
import dev.xkmc.l2backpack.events.TooltipUpdateEvents;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

public class EnderDrawerItem extends BlockItem implements BaseDrawerItem {

	public static Item getItem(ItemStack drawer) {
		return LBItems.DC_ENDER_DRAWER_ITEM.getOrDefault(drawer, Items.AIR);
	}

	public EnderDrawerItem(Block block, Properties properties) {
		super(block, properties.stacksTo(1).fireResistant());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

	@Override
	public ItemStack getDrawerContent(ItemStack drawer) {
		return getItem(drawer).getDefaultInstance();
	}

	@Override
	public boolean canAccept(ItemStack drawer, ItemStack stack) {
		Item item = getItem(drawer);
		return item == Items.AIR || stack.isComponentsPatchEmpty() && stack.getItem() == item;
	}

	void refresh(ItemStack drawer, Player player) {
		if (LBItems.DC_OWNER_ID.get(drawer) == null) {
			drawer.set(LBItems.DC_OWNER_ID, player.getUUID());
			drawer.set(LBItems.DC_OWNER_NAME, player.getName());
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getItem(stack) == Items.AIR)
			return InteractionResultHolder.fail(stack);
		if (!(player instanceof ServerPlayer sp)) {
			ContentTransfer.playDrawerSound(player);
			return InteractionResultHolder.success(stack);
		} else refresh(stack, player);
		if (!player.isShiftKeyDown()) {
			ItemStack take = takeItem(stack, sp);
			int c = take.getCount();
			player.getInventory().placeItemBackInInventory(take);
			ContentTransfer.onExtract(player, c, stack);
		} else {
			EnderDrawerAccess access = EnderDrawerAccess.of(world, stack);
			int count = access.getCount();
			int max = getStacking(stack) * access.item().getDefaultMaxStackSize();
			int ext = BaseDrawerItem.loadFromInventory(max, count, access.item(), player);
			count += ext;
			access.setCount(count);
			ContentTransfer.onCollect(player, ext, stack);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (context.getItemInHand().get(LBItems.DC_OWNER_ID) == null)
			return InteractionResult.FAIL;
		if (getItem(context.getItemInHand()) == Items.AIR) {
			if (!context.getLevel().isClientSide()) {
				if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
					serverPlayer.sendSystemMessage(LangData.IDS.NO_ITEM.get().withStyle(ChatFormatting.RED), true);
				}
			}
			return InteractionResult.FAIL;
		}
		if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		InteractionResult result = super.useOn(context);
		if (result == InteractionResult.FAIL) return InteractionResult.PASS;
		return result;
	}

	@Override
	public void insert(ItemStack drawer, ItemStack stack, Player player) {
		refresh(drawer, player);
		EnderDrawerAccess access = EnderDrawerAccess.of(player.level(), drawer);
		int count = access.getCount();
		int take = Math.min(getStacking(drawer) * stack.getMaxStackSize() - count, stack.getCount());
		access.setCount(access.getCount() + take);
		stack.shrink(take);
	}

	@Override
	public ItemStack takeItem(ItemStack drawer, int max, Player player, boolean simulate) {
		refresh(drawer, player);
		EnderDrawerAccess access = EnderDrawerAccess.of(player.level(), drawer);
		Item item = getItem(drawer);
		int take = Math.min(access.getCount(), Math.min(max, item.getDefaultMaxStackSize()));
		if (!simulate)
			access.setCount(access.getCount() - take);
		return new ItemStack(item, take);
	}

	@Override
	public boolean canSetNewItem(ItemStack drawer) {
		return getItem(drawer) == Items.AIR;
	}

	@Override
	public void setItem(ItemStack drawer, ItemStack item, Player player) {
		refresh(drawer, player);
		LBItems.DC_ENDER_DRAWER_ITEM.set(drawer, item.getItem());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		Item item = getItem(stack);
		var id = LBItems.DC_OWNER_ID.get(stack);
		if (item != Items.AIR) {
			int count = id == null ? -1 : TooltipUpdateEvents.getCount(id, item);
			list.add(LangData.IDS.DRAWER_CONTENT.get(item.getDescription(), count < 0 ? "???" : count));
		}
		var name = LBItems.DC_OWNER_NAME.get(stack);
		if (name != null) {
			list.add(LangData.IDS.STORAGE_OWNER.get(name));
			PickupConfig.addText(stack, list);

		}
		LangData.addInfo(list,
				LangData.Info.ENDER_DRAWER,
				LangData.Info.EXTRACT_DRAWER,
				LangData.Info.PLACE,
				LangData.Info.COLLECT_DRAWER,
				LangData.Info.ENDER_DRAWER_USE);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	public DrawerInvWrapper getCaps(ItemStack stack, Void ignored) {
		return new DrawerInvWrapper(stack, trace -> trace.player == null ? null : new EnderDrawerInvAccess(stack, this, trace.player));
	}

	@Override
	public void serverTrigger(ItemStack storage, ServerPlayer player) {
		var id = LBItems.DC_OWNER_ID.get(storage);
		if (id != null && !id.equals(player.getUUID())) {
			LBTriggers.SHARE.get().trigger(player);
		}
	}

	private static final ResourceLocation BG = L2Backpack.loc("textures/block/drawer/ender_side.png");

	@Override
	public ResourceLocation backgroundLoc() {
		return BG;
	}
}
