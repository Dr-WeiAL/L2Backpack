package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.UUID;

@SerialClass
public class LBUserData {

	@SerialField
	private final StorageContainer[] dimensional = new StorageContainer[16];

	@SerialField
	protected final HashMap<Item, Integer> drawer = new HashMap<>();

	public LBUserData() {
		for (int i = 0; i < 16; i++)
			dimensional[i] = new StorageContainer();
	}

	public StorageContainer getStorage(int i, UUID uuid) {
		dimensional[i].id = uuid;
		dimensional[i].color = i;
		return dimensional[i];
	}

}
