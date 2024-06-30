package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackItem;
import dev.xkmc.l2backpack.content.bag.BookBag;
import dev.xkmc.l2backpack.content.bag.EquipmentBag;
import dev.xkmc.l2backpack.content.drawer.DrawerItem;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSetSwap;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSwap;
import dev.xkmc.l2backpack.content.quickswap.quiver.Quiver;
import dev.xkmc.l2backpack.content.quickswap.scabbard.Scabbard;
import dev.xkmc.l2backpack.content.remote.drawer.EnderDrawerItem;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.content.tool.DestroyTweakerTool;
import dev.xkmc.l2backpack.content.tool.PickupTweakerTool;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.TagGen;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

@SuppressWarnings({"unchecked", "unsafe"})
@MethodsReturnNonnullByDefault
public class BackpackItems {

	public static final SimpleEntry<CreativeModeTab> TAB = REGISTRATE
			.buildL2CreativeTab("backpack", "L2 Backpack",
					e -> e.icon(BackpackItems.ENDER_BACKPACK::asStack));

	// -------- common --------
	public static final ItemEntry<BackpackItem>[] BACKPACKS;
	public static final ItemEntry<WorldChestItem>[] DIMENSIONAL_STORAGE;
	public static final ItemEntry<EnderBackpackItem> ENDER_BACKPACK;
	public static final ItemEntry<Item> ENDER_POCKET;
	public static final ItemEntry<PickupTweakerTool> PICKUP_TWEAKER;
	public static final ItemEntry<DestroyTweakerTool> DESTROY_TWEAKER;

	public static final ItemEntry<EquipmentBag> ARMOR_BAG;
	public static final ItemEntry<BookBag> BOOK_BAG;
	public static final ItemEntry<Quiver> QUIVER;
	public static final ItemEntry<Scabbard> SCABBARD;
	public static final ItemEntry<ArmorSwap> ARMOR_SWAP;
	public static final ItemEntry<ArmorSetSwap> SUIT_SWAP;

	public static final ItemEntry<DrawerItem> DRAWER;
	public static final ItemEntry<EnderDrawerItem> ENDER_DRAWER;


	static {
		TagKey<Item> curios_tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "back"));
		// Backpacks
		{
			BACKPACKS = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				BACKPACKS[i] = REGISTRATE.item("backpack_" + color.getName(), p -> new BackpackItem(color, p))
						.tag(TagGen.BACKPACKS, curios_tag)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_backpack"))
						.register();
			}
			DIMENSIONAL_STORAGE = new ItemEntry[16];
			for (int i = 0; i < 16; i++) {
				DyeColor color = DyeColor.values()[i];
				DIMENSIONAL_STORAGE[i] = REGISTRATE.item("dimensional_storage_" + color.getName(), p -> new WorldChestItem(color, p))
						.tag(TagGen.DIMENSIONAL_STORAGES, curios_tag)
						.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
								new ModelFile.UncheckedModelFile("builtin/entity")))
						.lang(RegistrateLangProvider.toEnglishName(color.getName() + "_dimensional_backpack")).register();
			}
			ENDER_BACKPACK = REGISTRATE.item("ender_backpack", EnderBackpackItem::new)
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(curios_tag, TagGen.ENDER_CHEST).defaultLang().register();

			ENDER_POCKET = simpleItem("ender_pocket");

			PICKUP_TWEAKER = REGISTRATE.item("pickup_tweaker_tool", p -> new PickupTweakerTool(p.stacksTo(1)))
					.defaultModel().defaultLang().register();
			DESTROY_TWEAKER = REGISTRATE.item("destroy_tweaker_tool", p -> new DestroyTweakerTool(p.stacksTo(1)))
					.defaultModel().defaultLang().register();

			ARMOR_BAG = REGISTRATE.item("armor_bag", EquipmentBag::new).tag(TagGen.BAGS)
					.model((ctx, pvd) -> pvd.generated(ctx).override()
							.predicate(L2Backpack.loc("fill"), 1)
							.model(pvd.getBuilder(ctx.getName() + "_filled")
									.parent(new ModelFile.UncheckedModelFile("item/generated"))
									.texture("layer0", pvd.modLoc("item/" + ctx.getName() + "_filled"))))
					.lang("Equipment Bag").register();
			BOOK_BAG = REGISTRATE.item("book_bag", BookBag::new).tag(TagGen.BAGS)
					.model((ctx, pvd) -> pvd.generated(ctx).override()
							.predicate(L2Backpack.loc("fill"), 1)
							.model(pvd.getBuilder(ctx.getName() + "_filled")
									.parent(new ModelFile.UncheckedModelFile("item/generated"))
									.texture("layer0", pvd.modLoc("item/" + ctx.getName() + "_filled"))))
					.defaultLang().register();
			QUIVER = REGISTRATE.item("arrow_bag", Quiver::new).model(BackpackItems::createArrowBagModel)
					.tag(curios_tag, TagGen.SWAPS).lang("Quiver").register();
			SCABBARD = REGISTRATE.item("tool_swap", Scabbard::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();
			ARMOR_SWAP = REGISTRATE.item("armor_swap", ArmorSwap::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();
			SUIT_SWAP = REGISTRATE.item("suit_swap", ArmorSetSwap::new).defaultModel().tag(curios_tag, TagGen.SWAPS).defaultLang().register();

			DRAWER = REGISTRATE.item("drawer", p -> new DrawerItem(BackpackBlocks.DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.DRAWERS).defaultLang().register();

			ENDER_DRAWER = REGISTRATE.item("ender_drawer", p -> new EnderDrawerItem(BackpackBlocks.ENDER_DRAWER.get(), p))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(
							new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.DRAWERS).defaultLang().register();
		}
	}

	public static <T extends Quiver> void createArrowBagModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd) {
		ItemModelBuilder builder = pvd.withExistingParent(ctx.getName(), "generated");
		builder.texture("layer0", "item/" + ctx.getName() + "_0");
		for (int i = 1; i < 4; i++) {
			String name = ctx.getName() + "_" + i;
			ItemModelBuilder ret = pvd.withExistingParent(name, "generated");
			ret.texture("layer0", "item/" + name);
			ItemModelBuilder.OverrideBuilder override = builder.override();
			override.predicate(L2Backpack.loc("arrow"), i);
			override.model(new ModelFile.UncheckedModelFile(L2Backpack.MODID + ":item/" + name));
		}
	}

	public static void register() {
	}

	public static ItemEntry<Item> simpleItem(String id) {
		return REGISTRATE.item(id, Item::new).defaultModel().defaultLang().register();
	}

}
