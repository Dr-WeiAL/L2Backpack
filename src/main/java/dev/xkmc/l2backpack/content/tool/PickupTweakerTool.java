package dev.xkmc.l2backpack.content.tool;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.init.data.LangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PickupTweakerTool extends TweakerTool {

	public PickupTweakerTool(Properties properties) {
		super(properties);
	}

	@Override
	public void click(ItemStack stack) {
		PickupConfig.iterateMode(stack);
	}

	@Override
	public PickupConfig click(PickupConfig config) {
		return config.iterateMode();
	}

	@Override
	public Component message(PickupConfig config) {
		return config.pickup().getTooltip();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		LangData.addInfo(list, LangData.Info.PICKUP_TWEAKER, LangData.Info.TWEAKER_BACK, LangData.Info.TWEAKER_BLOCK);
	}

}
