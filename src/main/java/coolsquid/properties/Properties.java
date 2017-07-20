package coolsquid.properties;

import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import coolsquid.properties.config.ConfigManager;
import coolsquid.properties.config.handler.BiomeHandler;
import coolsquid.properties.config.handler.BlockHandler;
import coolsquid.properties.config.handler.ItemHandler;
import coolsquid.properties.config.handler.MobHandler;
import coolsquid.properties.network.PacketConfig;
import coolsquid.properties.network.PacketManager;
import coolsquid.properties.util.CommandProperties;
import coolsquid.properties.util.ModEventHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Properties.MODID, name = Properties.NAME, version = Properties.VERSION, dependencies = Properties.DEPENDENCIES, updateJSON = Properties.UPDATE_JSON)
public class Properties {

	public static final String MODID = "properties";
	public static final String NAME = "Properties";
	public static final String VERSION = "0.0.1";
	public static final String DEPENDENCIES = "";
	public static final String UPDATE_JSON = "";

	public static final Logger LOG = LogManager.getLogger(NAME);

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {

	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		ConfigManager.registerHandler("blocks", new BlockHandler());
		ConfigManager.registerHandler("items", new ItemHandler());
		ConfigManager.registerHandler("mobs", new MobHandler());
		ConfigManager.registerHandler("biomes", new BiomeHandler());

		MinecraftForge.EVENT_BUS.register(this);
		Object handler = new ModEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		MinecraftForge.TERRAIN_GEN_BUS.register(handler);

		PacketManager.load();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		ConfigManager.load();
	}

	@Mod.EventHandler
	public void onInit(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandProperties());
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerLogIn(PlayerLoggedInEvent event) {
		PacketManager.sendConfigsToClient(event.player);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent event) {
		if (PacketConfig.Handler.hasServerConfigs()) {
			ConfigManager.load();
		}
	}

	@NetworkCheckHandler
	public boolean networkCheck(Map<String, String> versionMap, Side remote) {
		if (remote == Side.CLIENT && ConfigManager.syncConfigs) {
			return VERSION.equals(versionMap.get(MODID)); // if sync is enabled, connecting clients must use the same
															// React version as the server
		}
		return true;
	}
}