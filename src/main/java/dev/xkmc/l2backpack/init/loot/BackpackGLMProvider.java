package dev.xkmc.l2backpack.init.loot;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class BackpackGLMProvider extends GlobalLootModifierProvider {

	public BackpackGLMProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> pvd) {
		super(gen, pvd, L2Backpack.MODID);
	}

	@Override
	protected void start() {
		Random r = new Random(12345);
		for (LootGen.LootDefinition def : LootGen.LootDefinition.values()) {
			this.add(def.id, new BackpackLootModifier(def.chance, def, r.nextLong(), LootTableIdCondition.builder(def.target).build()));
		}
	}

}
