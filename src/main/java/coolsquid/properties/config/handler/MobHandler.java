package coolsquid.properties.config.handler;

import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigException;
import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.EntityData;

import com.typesafe.config.Config;

public class MobHandler implements ConfigHandler<Class<? extends EntityLivingBase>> {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends EntityLivingBase> getElement(String key) {
		return (Class<? extends EntityLivingBase>) EntityList.getClass(new ResourceLocation(key));
	}

	@Override
	public void handleNumber(Class<? extends EntityLivingBase> e, String key, Number value) {
		switch (key) {
			case "speed": {
				EntityData.getEntityData(e).speed = value.doubleValue();
				break;
			}
			case "max_health": {
				EntityData.getEntityData(e).maxHealth = value.doubleValue();
				break;
			}
			case "follow_range": {
				EntityData.getEntityData(e).followRange = value.doubleValue();
				break;
			}
			case "knockback_resistance": {
				EntityData.getEntityData(e).knockbackResistance = value.doubleValue();
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleBoolean(Class<? extends EntityLivingBase> e, String key, boolean value) {
		switch (key) {
			case "clear_drops": {
				EntityData.getEntityData(e).clearDrops = true;
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleList(Class<? extends EntityLivingBase> e, String key, List<? extends Config> value) {
		switch (key) {
			case "drops": {
				for (Config config : value) {
					Item item = Item.REGISTRY.getObject(new ResourceLocation(config.getString("item")));
					if (config.hasPath("remove") && config.getBoolean("remove")) {
						EntityData.getEntityData(e).dropsToRemove.add(item);
						continue;
					}
					int amount = config.hasPath("amount") ? config.getInt("amount") : 1;
					int meta = config.hasPath("meta") ? config.getInt("meta") : 0;
					NBTTagCompound nbt = config.hasPath("nbt") ? ConfigUtil.createNBT(config.getConfig("nbt")) : null;
					EntityData.getEntityData(e).dropsToAdd.add(new ItemStack(item, amount, meta, nbt));
				}
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}
}