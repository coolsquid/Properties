package coolsquid.properties.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public abstract class ConfigHandler<E> {

	private final List<Value> values = new ArrayList<>();
	private String currentKey;
	private E currentElement;

	@Nonnull
	protected abstract Iterable<E> getElements();

	protected abstract E getElement(String key);

	boolean check(String key, E e, Object expectedValue) {
		if (expectedValue instanceof String) {
			return this.checkString(e, key, (String) expectedValue);
		} else if (expectedValue instanceof Boolean) {
			return this.checkBoolean(e, key, (boolean) expectedValue);
		} else if (expectedValue instanceof Number) {
			return this.checkNumber(e, key, (Number) expectedValue);
		} else if (expectedValue instanceof Map) {
			return this.checkConfig(e, key, ConfigFactory.parseMap((Map<String, ? extends Object>) expectedValue));
		}
		return false;
	}

	protected boolean checkString(E e, String key, String expectedValue) {
		return false;
	}

	protected boolean checkBoolean(E e, String key, boolean expectedValue) {
		return false;
	}

	protected boolean checkNumber(E e, String key, Number expectedValue) {
		return false;
	}

	protected boolean checkConfig(E e, String key, Config expectedValue) {
		return false;
	}

	protected void handleString(E e, String key, String value) {
		throw new ConfigException("Unsupported type string for option " + key);
	}

	protected void handleBoolean(E e, String key, boolean value) {
		throw new ConfigException("Unsupported type boolean for option " + key);
	}

	protected void handleNumber(E e, String key, Number value) {
		throw new ConfigException("Unsupported type number for option " + key);
	}

	protected void handleConfig(E e, String key, Config value) {
		throw new ConfigException("Unsupported type map for option " + key);
	}

	protected final void missing(String key) {
		throw new ConfigException("Property %s was not found", key);
	}

	protected final void save(Object value) {
		if (this.currentKey == null) {
			throw new IllegalStateException();
		}
		Value container = new Value(this.currentKey, this.currentElement, value);
		if (!this.values.contains(container)) {
			this.values.add(container);
		}
	}

	protected final void saveConfig(Object... values) {
		if (this.currentKey == null) {
			throw new IllegalStateException();
		}
		Config config = ConfigFactory.empty();
		for (int i = 0; i < values.length; i += 2) {
			config = config.withValue((String) values[i], ConfigValueFactory.fromAnyRef(values[i + 1]));
		}
		this.values.add(new Value(this.currentKey, this.currentElement, config));
	}

	void handle(String key, E e, Object value) {
		this.currentKey = key;
		this.currentElement = e;
		if (value instanceof String) {
			this.handleString(e, key, (String) value);
		} else if (value instanceof Boolean) {
			this.handleBoolean(e, key, (boolean) value);
		} else if (value instanceof Number) {
			this.handleNumber(e, key, (Number) value);
		} else if (value instanceof List) {
			for (Object v : (List<?>) value) {
				this.handle(key, e, v);
			}
		} else if (value instanceof Map) {
			this.handleConfig(e, key, ConfigFactory.parseMap((Map<String, ? extends Object>) value));
		}
		this.currentKey = null;
	}

	protected void reset() {
		List<Value> values = new ArrayList<>(this.values);
		this.values.clear();
		for (Value v : values) {
			this.handle(v.key, v.e, v.value);
		}
	}

	private class Value {

		private final String key;
		private final E e;
		private final Object value;

		public Value(String key, E e, Object value) {
			this.key = key;
			this.e = e;
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getOuterType().hashCode();
			result = prime * result + (this.e == null ? 0 : this.e.hashCode());
			result = prime * result + (this.key == null ? 0 : this.key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Value other = (Value) obj;
			if (!this.getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.e == null) {
				if (other.e != null) {
					return false;
				}
			} else if (!this.e.equals(other.e)) {
				return false;
			}
			if (this.key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!this.key.equals(other.key)) {
				return false;
			}
			return true;
		}

		private ConfigHandler getOuterType() {
			return ConfigHandler.this;
		}
	}
}