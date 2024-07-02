package dev.xkmc.l2backpack.content.remote.common;

import dev.xkmc.l2core.capability.level.BaseSavedData;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@SerialClass
public class WorldStorage extends BaseSavedData<WorldStorage> {

	private static final String ID = "l2backpack:dimensional";
	private static final Factory<WorldStorage> FACTORY = new Factory<>(WorldStorage::new, WorldStorage::new);

	public static WorldStorage get(ServerLevel level) {
		var ans = level.getDataStorage().computeIfAbsent(FACTORY, ID);
		ans.level = level;
		return ans;
	}

	@SerialField
	private final HashMap<String, CompoundTag> storage = new HashMap<>();

	private final HashMap<UUID, StorageContainer[]> cache = new HashMap<>();

	private ServerLevel level;

	private WorldStorage() {
		super(WorldStorage.class);
	}

	private WorldStorage(CompoundTag tag, HolderLookup.Provider pvd) {
		super(WorldStorage.class, tag, pvd);
	}

	public Optional<StorageContainer> getOrCreateStorage(UUID id, int color, long password,
														 @Nullable ServerPlayer player,
														 @Nullable ResourceLocation loot,
														 long seed) {
		if (cache.containsKey(id)) {
			StorageContainer storage = cache.get(id)[color];
			if (storage != null) {
				if (storage.password == password)
					return Optional.of(storage);
				return Optional.empty();
			}
		}
		CompoundTag col = getColor(id, color, password);
		if (col.getLong("password") != password)
			return Optional.empty();
		StorageContainer storage = new StorageContainer(id, color, col, level.registryAccess());
		if (loot != null) {
			LootTable loottable = level.getServer().reloadableRegistries()
					.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, loot));
			LootParams.Builder builder = new LootParams.Builder(level);
			if (player != null) {
				builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
			}
			loottable.fill(storage.container, builder.create(LootContextParamSets.EMPTY), seed);
		}
		putStorage(id, color, storage);
		return Optional.of(storage);
	}

	public Optional<StorageContainer> getStorageWithoutPassword(UUID id, int color) {
		if (cache.containsKey(id)) {
			StorageContainer storage = cache.get(id)[color];
			if (storage != null) {
				return Optional.of(storage);
			}
		}
		Optional<CompoundTag> colOptional = getColorWithoutPassword(id, color);
		if (colOptional.isEmpty()) {
			return Optional.empty();
		}
		StorageContainer storage = new StorageContainer(id, color, colOptional.get(), level.registryAccess());
		putStorage(id, color, storage);
		return Optional.of(storage);
	}

	public StorageContainer changePassword(UUID id, int color, long password) {
		cache.remove(id);
		CompoundTag col = getColor(id, color, password);
		col.putLong("password", password);
		StorageContainer storage = new StorageContainer(id, color, col, level.registryAccess());
		putStorage(id, color, storage);
		return storage;
	}

	private void putStorage(UUID id, int color, StorageContainer storage) {
		StorageContainer[] arr;
		if (cache.containsKey(id))
			arr = cache.get(id);
		else cache.put(id, arr = new StorageContainer[16]);
		arr[color] = storage;
	}

	private CompoundTag getColor(UUID id, int color, long password) {
		CompoundTag ans;
		String sid = id.toString();
		if (!storage.containsKey(sid)) {
			storage.put(sid, ans = new CompoundTag());
			ans.putUUID("owner_id", id);
		} else ans = storage.get(sid);
		CompoundTag col;
		if (ans.contains("color_" + color)) {
			col = ans.getCompound("color_" + color);
		} else {
			col = new CompoundTag();
			col.putLong("password", password);
			ans.put("color_" + color, col);
		}
		return col;
	}

	private Optional<CompoundTag> getColorWithoutPassword(UUID id, int color) {
		CompoundTag ans;
		String sid = id.toString();
		if (!storage.containsKey(sid)) {
			return Optional.empty();
		} else ans = storage.get(sid);
		CompoundTag col;
		if (ans.contains("color_" + color)) {
			col = ans.getCompound("color_" + color);
		} else {
			return Optional.empty();
		}
		return Optional.of(col);
	}

	@SerialField
	final HashMap<String, HashMap<Item, Integer>> drawer = new HashMap<>();

	private final HashMap<String, HashMap<Item, EnderDrawerAccess>> drawer_cache = new HashMap<>();

	public EnderDrawerAccess getOrCreateDrawer(UUID id, Item item) {
		return drawer_cache.computeIfAbsent(id.toString(), e -> new HashMap<>())
				.computeIfAbsent(item, i -> new EnderDrawerAccess(this, id, item));
	}
}
