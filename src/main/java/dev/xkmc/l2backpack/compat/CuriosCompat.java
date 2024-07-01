package dev.xkmc.l2backpack.compat;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2menustacker.compat.CuriosTrackCompatImpl;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import dev.xkmc.l2menustacker.screen.source.SimpleSlotData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.function.Predicate;

public class CuriosCompat {

	public static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlot(LivingEntity player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getSlotImpl(player, pred);
		}
		return Optional.empty();
	}

	public static Optional<ItemStack> getRenderingSlot(LivingEntity player, Predicate<ItemStack> pred) {
		if (ModList.get().isLoaded("curios")) {
			return getRenderingSlotImpl(player, pred);
		}
		return Optional.empty();
	}

	private static Optional<Pair<ItemStack, PlayerSlot<?>>> getSlotImpl(LivingEntity player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosInventory(player);
		if (curio.isPresent()) {
			var e = curio.get().getEquippedCurios();
			for (int i = 0; i < e.getSlots(); i++) {
				ItemStack stack = e.getStackInSlot(i);
				if (pred.test(stack)) {
					return Optional.of(Pair.of(stack,
							new PlayerSlot<>(CuriosTrackCompatImpl.get().IS_CURIOS.get(),
									new SimpleSlotData(i))));
				}
			}
		}
		return Optional.empty();
	}

	private static Optional<ItemStack> getRenderingSlotImpl(LivingEntity player, Predicate<ItemStack> pred) {
		var curio = CuriosApi.getCuriosInventory(player);
		if (curio.isPresent()) {
			var e = curio.get().getCurios();
			for (var ent : e.values()) {
				if (!ent.isVisible()) continue;
				for (int i = 0; i < ent.getCosmeticStacks().getSlots(); i++) {
					ItemStack stack = ent.getCosmeticStacks().getStackInSlot(i);
					if (pred.test(stack)) {
						return Optional.of(stack);
					}
				}
				for (int i = 0; i < ent.getStacks().getSlots(); i++) {
					if (ent.getRenders().size() > i && !ent.getRenders().get(i))
						continue;
					ItemStack stack = ent.getStacks().getStackInSlot(i);
					if (pred.test(stack)) {
						return Optional.of(stack);
					}
				}

			}
		}
		return Optional.empty();
	}

}
