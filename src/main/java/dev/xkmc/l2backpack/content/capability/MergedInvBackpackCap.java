package dev.xkmc.l2backpack.content.capability;

import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class MergedInvBackpackCap extends InvBackpackCap<IItemHandlerModifiable> implements IItemHandlerModifiable {

	@Override
	public IItemHandlerModifiable getInv(PickupTrace trace) {
		return this;
	}

}