package dev.xkmc.l2backpack.content.capability;

import dev.xkmc.l2backpack.init.data.LBLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public enum DestroyMode {
	NONE(LBLang.IDS.DESTROY_NONE, true),
	EXCESS(LBLang.IDS.DESTROY_EXCESS, true),
	MATCH(LBLang.IDS.DESTROY_MATCH, false),
	ALL(LBLang.IDS.DESTROY_ALL, false);

	private final LBLang.IDS lang;

	public final boolean attemptInsert;

	DestroyMode(LBLang.IDS lang, boolean attemptInsert) {
		this.lang = lang;
		this.attemptInsert = attemptInsert;
	}

	public MutableComponent getTooltip() {
		return lang.get().withStyle(ChatFormatting.RED);
	}

}
