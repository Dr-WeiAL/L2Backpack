package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.init.data.TagGen;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import dev.xkmc.l2core.serial.recipe.AbstractShapelessRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

public class BackpackDyeRecipe extends AbstractShapelessRecipe<BackpackDyeRecipe> {

	public BackpackDyeRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(group, result, ingredients);
	}

	@Override
	public ItemStack assemble(CraftingInput container, HolderLookup.Provider access) {
		ItemStack bag = ItemStack.EMPTY;
		for (int i = 0; i < container.size(); i++) {
			if (container.getItem(i).is(TagGen.BACKPACKS)) {
				bag = container.getItem(i);
			}
		}
		ItemStack stack = super.assemble(container, access);
		stack.applyComponents(bag.getComponents());
		return stack;
	}

	@Override
	public Serializer<BackpackDyeRecipe> getSerializer() {
		return LBMisc.RSC_BAG_DYE.get();
	}
}
