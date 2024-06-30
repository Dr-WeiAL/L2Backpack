package dev.xkmc.l2backpack.init.data;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum BackpackKeys {
	OPEN("key.l2backpack.open", GLFW.GLFW_KEY_B);

	public final String id;
	public final int key;
	public final KeyMapping map;

	BackpackKeys(String id, int key) {
		this.id = id;
		this.key = key;
		map = new KeyMapping(id, key, "key.categories.l2mods");
	}

}
