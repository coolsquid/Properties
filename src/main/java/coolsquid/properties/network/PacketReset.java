package coolsquid.properties.network;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import coolsquid.properties.config.ConfigManager;

import io.netty.buffer.ByteBuf;

public class PacketReset implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<PacketReset, IMessage> {

		@Override
		public IMessage onMessage(PacketReset message, MessageContext ctx) {
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				ConfigManager.reset();
			}
			return null;
		}
	}
}