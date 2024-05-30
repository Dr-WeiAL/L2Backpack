package dev.xkmc.l2backpack.content.bag;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.common.InvTooltip;
import dev.xkmc.l2backpack.content.common.TooltipInvItem;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;


public abstract class AbstractBag extends Item
		implements ContentTransfer.Quad, PickupBagItem, InsertOnlyItem, TooltipInvItem {

	public static final int SIZE = 64;

	public AbstractBag(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return ContentTransfer.blockInteract(context, this);
	}

	public NonNullList<ItemStack> getContent(ItemStack stack) {
		NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
		CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
		if (tag.contains("Items")) ContainerHelper.loadAllItems(tag, list);
		return list;
	}

	public void setContent(ItemStack stack, NonNullList<ItemStack> list) {
		CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
		ContainerHelper.saveAllItems(tag, list);
	}

	@Override
	public void click(Player player, ItemStack stack, boolean client, boolean shift, boolean right, @Nullable IItemHandler target) {
		if (!client && shift && right && target != null) {
			NonNullList<ItemStack> list = getContent(stack);
			int pre = 0;
			for (ItemStack inv : list) pre += inv.getCount();
			ContentTransfer.transfer(list, target);
			int post = 0;
			for (ItemStack inv : list) post += inv.getCount();
			setContent(stack, list);
			ContentTransfer.onDump(player, pre - post, stack);
		} else if (client && shift && right && target != null)
			ContentTransfer.playSound(player);
		if (!client && shift && !right && target != null) {
			NonNullList<ItemStack> list = getContent(stack);
			int count = ContentTransfer.loadFrom(list, target, player, e -> matches(stack, e));
			setContent(stack, list);
			ContentTransfer.onLoad(player, count, stack);
		} else if (client && shift && !right && target != null)
			ContentTransfer.playSound(player);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide()) {
			ContentTransfer.playSound(player);
			return InteractionResultHolder.success(stack);
		}
		NonNullList<ItemStack> list = getContent(stack);
		if (player.isShiftKeyDown()) {
			throwOut(list, player, stack);
		} else {
			Queue<Holder<ItemStack>> queue = new ArrayDeque<>();
			player.getCapability(ForgeCapabilities.ITEM_HANDLER)
					.resolve().ifPresent(e -> {
						for (int i = 9; i < 36; i++) {
							ItemStack inv_stack = player.getInventory().items.get(i);
							if (matches(stack, inv_stack)) {
								int finalI = i;
								queue.add(new Holder<>(
										() -> e.getStackInSlot(finalI),
										() -> e.extractItem(finalI, 1, false)));
							}
						}
					});
			int moved = add(list, queue);
			ContentTransfer.onCollect(player, moved, stack);
		}
		setContent(stack, list);
		return InteractionResultHolder.success(stack);
	}

	public abstract boolean matches(ItemStack self, ItemStack stack);

	@Override
	public int getRowSize() {
		return 8;
	}

	@Override
	public int getInvSize(ItemStack stack) {
		return SIZE;
	}

	@Override
	public List<ItemStack> getInvItems(ItemStack stack, Player player) {
		return getContent(stack);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return InvTooltip.get(this, stack);
	}

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		if (Screen.hasAltDown()) {
			return;
		}
		list.add(LangData.IDS.BAG_SIZE.get(getSize(stack), SIZE));
		PickupConfig.addText(stack, list);
		LangData.addInfo(list,
				LangData.Info.COLLECT_BAG,
				LangData.Info.LOAD,
				LangData.Info.EXTRACT_BAG);
		if (!Screen.hasShiftDown()) {
			list.add(LangData.Info.ALT_CONTENT.get().withStyle(ChatFormatting.GRAY));
		}
	}

	public boolean isBarVisible(ItemStack stack) {
		return getSize(stack) < SIZE;
	}

	public int getBarWidth(ItemStack stack) {
		return (int) Math.ceil(getSize(stack) * 13f / SIZE);
	}

	public int getBarColor(ItemStack stack) {
		return 0xFFFFFF;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	public int getSize(ItemStack stack) {
		NonNullList<ItemStack> list = getContent(stack);
		int ans = 0;
		for (ItemStack is : list) {
			if (!is.isEmpty()) {
				ans++;
			}
		}
		return ans;
	}

	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new BagCaps(this, stack);
	}

	private void throwOut(NonNullList<ItemStack> list, Player player, ItemStack bag) {
		int count = 0;
		for (ItemStack stack : list) {
			if (!stack.isEmpty()) {
				count += stack.getCount();
				player.getInventory().placeItemBackInInventory(stack.copy());
			}
		}
		ContentTransfer.onExtract(player, count, bag);
		list.clear();
	}

	private static int add(NonNullList<ItemStack> list, Queue<Holder<ItemStack>> toAdd) {
		int count = 0;
		for (int i = 0; i < SIZE; i++) {
			if (list.get(i).isEmpty()) {
				if (toAdd.isEmpty()) return count;
				Holder<ItemStack> item = toAdd.poll();
				list.set(i, item.getter.get().copy());
				item.remove.run();
				count++;
			}
		}
		return count;
	}

	private record Holder<T>(Supplier<T> getter, Runnable remove) {

	}


}
