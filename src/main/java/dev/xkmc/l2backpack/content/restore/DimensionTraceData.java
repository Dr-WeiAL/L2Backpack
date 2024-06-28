package dev.xkmc.l2backpack.content.restore;

import dev.xkmc.l2menustacker.screen.track.TrackedEntryData;

import java.util.UUID;

public record DimensionTraceData(int color, UUID uuid)
		implements TrackedEntryData<DimensionTraceData> {
}
