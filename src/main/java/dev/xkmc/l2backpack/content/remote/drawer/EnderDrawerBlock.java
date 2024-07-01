package dev.xkmc.l2backpack.content.remote.drawer;

import dev.xkmc.l2backpack.content.capability.PickupConfig;
import dev.xkmc.l2backpack.content.common.ContentTransfer;
import dev.xkmc.l2backpack.content.remote.common.EnderDrawerAccess;
import dev.xkmc.l2backpack.init.registrate.LBBlocks;
import dev.xkmc.l2backpack.init.registrate.LBItems;
import dev.xkmc.l2modularblock.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2modularblock.mult.UseItemOnBlockMethod;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.one.GetBlockItemBlockMethod;
import dev.xkmc.l2modularblock.one.SpecialDropBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EnderDrawerBlock implements UseItemOnBlockMethod, GetBlockItemBlockMethod, SpecialDropBlockMethod, SetPlacedByBlockMethod {

	public static final EnderDrawerBlock INSTANCE = new EnderDrawerBlock();

	public static final BlockEntityBlockMethod<EnderDrawerBlockEntity> BLOK_ENTITY =
			new EnderDrawerAnalogBlockEntity<>(LBBlocks.TE_ENDER_DRAWER, EnderDrawerBlockEntity.class);

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			if (!stack.isEmpty() && stack.isComponentsPatchEmpty() && stack.getItem() == chest.item) {
				if (!level.isClientSide()) {
					stack = new EnderDrawerItemHandler(chest.getAccess(), false).insertItem(0, stack, false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return ItemInteractionResult.SUCCESS;
			} else if (stack.isEmpty()) {
				if (!level.isClientSide()) {
					EnderDrawerAccess access = chest.getAccess();
					stack = new EnderDrawerItemHandler(access, false).extractItem(0, access.item().getDefaultMaxStackSize(), false);
					player.setItemInHand(hand, stack);
				} else {
					ContentTransfer.playDrawerSound(player);
				}
				return ItemInteractionResult.SUCCESS;
			}
			return ItemInteractionResult.FAIL;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack stack) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		UUID id = LBItems.DC_OWNER_ID.get(stack);
		Component name = LBItems.DC_OWNER_NAME.get(stack);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			chest.ownerId = id;
			chest.ownerName = name;
			chest.item = EnderDrawerItem.getItem(stack);
			chest.config = PickupConfig.get(stack);
			chest.addToListener();
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof EnderDrawerBlockEntity chest) {
			return buildStack(chest);
		}
		return LBItems.ENDER_DRAWER.asStack();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof EnderDrawerBlockEntity chest) {
			return List.of(buildStack(chest));
		}
		return List.of(LBItems.ENDER_DRAWER.asStack());
	}

	private ItemStack buildStack(EnderDrawerBlockEntity chest) {
		ItemStack stack = LBItems.ENDER_DRAWER.asStack();
		if (chest.ownerId != null) {
			stack.set(LBItems.DC_OWNER_ID, chest.ownerId);
			stack.set(LBItems.DC_OWNER_NAME, chest.ownerName);
			stack.set(LBItems.DC_PICKUP, chest.config);
			stack.set(LBItems.DC_ENDER_DRAWER_ITEM, chest.item);
		}
		return stack;
	}

}
