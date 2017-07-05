package coolsquid.properties.config;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.typesafe.config.Config;

public class ItemHandler implements ConfigHandler<Item> {

	@Override
	public Item getElement(String key) {
		return Item.REGISTRY.getObject(new ResourceLocation(key));
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
		}
	}
}