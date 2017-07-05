package coolsquid.properties.config;

import java.util.List;

import com.typesafe.config.Config;

public interface ConfigHandler<E> {

	E getElement(String key);

	default void handleString(E e, String key, String value) {
		throw new ConfigException("Unsupported type string for option " + key);
	}

	default void handleBoolean(E e, String key, boolean value) {
		throw new ConfigException("Unsupported type boolean for option " + key);
	}

	default void handleNumber(E e, String key, Number value) {
		throw new ConfigException("Unsupported type number for option " + key);
	}

	default void handleList(E e, String key, List<? extends Config> value) {
		throw new ConfigException("Unsupported type list for option " + key);
	}

	default void handleConfig(E e, String key, Config value) {
		throw new ConfigException("Unsupported type map for option " + key);
	}
}