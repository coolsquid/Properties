package coolsquid.properties.util;

import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;

import coolsquid.properties.config.ConfigException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class HoconUtil {

	public static NBTTagCompound createNBT(Config config) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<String, ConfigValue> e : config.root().entrySet()) {
			switch (e.getValue().valueType()) {
				case BOOLEAN: {
					nbt.setBoolean(e.getKey(), (boolean) e.getValue().unwrapped());
					break;
				}
				case NULL: {
					nbt.setTag(e.getKey(), null);
					break;
				}
				case NUMBER: {
					Number number = (Number) e.getValue().unwrapped();
					if (number instanceof Byte) {
						nbt.setByte(e.getKey(), number.byteValue());
					} else if (number instanceof Short) {
						nbt.setShort(e.getKey(), number.shortValue());
					} else if (number instanceof Integer) {
						nbt.setInteger(e.getKey(), number.intValue());
					} else if (number instanceof Long) {
						nbt.setLong(e.getKey(), number.longValue());
					} else if (number instanceof Float) {
						nbt.setFloat(e.getKey(), number.floatValue());
					} else if (number instanceof Double) {
						nbt.setDouble(e.getKey(), number.doubleValue());
					}
					break;
				}
				case OBJECT: {
					nbt.setTag(e.getKey(), createNBT(config.getConfig(e.getKey())));
					break;
				}
				case STRING: {
					nbt.setString(e.getKey(), (String) e.getValue().unwrapped());
					break;
				}
				default: {
					throw new ConfigException("Unsupported type %s", e.getValue().valueType());
				}
			}
		}
		return nbt;
	}
}