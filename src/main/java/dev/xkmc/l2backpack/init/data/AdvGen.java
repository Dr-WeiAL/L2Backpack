package dev.xkmc.l2backpack.init.data;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.*;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2backpack.init.registrate.BackpackItems;
import dev.xkmc.l2backpack.init.registrate.BackpackMisc;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import dev.xkmc.l2library.serial.advancements.AdvancementGenerator;
import dev.xkmc.l2library.serial.advancements.CriterionBuilder;
import dev.xkmc.l2screentracker.screen.base.ScreenTrackerRegistry;
import dev.xkmc.l2screentracker.screen.triggers.ExitMenuTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class AdvGen {

	public static void genAdvancements(RegistrateAdvancementProvider pvd) {
		pvd.accept(Advancement.Builder.advancement().addCriterion("locate",
						PlayerTrigger.TriggerInstance.located(LocationPredicate.ANY))
				.build(new ResourceLocation(L2Backpack.MODID, "detection")));
		AdvancementGenerator gen = new AdvancementGenerator(pvd, L2Backpack.MODID);
		gen.new TabBuilder("backpacks").root("root", backpack(DyeColor.WHITE),
						CriterionBuilder.one(PlayerTrigger.TriggerInstance.tick()),
						"Welcome to L2Backpack", "Guide to Backpacks")
				.root().patchouli(L2Backpack.REGISTRATE,
						CriterionBuilder.one(PlayerTrigger.TriggerInstance.tick()),
						new ResourceLocation(L2Backpack.MODID, "backpack_guide"),
						"Intro to Backpacks", "Read the backpack guide")
				.root().create("backpack", backpack(DyeColor.RED),
						CriterionBuilder.item(TagGen.BACKPACKS),
						"Your First Backpack!", "Obtain a Backpack")
				.create("press_b", backpack(DyeColor.CYAN),
						CriterionBuilder.one(SlotClickTrigger.fromKeyBind()),
						"Keybind", "open Backpack through keybind")
				.create("quick_access", backpack(DyeColor.BLUE),
						CriterionBuilder.one(SlotClickTrigger.fromGUI()),
						"Quick Access", "open Backpack in inventory directly")
				.create("folder_structure", backpack(DyeColor.LIGHT_BLUE),
						CriterionBuilder.one(ExitMenuTrigger.exitOne()),
						"Folders?", "exit Backpack GUI and return to previous page")
				.create("close_all", backpack(DyeColor.LIME),
						CriterionBuilder.one(ExitMenuTrigger.exitAll()),
						"Close All at Once", "exit all GUIs at once using shift+esc")

				// ender backpack
				.root().enter().create("ender", BackpackItems.ENDER_BACKPACK.get(),
						CriterionBuilder.item(BackpackItems.ENDER_BACKPACK.get()),
						"Portable Ender Chest", "Obtain an Ender Backpack")
				.create("safe_storage", backpack(DyeColor.PURPLE),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(ScreenTrackerRegistry.IS_ENDER.get())),
						"GameRule KeepInventory True", "Open a Backpack in Ender Backpack")
				.create("color", backpack(DyeColor.GREEN),
						CriterionBuilder.one(InventoryChangeTrigger.TriggerInstance.hasItems(
								Arrays.stream(BackpackItems.BACKPACKS).map(e -> (ItemLike) e.get())
										.toArray(ItemLike[]::new))),
						"Colorful Inventory", "Obtain Backpacks of all colors").type(FrameType.CHALLENGE)

				//interact
				.root().enter().create("interact_load", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.LOAD)),
						"Fast Transfer", "Load things into a Drawer by shift-left clicking a chest with it")
				.create("interact_dump", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.DUMP)),
						"Dump Out", "Dump things into a chest by shift-right clicking a chest with a Drawer")

				// ender pocket
				.root().create("ender_pocket", BackpackItems.ENDER_POCKET.get(),
						CriterionBuilder.item(BackpackItems.ENDER_POCKET.get()),
						"4D Pocket", "Obtain an Ender Pocket")
				.create("upgrade", backpack(DyeColor.LIGHT_GRAY),
						CriterionBuilder.item(TagGen.BACKPACKS,
								BackpackItem.setRow(backpack(DyeColor.WHITE)
										.getDefaultInstance(), 3).getOrCreateTag()),
						"Expand the Space", "Upgrade a Backpack")
				.create("upgrade_max", backpack(DyeColor.GRAY),
						CriterionBuilder.item(TagGen.BACKPACKS,
								BackpackItem.setRow(backpack(DyeColor.WHITE)
										.getDefaultInstance(), MAX_ROW).getOrCreateTag()),
						"Maximize the Space", "Upgrade a Backpack to max level").type(FrameType.CHALLENGE)

				// dimensional backpack
				.root().enter().create("dimension", dimension(DyeColor.WHITE),
						CriterionBuilder.item(TagGen.DIMENSIONAL_STORAGES),
						"Another Ender Chest?", "Obtain a Dimensional Backpack")
				.create("dimension_recursion", dimension(DyeColor.YELLOW),
						CriterionBuilder.one(SlotClickTrigger.fromBackpack(BackpackMisc.IS_DIM.get())),
						"Infinite Recursion", "Open a Backpack in Dimensional Backpack").type(FrameType.GOAL)
				.create("dimension_hopper", dimension(DyeColor.LIGHT_GRAY),
						CriterionBuilder.one(RemoteHopperTrigger.ins()),
						"Ender Chest with Hopper", "Use Hopper to insert items into a Dimensional Backpack or an Ender Drawer").type(FrameType.GOAL)
				.create("dimension_analog", dimension(DyeColor.RED),
						CriterionBuilder.one(AnalogSignalTrigger.ins()),
						"Ender Chest with Comparator", "Use a Comparator to measure a Dimensional Backpack or an Ender Drawer").type(FrameType.CHALLENGE)

				// drawer
				.root().enter().create("drawer", BackpackItems.DRAWER.get(),
						CriterionBuilder.item(BackpackItems.DRAWER.get()),
						"Portable Drawer", "Obtain a Drawer")
				.create("drawer_store", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(DrawerInteractToServer.Type.INSERT)),
						"Is it a Stack?", "Put items into a Drawer")
				.create("drawer_take", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(DrawerInteractTrigger.fromType(DrawerInteractToServer.Type.TAKE)),
						"It is a Stack", "Take items from a Drawer")
				.create("drawer_collect", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								BackpackItems.DRAWER.get(), BackpackItems.ENDER_DRAWER.get())),
						"Bye bye Cobblestone", "Collect items into drawer by shift-right clicking with it")
				.create("drawer_extract", BackpackItems.DRAWER.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								BackpackItems.DRAWER.get(), BackpackItems.ENDER_DRAWER.get())),
						"Come Here Cobblestone", "Takes items from drawer by right clicking with it")

				// ender drawer
				.root().enter().enter().create("ender_drawer", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.item(BackpackItems.ENDER_DRAWER.get()),
						"A Third Ender Chest?", "Obtain an Ender Drawer")
				.create("ender_drawer_place", BackpackItems.ENDER_DRAWER.get(),
						CriterionBuilder.one(ItemUsedOnLocationTrigger.TriggerInstance
								.placedBlock(BackpackBlocks.ENDER_DRAWER.get())),
						"Remote Logistics", "Place down an Ender Drawer").type(FrameType.GOAL)
				.create("dimension_share", dimension(DyeColor.BLUE),
						CriterionBuilder.one(SharedDriveTrigger.ins()),
						"Shared Drive", "Open a Dimensional Backpack or use an Ender Drawer that belongs to someone else").type(FrameType.CHALLENGE)

				// bags
				.root().create("bag", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.items(BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get()),
						"Make Unstackables Stackable", "Obtain an Armor Bag or Book Bag")
				.create("bag_collect", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.COLLECT,
								BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get())),
						"Take the Loot", "Store unused weapons and tools into bag by right clicking with it")
				.create("bag_dump", BackpackItems.ARMOR_BAG.get(),
						CriterionBuilder.one(BagInteractTrigger.fromType(BagInteractTrigger.Type.EXTRACT,
								BackpackItems.ARMOR_BAG.get(), BackpackItems.BOOK_BAG.get())),
						"Throw out the Loot", "Throw out collected weapons and tools into bag by shift-right clicking with it")

				//quiver
				.root().create("quiver", BackpackItems.QUIVER.get(),
						CriterionBuilder.item(BackpackItems.QUIVER.get()),
						"9 Arrows on Bow", "Obtain a Quiver")
				.create("scabbard", BackpackItems.SCABBARD.get(),
						CriterionBuilder.item(BackpackItems.SCABBARD.get()),
						"9 Tools in One", "Obtain a Tool Swap")
				.create("armor_swap", BackpackItems.ARMOR_SWAP.get(),
						CriterionBuilder.item(BackpackItems.ARMOR_SWAP.get()),
						"Backup Armors", "Obtain an Armor Swap")
				.finish();

	}

	private static Item backpack(DyeColor color) {
		return BackpackItems.BACKPACKS[color.ordinal()].get();
	}

	private static Item dimension(DyeColor color) {
		return BackpackItems.DIMENSIONAL_STORAGE[color.ordinal()].get();
	}

}
