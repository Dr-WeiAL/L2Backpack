package dev.xkmc.l2backpack.compat;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.remote.dimensional.DimensionalItem;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;

public class GolemCompat {

	public static void register() {
		//TODO NeoForge.EVENT_BUS.register(GolemCompat.class);
	}

	private static boolean canEquip(ItemStack stack) {
		if (stack.getItem() instanceof BaseBagItem) {
			return true;
		}
		if (stack.getItem() instanceof DimensionalItem) {
			return LBItems.DC_OWNER_ID.get(stack) != null;
		}
		return false;
	}

	/* TODO
	@Nullable
	private static GenericItemStack<WorldChestItem> getBackpack(AbstractGolemEntity<?, ?> golem) {
		for (var e : List.of(EquipmentSlot.CHEST, EquipmentSlot.OFFHAND)) {
			ItemStack stack = golem.getItemBySlot(e);
			if (stack.getItem() instanceof WorldChestItem item) {
				return new GenericItemStack<>(item, stack);
			}
		}
		var opt = CuriosCompat.getSlot(golem,
				e -> e.getItem() instanceof WorldChestItem);
		if (opt.isPresent()) {
			ItemStack stack = opt.get().getFirst();
			if (stack.getItem() instanceof WorldChestItem item) {
				return new GenericItemStack<>(item, stack);
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (canEquip(event.getStack())) {
			ItemStack back = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
			if (back.isEmpty() || canEquip(back))
				event.setSlot(EquipmentSlot.CHEST, 1);
			else event.setSlot(EquipmentSlot.OFFHAND, 1);
		}
	}

	@SubscribeEvent
	public static void onHandleItem(GolemHandleItemEvent event) {
		if (event.getItem().isRemoved()) return;
		ItemStack stack = event.getItem().getItem();
		if (stack.isEmpty()) return;
		var backpack = getBackpack(event.getEntity());
		if (backpack == null) return;
		ServerLevel level = (ServerLevel) event.getEntity().level();
		var cont = backpack.item().getContainer(backpack.stack(), level);
		if (cont.isEmpty()) return;
		var storage = cont.get();
		var handler = new WorldChestInvWrapper(storage.container, storage.id);
		ItemStack remain = ItemHandlerHelper.insertItem(handler, stack, false);
		event.getItem().setItem(remain);
	}


	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onArrowFind(ArrowBagEvents.ArrowFindEvent event) {
		if (!(event.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		IQuickSwapToken<?> token = QuickSwapManager.getToken(event.getEntity(), event.getStack(), false);
		if (token != null && token.type() == QuickSwapTypes.ARROW) {
			var arrows = token.getList();
			for (int i = 0; i < 9; i++) {
				ItemStack stack = arrows.get(i).getStack();
				if (event.setProjectile(Pair.of(stack, token::shrink))) {
					return;
				}
			}
		}
		var backpack = getBackpack(golem);
		if (backpack == null) return;
		if (event.getEntity().level() instanceof ServerLevel level) {
			var cont = backpack.item().getContainer(backpack.stack(), level);
			if (cont.isEmpty()) return;
			var storage = cont.get();
			for (int i = 0; i < storage.container.getContainerSize(); i++) {
				ItemStack stack = storage.container.getItem(i);
				if (event.setProjectile(Pair.of(stack, x -> storage.container.setChanged()))) {
					return;
				}
			}
		}
	}

	 */

	public static void genBlockTag(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		// TODO pvd.addTag(MGTagGen.POTENTIAL_DST).add(LBBlocks.WORLD_CHEST.get());
	}

}
