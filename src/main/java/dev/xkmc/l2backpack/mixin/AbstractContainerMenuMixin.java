package dev.xkmc.l2backpack.mixin;

import dev.xkmc.l2backpack.content.doubleclick.DoubleClickHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

	@Shadow
	@Final
	public NonNullList<Slot> slots;

	@Shadow
	public abstract ItemStack getCarried();

	@Inject(method = "doClick", at = @At(value = "HEAD"), cancellable = true)
	public void l2backpack$canItemQuickReplace(int slot, int btn, ClickType type, Player player, CallbackInfo ci) {
		AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
		if (type == ClickType.PICKUP_ALL && slot >= 0) {
			if (DoubleClickHandler.handle(self, slots.get(slot), getCarried(), player, btn)) {
				ci.cancel();
			}
		}
	}

}
