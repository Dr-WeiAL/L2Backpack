package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.backpack.BackpackMenu;
import dev.xkmc.l2backpack.content.backpack.BackpackScreen;
import dev.xkmc.l2backpack.content.common.BaseOpenableScreen;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorBagMenu;
import dev.xkmc.l2backpack.content.quickswap.armorswap.ArmorSetBagMenu;
import dev.xkmc.l2backpack.content.quickswap.quiver.QuiverMenu;
import dev.xkmc.l2backpack.content.quickswap.scabbard.ScabbardMenu;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestContainer;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import static dev.xkmc.l2backpack.init.L2Backpack.REGISTRATE;

/**
 * handles container menu
 */
public class LBMenu {

	public static final MenuEntry<BackpackMenu> MT_BACKPACK = REGISTRATE.menu("backpack",
					BackpackMenu::fromNetwork,
					() -> BackpackScreen::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<WorldChestContainer> MT_WORLD_CHEST = REGISTRATE.menu("dimensional_storage",
					WorldChestContainer::fromNetwork,
					() -> WorldChestScreen::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<QuiverMenu> MT_ARROW = REGISTRATE.menu("arrow_bag",
					QuiverMenu::fromNetwork,
					() -> BaseOpenableScreen<QuiverMenu>::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<ScabbardMenu> MT_TOOL = REGISTRATE.menu("tool_bag",
					ScabbardMenu::fromNetwork,
					() -> BaseOpenableScreen<ScabbardMenu>::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<ArmorBagMenu> MT_ARMOR = REGISTRATE.menu("armor_bag",
					ArmorBagMenu::fromNetwork,
					() -> BaseOpenableScreen<ArmorBagMenu>::new)
			.lang(LBMenu::getLangKey).register();

	public static final MenuEntry<ArmorSetBagMenu> MT_ARMOR_SET = REGISTRATE.menu("armor_set",
					ArmorSetBagMenu::fromNetwork,
					() -> BaseOpenableScreen<ArmorSetBagMenu>::new)
			.lang(LBMenu::getLangKey).register();

	public static void register() {

	}

	public static String getLangKey(MenuType<?> menu) {
		ResourceLocation rl = BuiltInRegistries.MENU.getKey(menu);
		assert rl != null;
		return "container." + rl.getNamespace() + "." + rl.getPath();
	}

}
