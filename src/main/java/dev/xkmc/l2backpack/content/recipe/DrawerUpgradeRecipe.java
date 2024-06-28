package dev.xkmc.l2backpack.content.recipe;

import dev.xkmc.l2backpack.content.drawer.BaseDrawerItem;
import dev.xkmc.l2backpack.init.registrate.BackpackMisc;
import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import static dev.xkmc.l2backpack.content.drawer.BaseDrawerItem.MAX_FACTOR;

public class DrawerUpgradeRecipe extends AbstractSmithingRecipe<DrawerUpgradeRecipe> {

	public DrawerUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
		super(template, base, addition, BaseDrawerItem.setStackingFactor(result, 2));
	}

	@Override
	public boolean matches(SmithingRecipeInput container, Level level) {
		if (!super.matches(container, level)) return false;
		ItemStack stack = container.getItem(1);
		return BaseDrawerItem.getStackingFactor(stack) < MAX_FACTOR;
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput container, HolderLookup.Provider access) {
		ItemStack stack = super.assemble(container, access);
		BaseDrawerItem.setStackingFactor(stack, BaseDrawerItem.getStackingFactor(stack) + 1);
		return stack;
	}

	@Override
	public Serializer<DrawerUpgradeRecipe> getSerializer() {
		return BackpackMisc.RSC_DRAWER_UPGRADE.get();
	}
}
