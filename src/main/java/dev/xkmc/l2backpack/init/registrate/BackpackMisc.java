package dev.xkmc.l2backpack.init.registrate;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2backpack.content.common.BaseBagMenu;
import dev.xkmc.l2backpack.content.recipe.BackpackDyeRecipe;
import dev.xkmc.l2backpack.content.recipe.BackpackUpgradeRecipe;
import dev.xkmc.l2backpack.content.recipe.DrawerUpgradeRecipe;
import dev.xkmc.l2backpack.content.recipe.MultiSwitchCraftRecipe;
import dev.xkmc.l2backpack.content.restore.*;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.loot.BackpackLootModifier;
import dev.xkmc.l2core.init.reg.simple.CdcReg;
import dev.xkmc.l2core.init.reg.simple.CdcVal;
import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2core.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.l2core.serial.recipe.AbstractShapelessRecipe;
import dev.xkmc.l2core.serial.recipe.AbstractSmithingRecipe;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.source.ItemSource;
import dev.xkmc.l2menustacker.screen.source.MenuSourceRegistry;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import dev.xkmc.l2menustacker.screen.source.SimpleSlotData;
import dev.xkmc.l2menustacker.screen.track.ItemBasedTraceData;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import dev.xkmc.l2screentracker.screen.track.ItemBasedTraceData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public class BackpackMisc {

	private static final SR<RecipeSerializer<?>> RS = SR.of(L2Backpack.REG, BuiltInRegistries.RECIPE_SERIALIZER);

	public static final Val<AbstractShapelessRecipe.Serializer<BackpackDyeRecipe>> RSC_BAG_DYE =
			RS.reg("backpack_dye", () -> new AbstractShapelessRecipe.Serializer<>(BackpackDyeRecipe::new));
	public static final Val<AbstractSmithingRecipe.Serializer<BackpackUpgradeRecipe>> RSC_BAG_UPGRADE =
			RS.reg("backpack_upgrade", () -> new AbstractSmithingRecipe.Serializer<>(BackpackUpgradeRecipe::new));
	public static final Val<AbstractSmithingRecipe.Serializer<DrawerUpgradeRecipe>> RSC_DRAWER_UPGRADE =
			RS.reg("drawer_upgrade", () -> new AbstractSmithingRecipe.Serializer<>(DrawerUpgradeRecipe::new));
	public static final Val<AbstractShapedRecipe.Serializer<MultiSwitchCraftRecipe>> RSC_BAG_CRAFT =
			RS.reg("multiswitch_craft", () -> new AbstractShapedRecipe.Serializer<>(MultiSwitchCraftRecipe::new));

	private static final CdcReg<IGlobalLootModifier> GLM = CdcReg.of(L2Backpack.REG, NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

	public static final CdcVal<BackpackLootModifier> SER = GLM.reg("main", BackpackLootModifier.MAP_CODEC);

	private static final SR<ItemSource<?>> SOURCE = SR.of(L2Backpack.REG, L2MSReg.ITEM_SOURCE.key());
	private static final SR<TrackedEntryType<?>> TRACKED = SR.of(L2Backpack.REG, L2MSReg.TRACKED_ENTRY_TYPE.key());
	public static final Val<DimensionItemSource> IS_DIM = SOURCE.reg("dimension", DimensionItemSource::new);
	public static final Val<DimensionTrace> TE_DIM = TRACKED.reg("dimension", DimensionTrace::new);
	public static final Val<BackpackTrace> TE_BAG = TRACKED.reg("backpack", BackpackTrace::new);

	public static void register() {
	}

	public static void commonSetup() {
		MenuSourceRegistry.register(BackpackMenus.MT_ES.get(), (menu, slot, index, wid) ->
				index >= 36 && index < 63 ?
						Optional.of(new PlayerSlot<>(L2MSReg.IS_ENDER.get(), new SimpleSlotData(index - 36))) :
						Optional.empty());

		MenuSourceRegistry.register(BackpackMenus.MT_WORLD_CHEST.get(), (menu, slot, index, wid) ->
				Optional.of(new PlayerSlot<>(IS_DIM.get(),
						new DimensionSourceData(menu.getColor(), index - 36, menu.getOwner()))));

		MenuTraceRegistry.register(BackpackMenus.MT_WORLD_CHEST.get(), menu ->
				Optional.of(TrackedEntry.of(TE_DIM.get(),
						new DimensionTraceData(menu.getColor(), menu.getOwner()))));

		addBag(BackpackMenus.MT_BACKPACK);
		addBag(BackpackMenus.MT_ARMOR);
		addBag(BackpackMenus.MT_ARROW);
		addBag(BackpackMenus.MT_TOOL);
		addBag(BackpackMenus.MT_MULTI);
		addBag(BackpackMenus.MT_ES);
	}

	private static <T extends BaseBagMenu<T>> void addBag(MenuEntry<T> type) {
		MenuTraceRegistry.register(type.get(), menu ->
				Optional.of(TrackedEntry.of(TE_BAG.get(),
						new ItemBasedTraceData(menu.item_slot,
								menu.getStack().getItem()))));
	}

}
