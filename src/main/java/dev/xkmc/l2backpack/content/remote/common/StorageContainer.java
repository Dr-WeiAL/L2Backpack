package dev.xkmc.l2backpack.content.remote.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class StorageContainer implements ContainerListener {

	private final CompoundTag tag;

	final long password;

	public final UUID id;
	public final SimpleContainer container;
	public final int color;
	public final RegistryOps<Tag> ops;

	StorageContainer(UUID id, int color, CompoundTag tag, HolderLookup.Provider pvd) {
		this.tag = tag;
		this.id = id;
		this.color = color;
		this.password = tag.getLong("password");
		this.container = new SimpleContainer(27);//TODO
		this.ops = pvd.createSerializationContext(NbtOps.INSTANCE);
		if (tag.contains("container")) {
			ListTag list = tag.getList("container", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				ItemStack stack = ItemStack.CODEC.decode(ops, list.get(i)).getOrThrow().getFirst();
				this.container.setItem(i, stack);
			}
		}
		container.addListener(this);
	}

	@Override
	public void containerChanged(Container cont) {
		ListTag list = new ListTag();
		for (int i = 0; i < container.getContainerSize(); i++) {
			Tag e = ItemStack.CODEC.encodeStart(ops, container.getItem(i)).getOrThrow();
			list.add(i, e);
		}
		tag.put("container", list);
	}

	public boolean isValid() {
		return password == tag.getLong("password");
	}
}
