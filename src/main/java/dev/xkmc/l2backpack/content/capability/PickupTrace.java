package dev.xkmc.l2backpack.content.capability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Stack;

public class PickupTrace {

	private final HashSet<Integer> visited = new HashSet<>();
	private final HashSet<Integer> active = new HashSet<>();
	private final Stack<Entry> layer = new Stack<>();

	public final boolean simulate;
	public final ServerLevel level;
	@Nullable
	public final ServerPlayer player;

	public PickupTrace(boolean simulate, ServerLevel level) {
		this.simulate = simulate;
		this.level = level;
		this.player = null;
	}

	public PickupTrace(boolean simulate, ServerPlayer player) {
		this.simulate = simulate;
		this.player = player;
		this.level = player.serverLevel();
	}

	public boolean push(int sig, PickupConfig mode) {
		if (visited.contains(sig)) {
			return false;
		}
		layer.push(new Entry(sig, mode));
		visited.add(sig);
		active.add(sig);
		return true;
	}

	public void pop() {
		Entry ent = layer.pop();
		active.remove(ent.sig());
	}

	public PickupConfig getMode() {
		return layer.peek().mode();
	}

	private record Entry(int sig, PickupConfig mode) {

	}

}
