package coolsquid.properties.config.handler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.ModEventHandler;
import coolsquid.properties.util.PrReflection;

import com.typesafe.config.Config;

public class ItemHandler extends ConfigHandler<Item> {

	@Override
	public Iterable<Item> getElements() {
		return Item.REGISTRY;
	}

	@Override
	protected Item getElement(String key) {
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
			case "tool_efficiency": {
				ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) e, value.floatValue(), 1);
				break;
			}
			case "attack_damage": {
				ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) e, value.floatValue(), 2);
				break;
			}
			case "attack_speed": {
				ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) e, value.floatValue(), 3);
				break;
			}
			case "eating_time": {
				PrReflection.setFinalValue(ItemFood.class, (ItemFood) e, value.intValue(), 0);
				break;
			}
			case "food_amount": {
				PrReflection.setFinalValue(ItemFood.class, (ItemFood) e, value.intValue(), 1);
				break;
			}
			case "saturation": {
				PrReflection.setFinalValue(ItemFood.class, (ItemFood) e, value.floatValue(), 2);
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	protected void handleBoolean(Item e, String key, boolean value) {
		switch (key) {
			case "wolf_food": {
				PrReflection.setFinalValue(ItemFood.class, (ItemFood) e, value, 3);
				break;
			}
			case "always_edible": {
				PrReflection.setFinalValue(ItemFood.class, (ItemFood) e, value, 3);
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
			case "potion_effect": {
				ItemFood food = (ItemFood) e;
				food.setPotionEffect(
						new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation(value.getString("potion"))),
								value.getInt("duration"), value.hasPath("amplifier") ? value.getInt("amplifier") : 0,
								value.hasPath("ambient") ? value.getBoolean("ambient") : false,
								value.hasPath("particles") ? value.getBoolean("particles") : true),
						value.hasPath("probability") ? value.getNumber("probability").floatValue() : 1);
				break;
			}
			default:
				this.missing(key);
		}
	}
}