package coolsquid.properties.config.handler;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.EntityData;

import com.google.common.collect.Iterables;
import com.typesafe.config.Config;

public class MobHandler extends ConfigHandler<Class<? extends EntityLivingBase>> {

	@Override
	protected void reset() {
		EntityData.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Class<? extends EntityLivingBase>> getElements() {
		return Iterables.transform(EntityList.getEntityNameList(),
				(n) -> (Class<? extends EntityLivingBase>) EntityList.getClass(n));
	}

	@Override
	protected Class<? extends EntityLivingBase> getElement(String key) {
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
				this.missing(key);
		}
	}

	@Override
	public void handleBoolean(Class<? extends EntityLivingBase> e, String key, boolean value) {
		switch (key) {
			case "clear_drops": {
				EntityData.getEntityData(e).clearDrops = value;
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	public void handleConfig(Class<? extends EntityLivingBase> e, String key, Config value) {
		switch (key) {
			case "drops": {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(value.getString("item")));
				if (value.hasPath("remove") && value.getBoolean("remove")) {
					EntityData.getEntityData(e).dropsToRemove.add(item);
					return;
				}
				int amount = value.hasPath("amount") ? value.getInt("amount") : 1;
				int meta = value.hasPath("meta") ? value.getInt("meta") : 0;
				NBTTagCompound nbt = value.hasPath("nbt") ? ConfigUtil.createNBT(value.getConfig("nbt")) : null;
				EntityData.getEntityData(e).dropsToAdd.add(new ItemStack(item, amount, meta, nbt));
				break;
			}
			default:
				this.missing(key);
		}
	}
}