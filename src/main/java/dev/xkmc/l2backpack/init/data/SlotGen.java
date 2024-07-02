package dev.xkmc.l2backpack.init.data;

import dev.xkmc.l2core.serial.config.RecordDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SlotGen extends RecordDataProvider {

	public SlotGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "Curios Generator");
	}

	@Override
	public void add(BiConsumer<String, Record> map) {
	}

}
