package dev.xkmc.l2backpack.content.remote.worldchest;

import dev.xkmc.l2backpack.content.capability.PickupBagItem;
import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.BackpackModelItem;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.insert.InsertOnlyItem;
import dev.xkmc.l2backpack.content.remote.common.StorageContainer;
import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.render.BaseItemRenderer;
import dev.xkmc.l2backpack.init.L2Backpack;
import dev.xkmc.l2backpack.init.data.LangData;
import dev.xkmc.l2backpack.init.registrate.BackpackBlocks;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class WorldChestItem extends BlockItem implements BackpackModelItem, PickupBagItem, InsertOnlyItem {

	public static Optional<UUID> getOwner(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("owner_id")) {
				return Optional.of(tag.getUUID("owner_id"));
			}
		}
		return Optional.empty();
	}

	public static Component getName(String name) {
		if (name.startsWith(L2Backpack.MODID + ".names.")) {
			return Component.translatable(name).withStyle(ChatFormatting.GOLD);
		}
		return Component.literal(name);
	}

	public static ItemStack initLootGen(ItemStack stack, UUID uuid, String name, DyeColor color, ResourceLocation loot) {
		var ctag = stack.getOrCreateTag();
		ctag.putUUID("owner_id", uuid);
		ctag.putString("owner_name", name);
		ctag.putLong("password", color.getId());
		ctag.putString("loot", loot.toString());
		return stack;
	}

	public final DyeColor color;

	public WorldChestItem(DyeColor color, Properties props) {
		super(BackpackBlocks.WORLD_CHEST.get(), props.stacksTo(1).fireResistant());
		this.color = color;
	}

	void refresh(ItemStack stack, Player player) {
		var ctag = stack.getOrCreateTag();
		if (!ctag.contains("owner_id")) {
			ctag.putUUID("owner_id", player.getUUID());
			ctag.putString("owner_name", player.getName().getString());
			ctag.putLong("password", color.getId());
		}
		if (ctag.contains("loot")) {
			new WorldChestMenuPvd((ServerPlayer) player, stack, this).getContainer((ServerLevel) player.level());
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			new WorldChestMenuPvd((ServerPlayer) player, stack, this).open();
		} else {
			ContentTransfer.playSound(player);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null)
			refresh(context.getItemInHand(), context.getPlayer());
		if (context.getPlayer() != null && !context.getPlayer().isCrouching()) {
			ItemStack stack = context.getItemInHand();
			if (!context.getLevel().isClientSide()) {
				new WorldChestMenuPvd((ServerPlayer) context.getPlayer(), stack, this).open();
			} else {
				ContentTransfer.playSound(context.getPlayer());
			}
			return InteractionResult.SUCCESS;
		}
		if (!context.getItemInHand().getOrCreateTag().contains("owner_id"))
			return InteractionResult.FAIL;
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			if (tag.contains("owner_name")) {
				String name = tag.getString("owner_name");
				list.add(LangData.IDS.STORAGE_OWNER.get(getName(name)));
				PickupConfig.addText(stack, list);
			}
			if (tag.contains("loot")) {
				list.add(LangData.IDS.LOOT.get().withStyle(ChatFormatting.AQUA));
			}
		}
		LangData.addInfo(list, LangData.Info.QUICK_ANY_ACCESS,
				LangData.Info.PLACE,
				LangData.Info.DIMENSIONAL,
				LangData.Info.KEYBIND,
				LangData.Info.EXIT,
				LangData.Info.PICKUP);
		LangData.altInsert(list);
	}

	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
		return armorType == EquipmentSlot.CHEST;
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack stack) {
		return new ResourceLocation(L2Backpack.MODID, "textures/block/dimensional_storage/" + color.getName() + ".png");
	}

	@ServerOnly
	public Optional<StorageContainer> getContainer(ItemStack stack, ServerLevel level) {
		if (!stack.hasTag()) return Optional.empty();
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("owner_id")) return Optional.empty();
		UUID id = tag.getUUID("owner_id");
		long pwd = tag.getLong("password");
		return WorldStorage.get(level).getOrCreateStorage(id, color.getId(), pwd, null, null, 0);
	}

	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new WorldChestCaps(stack);
	}

	@Override
	public @Nullable IItemHandler getInvCap(ItemStack stack, ServerPlayer player) {
		var opt = getContainer(stack, player.serverLevel());
		if (opt.isPresent()) {
			var storage = opt.get();
			return new WorldChestInvWrapper(storage.container, storage.id);
		}
		return null;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(BaseItemRenderer.EXTENSIONS);
	}

}
