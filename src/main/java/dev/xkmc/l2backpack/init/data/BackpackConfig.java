package dev.xkmc.l2backpack.init.data;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import static dev.xkmc.l2backpack.content.backpack.BackpackItem.MAX_ROW;

public class BackpackConfig {

	public static class Client {

		public final ModConfigSpec.BooleanValue previewOnCenter;

		public final ModConfigSpec.BooleanValue popupArrowOnSwitch;
		public final ModConfigSpec.BooleanValue popupToolOnSwitch;
		public final ModConfigSpec.BooleanValue popupArmorOnSwitch;

		public final ModConfigSpec.BooleanValue drawerAlwaysRenderFlat;

		public final ModConfigSpec.BooleanValue reverseScroll;
		public final ModConfigSpec.BooleanValue backpackInsertRequiresShift;
		public final ModConfigSpec.BooleanValue backpackEnableLeftClickInsert;
		public final ModConfigSpec.BooleanValue backpackEnableRightClickInsert;


		Client(ModConfigSpec.Builder builder) {
			previewOnCenter = builder.comment("Put quiver preview near the center of the screen, rather than edge of the screen")
					.define("previewOnCenter", true);

			popupArrowOnSwitch = builder.comment("Show arrow quick swap when switching to a bow")
					.define("popupArrowOnSwitch", true);

			popupToolOnSwitch = builder.comment("Show tool quick swap when switching to a tool")
					.define("popupToolOnSwitch", false);

			popupArmorOnSwitch = builder.comment("Show armor quick swap when switching to empty hand")
					.define("popupArmorOnSwitch", false);

			reverseScroll = builder.comment("Reverse scrolling direction for quick swap")
					.define("reverseScroll", false);

			backpackInsertRequiresShift = builder.comment("Backpack inventory quick insert requires shift click")
					.define("backpackInsertRequiresShift", false);

			backpackEnableLeftClickInsert = builder.comment("Backpack inventory quick insert allows left click insert")
					.define("backpackEnableLeftClickInsert", true);

			backpackEnableRightClickInsert = builder.comment("Backpack inventory quick insert allows right click insert")
					.define("backpackEnableRightClickInsert", true);

			drawerAlwaysRenderFlat = builder.comment("Draws Always render content directly")
					.define("drawerAlwaysRenderFlat", false);

		}

		public boolean allowBackpackInsert(int button) {
			if (backpackInsertRequiresShift.get()) {
				if (!Screen.hasShiftDown())
					return false;
			}
			boolean allow = false;
			if (backpackEnableLeftClickInsert.get()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
					allow = true;
			}
			if (backpackEnableRightClickInsert.get()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
					allow = true;
			}
			return allow;
		}

	}

	public static class Server {

		public final ModConfigSpec.IntValue initialRows;

		public final ModConfigSpec.IntValue startupBackpackCondition;

		Server(ModConfigSpec.Builder builder) {
			initialRows = builder.comment("Initial Rows (x9 slots) for backpack")
					.defineInRange("initialRows", 2, 1, MAX_ROW);
			startupBackpackCondition = builder.comment("How many items do players need to spawn with to have the privilege of having them in a backpack")
					.defineInRange("startupBackpackCondition", 6, 1, 36);
		}
	}

	public static final ModConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	public static final ModConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		final Pair<Client, ModConfigSpec> client = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = client.getRight();
		CLIENT = client.getLeft();

		final Pair<Server, ModConfigSpec> server = new ModConfigSpec.Builder().configure(Server::new);
		SERVER_SPEC = server.getRight();
		SERVER = server.getLeft();
	}

	public static void init() {
		register(ModConfig.Type.CLIENT, CLIENT_SPEC);
		register(ModConfig.Type.SERVER, SERVER_SPEC);
	}

	private static void register(ModConfig.Type type, IConfigSpec<?> spec) {
		var mod = ModLoadingContext.get().getActiveContainer();
		String path = "l2_configs/" + mod.getModId() + "-" + type.extension() + ".toml";
		mod.registerConfig(type, spec, path);
	}

}
