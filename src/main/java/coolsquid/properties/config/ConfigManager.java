package coolsquid.properties.config;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import coolsquid.properties.util.Log;
import coolsquid.properties.util.WarningHandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValue;

public class ConfigManager {

	public static final File CONFIG_DIRECTORY = new File("./config/properties");
	public static final FilenameFilter CONFIG_FILE_FILTER = (file, name) -> file.isDirectory()
			|| name.endsWith(".conf");

	private static final Map<String, ConfigHandler<?>> HANDLERS = new HashMap<>();

	private static int errorCount = 0;

	public static void load() {
		if (!CONFIG_DIRECTORY.exists()) {
			CONFIG_DIRECTORY.mkdirs();
		}
		load(CONFIG_DIRECTORY);
		if (errorCount > 0 && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			WarningHandler.registerWarning(errorCount);
		}
		errorCount = 0;
	}

	public static void load(File file) {
		if (file.isDirectory()) {
			for (File file2 : file.listFiles((f, n) -> n.endsWith(".conf") || f.isDirectory())) {
				load(file2);
			}
		} else if (CONFIG_FILE_FILTER.accept(file, file.getName())) {
			load(ConfigFactory.parseFile(file));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void load(Config config) {
		for (String key : config.root().keySet()) {
			List<? extends Config> list = config.getConfigList(key);
			ConfigHandler handler = HANDLERS.get(key);
			if (handler == null) {
				addError(config.getValue(key).origin(), "No such handler %s", key);
				continue;
			}
			for (Config entry : list) {
				String name = entry.getString("name");
				Object e = handler.getElement(name);
				if (e != null) {
					for (String key2 : entry.root().keySet()) {
						if (!"name".equals(key2)) {
							ConfigValue finalValue = entry.getValue(key2);
							//System.out.println(key2);
							//System.out.println(finalValue.valueType());
							try {
								switch (finalValue.valueType()) {
									case STRING: {
										handler.handleString(e, key2, entry.getString(key2));
										break;
									}
									case BOOLEAN: {
										handler.handleBoolean(e, key2, entry.getBoolean(key2));
										break;
									}
									case NUMBER: {
										handler.handleNumber(e, key2, entry.getNumber(key2));
										break;
									}
									case LIST: {
										handler.handleList(e, key2, entry.getConfigList(key2));
										break;
									}
									case OBJECT: {
										handler.handleConfig(e, key2, entry.getConfig(key2));
										break;
									}
									default: {
										throw new ConfigException("Unsupported type null");
									}
								}
							} catch (ConfigException e2) {
								addError(finalValue.origin(), e2.getMessage());
							}
						}
					}
				}
			}
		}
	}

	private static void addError(ConfigOrigin debug, String error, Object... args) {
		Object[] args2 = new Object[args.length + 2];
		args2[0] = debug.filename();
		args2[1] = debug.lineNumber();
		for (int i = 0; i < args.length; i++) {
			args2[i + 2] = args[i];
		}
		errorCount++;
		Log.error("Error in file %s at line %s:\n" + error, args2);
	}

	public static void registerHandler(String key, ConfigHandler<?> handler) {
		HANDLERS.put(key, handler);
	}
}