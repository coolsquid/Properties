
package coolsquid.properties.config.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigException;
import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.ModEventHandler;

import com.typesafe.config.Config;

public class BlockHandler implements ConfigHandler<Block> {

	@Override
	public Block getElement(String key) {
		return Block.REGISTRY.getObject(new ResourceLocation(key));
	}

	@Override
	public void handleString(Block e, String key, String value) {
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
	public void handleBoolean(Block e, String key, boolean value) {
		switch (key) {
			case "clear_drops": {
				ModEventHandler.REMOVE_ALL_BLOCK_DROPS.add(e);
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleNumber(Block e, String key, Number value) {
		switch (key) {
			case "hardness": {
				e.setHardness(value.floatValue());
				break;
			}
			case "resistance": {
				e.setResistance(value.floatValue());
				break;
			}
			case "slipperiness": {
				if (value.floatValue() < 0.1F) {
					// causes world corruption
					throw new ConfigException("Cannot set slipperiness to less than 0.1");
				}
				e.slipperiness = value.floatValue();
				break;
			}
			case "light_level": {
				e.setLightLevel(value.floatValue());
				break;
			}
			case "light_opacity": {
				e.setLightOpacity(value.intValue());
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}

	@Override
	public void handleConfig(Block e, String key, Config value) {
		switch (key) {
			case "harvest_level": {
				for (IBlockState state : e.getBlockState().getValidStates()) {
					// TODO make tool_class work
					e.setHarvestLevel(value.getString("tool_class"), value.getInt("level"), state);
				}
				break;
			}
			case "drops": {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(value.getString("item")));
				if (value.hasPath("remove") && value.getBoolean("remove")) {
					ModEventHandler.REMOVE_BLOCK_DROPS.get(e).add(item);
					return;
				}
				int amount = value.hasPath("amount") ? value.getInt("amount") : 1;
				int meta = value.hasPath("meta") ? value.getInt("meta") : 0;
				NBTTagCompound nbt = value.hasPath("nbt") ? ConfigUtil.createNBT(value.getConfig("nbt")) : null;
				ModEventHandler.BLOCK_DROPS.get(e).add(new ItemStack(item, amount, meta, nbt));
				break;
			}
			default:
				throw new ConfigException("Property %s was not found", key);
		}
	}
}