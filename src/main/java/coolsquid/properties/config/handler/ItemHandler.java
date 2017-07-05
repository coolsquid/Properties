package coolsquid.properties.config.handler;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigException;
import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;

import com.typesafe.config.Config;

public class ItemHandler implements ConfigHandler<Item> {

	@Override
	public Item getElement(String key) {
		return Item.REGISTRY.getObject(new ResourceLocation(key));
	}

	@Override
	public void handleString(Item e, String key, String value) {
		switch (key) {
			case "creative_tab": {
				e.setCreativeTab(ConfigUtil.getCreativeTab(value));
				break;
			}
			case "localization_key": {
				e.setUnlocalizedName(value);
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleNumber(Item e, String key, Number value) {
		switch (key) {
			case "max_damage": {
				e.setMaxDamage(value.intValue());
				break;
			}
			case "stack_size": {
				e.setMaxStackSize(value.intValue());
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleList(Item e, String key, List<? extends Config> value) {
		switch (key) {
			case "harvest_levels": {
				for (Config config : value) {
					// TODO make tool_class work
					e.setHarvestLevel(config.getString("tool_class"), config.getInt("level"));
				}
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}
}