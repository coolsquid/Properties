package coolsquid.properties;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import coolsquid.properties.config.ConfigManager;
import coolsquid.properties.config.handler.BlockHandler;
import coolsquid.properties.config.handler.MobHandler;
import coolsquid.properties.config.handler.ItemHandler;
import coolsquid.properties.util.ModEventHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Properties.MODID, name = Properties.NAME, version = Properties.VERSION, dependencies = Properties.DEPENDENCIES, updateJSON = Properties.UPDATE_JSON)
public class Properties {

	public static final String MODID = "properties";
	public static final String NAME = "Properties";
	public static final String VERSION = "1.0.0";
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

		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		ConfigManager.load();
	}
}