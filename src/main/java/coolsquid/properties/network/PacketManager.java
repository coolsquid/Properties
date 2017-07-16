package coolsquid.properties.network;

import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import coolsquid.properties.Properties;
import coolsquid.properties.config.ConfigManager;
import coolsquid.properties.util.Log;

import org.apache.commons.io.FileUtils;

public class PacketManager {

	private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Properties.MODID);

	public static void load() {
		INSTANCE.registerMessage(PacketConfig.Handler.class, PacketConfig.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(PacketReset.Handler.class, PacketReset.class, 1, Side.CLIENT);
	}

	public static void sendConfigsToClient(EntityPlayer player) {
		if (ConfigManager.syncConfigs && FMLCommonHandler.instance().getSide() == Side.SERVER) {
			if (player == null) {
				Log.info("Sending configurations to %s clients",
						FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount());
				INSTANCE.sendToAll(new PacketReset());
				for (File file : ConfigManager.CONFIG_DIRECTORY.listFiles(ConfigManager.CONFIG_FILE_FILTER)) {
					try {
						INSTANCE.sendToAll(new PacketConfig(FileUtils.readFileToByteArray(file)));
					} catch (IOException e) {
						Log.error("Exception while reading file %s", file.getName());
						Log.catching(e);
					}
				}
			} else {
				Log.info("Sending configurations to player %s (%s)", player.getName(),
						player.getCachedUniqueIdString());
				INSTANCE.sendTo(new PacketReset(), (EntityPlayerMP) player);
				for (File file : ConfigManager.CONFIG_DIRECTORY.listFiles(ConfigManager.CONFIG_FILE_FILTER)) {
					try {
						INSTANCE.sendTo(new PacketConfig(FileUtils.readFileToByteArray(file)), (EntityPlayerMP) player);
					} catch (IOException e) {
						Log.error("Exception while reading file %s", file.getName());
						Log.catching(e);
					}
				}
			}
		}
	}
}