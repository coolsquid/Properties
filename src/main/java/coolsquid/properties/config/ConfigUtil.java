package coolsquid.properties.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.typesafe.config.Config;

public class ConfigUtil {

	public static NBTTagCompound createNBT(Config config) {
		return (NBTTagCompound) createNBT(config.root().unwrapped());
	}

	private static NBTBase createNBT(Object obj) {
		if (obj instanceof Boolean) {
			return new NBTTagByte((byte) ((boolean) obj ? 1 : 0));
		} else if (obj instanceof Number) {
			Number number = (Number) obj;
			if (number instanceof Byte) {
				return new NBTTagByte(number.byteValue());
			} else if (number instanceof Short) {
				return new NBTTagShort(number.shortValue());
			} else if (number instanceof Integer) {
				return new NBTTagInt(number.intValue());
			} else if (number instanceof Long) {
				return new NBTTagLong(number.longValue());
			} else if (number instanceof Float) {
				return new NBTTagFloat(number.floatValue());
			} else if (number instanceof Double) {
				return new NBTTagDouble(number.doubleValue());
			}
		} else if (obj instanceof String) {
			return new NBTTagString((String) obj);
		} else if (obj instanceof Map) {
			NBTTagCompound map = new NBTTagCompound();
			for (Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
				map.setTag((String) entry.getKey(), createNBT(entry.getValue()));
			}
			return map;
		} else if (obj instanceof List) {
			NBTTagList list = new NBTTagList();
			for (Object entry : (List<?>) obj) {
				list.appendTag(createNBT(entry));
			}
			return list;
		}
		return null;
	}

	@SubscribeEvent
	public static CreativeTabs getCreativeTab(String label) {
		for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
			if (label.equals(tab.getTabLabel())) {
				return tab;
			}
		}
		throw new ConfigException("Creative tab %s not found", label);
	}
}