package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2backpack.init.data.LBLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public enum PickupMode {
	NONE(LBLang.IDS.MODE_NONE),
	STACKING(LBLang.IDS.MODE_STACKING),
	ALL(LBLang.IDS.MODE_ALL);

	private final LBLang.IDS lang;

	PickupMode(LBLang.IDS lang) {
		this.lang = lang;
	}

	public MutableComponent getTooltip() {
		return lang.get().withStyle(ChatFormatting.AQUA);
	}
}
