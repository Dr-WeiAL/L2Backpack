package dev.xkmc.l2backpack.init.registrate;

import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.advancement.BagInteractTrigger;
import dev.xkmc.l2backpack.init.advancement.DrawerInteractTrigger;
import dev.xkmc.l2backpack.init.advancement.SlotClickTrigger;
import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;

public class LBTriggers {

	public static final SR<CriterionTrigger<?>> TRIGGERS = SR.of(L2Backpack.REG, BuiltInRegistries.TRIGGER_TYPES);

	public static final Val<SlotClickTrigger> SLOT_CLICK = TRIGGERS.reg("slot_click", SlotClickTrigger::new);
	public static final Val<BagInteractTrigger> INTERACT = TRIGGERS.reg("bag_interact", BagInteractTrigger::new);
	public static final Val<DrawerInteractTrigger> DRAWER = TRIGGERS.reg("drawer_interact", DrawerInteractTrigger::new);

	public static final Val<PlayerTrigger> REMOTE = TRIGGERS.reg("remote_hopper", PlayerTrigger::new);
	public static final Val<PlayerTrigger> ANALOG = TRIGGERS.reg("analog_signal", PlayerTrigger::new);
	public static final Val<PlayerTrigger> SHARE = TRIGGERS.reg("shared_drive", PlayerTrigger::new);

	public static void register() {

	}

}
