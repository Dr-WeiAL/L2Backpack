package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SerialClass
public class StorageContainer implements ContainerListener {

	public final UUID id;
	public final int color;
	public final RegistryOps<Tag> ops;

	@SerialField
	private final List<ItemStack> tag = new ArrayList<>();

	@SerialField
	long password = -1L;

	@SerialField
	boolean init = false;

	private SimpleContainer container;

	StorageContainer(UUID id, int color, HolderLookup.Provider pvd) {
		this.id = id;
		this.color = color;
		this.ops = pvd.createSerializationContext(NbtOps.INSTANCE);
	}

	public SimpleContainer get() {
		if (container == null) {
			this.container = new SimpleContainer(27);//TODO
			for (int i = 0; i < tag.size(); i++) {
				this.container.setItem(i, tag.get(i));
			}
		}
		container.addListener(this);
		return container;
	}

	@Override
	public void containerChanged(Container cont) {
		tag.clear();
		for (int i = 0; i < container.getContainerSize(); i++)
			tag.add(container.getItem(i));
	}

}
