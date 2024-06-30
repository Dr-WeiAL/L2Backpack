package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.registrate.LBTriggers;
import dev.xkmc.l2backpack.network.DrawerInteractToServer;
import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class DrawerInteractTrigger extends BaseCriterion<DrawerInteractTrigger.Ins, DrawerInteractTrigger> {

	public static Ins fromType(DrawerInteractToServer.Type type) {
		Ins ans = new Ins(LBTriggers.DRAWER.get());
		ans.type = type;
		return ans;
	}

	public DrawerInteractTrigger() {
		super(Ins.class);
	}

	public void trigger(ServerPlayer player, DrawerInteractToServer.Type type) {
		this.trigger(player, e -> (e.type == null || e.type == type));
	}

	@SerialClass
	public static class Ins extends BaseCriterionInstance<Ins, DrawerInteractTrigger> {

		@Nullable
		@SerialField
		private DrawerInteractToServer.Type type;

		protected Ins(DrawerInteractTrigger drawerInteractTrigger) {
			super(drawerInteractTrigger);
		}
	}

}
