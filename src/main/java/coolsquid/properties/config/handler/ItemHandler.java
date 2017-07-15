package coolsquid.properties.config.handler;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.ModEventHandler;

import com.typesafe.config.Config;

public class ItemHandler extends ConfigHandler<Item> {

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
			case "tooltip": {
				ModEventHandler.ITEM_TOOLTIPS.put(e, value);
				break;
			}
			default:
				this.missing(key);
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
				this.missing(key);
		}
	}

	@Override
	public void handleConfig(Item e, String key, Config value) {
		switch (key) {
			case "harvest_levels": {
				// TODO make tool_class work properly
				e.setHarvestLevel(value.getString("tool_class"), value.getInt("level"));
				break;
			}
			default:
				this.missing(key);
		}
	}
}