package dev.xkmc.l2backpack.network.restore;

import dev.xkmc.l2backpack.content.restore.LayerPopType;
import dev.xkmc.l2backpack.content.restore.ScreenTracker;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import dev.xkmc.l2library.util.Proxy;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class PopLayerToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public LayerPopType type;

	@SerialClass.SerialField
	public int wid;

	@Deprecated
	public PopLayerToClient() {

	}

	public PopLayerToClient(LayerPopType type, int wid) {
		this.type = type;
		this.wid = wid;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenTracker.get(Proxy.getClientPlayer()).clientPop(type, wid);
	}

}
