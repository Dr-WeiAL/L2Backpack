package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.ScreenTracker;
import dev.xkmc.l2backpack.content.restore.ScreenType;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import dev.xkmc.l2library.util.Proxy;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class SetScreenToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public ScreenType type;

	@Deprecated
	public SetScreenToClient() {
	}

	public SetScreenToClient(ScreenType type) {
		this.type = type;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTracker.get(Proxy.getClientPlayer()).clientClear(type);
	}

}
