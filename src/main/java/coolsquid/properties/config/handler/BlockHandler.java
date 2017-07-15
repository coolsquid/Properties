package coolsquid.properties.config.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import coolsquid.properties.config.ConfigException;
import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.config.ConfigUtil;
import coolsquid.properties.util.ModEventHandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class BlockHandler extends ConfigHandler<Block> {

	@Override
	public Block getElement(String key) {
		return Block.REGISTRY.getObject(new ResourceLocation(key));
	}

	@Override
	public void handleString(Block e, String key, String value) {
		switch (key) {
			case "creative_tab": {
				this.save(e.getCreativeTabToDisplayOn().getTabLabel());
				e.setCreativeTab(ConfigUtil.getCreativeTab(value));
				break;
			}
			case "localization_key": {
				this.save(e.getUnlocalizedName());
				e.setUnlocalizedName(value);
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	public void handleBoolean(Block e, String key, boolean value) {
		switch (key) {
			case "clear_drops": {
				this.save(ModEventHandler.REMOVE_ALL_BLOCK_DROPS.contains(e));
				if (value) {
					ModEventHandler.REMOVE_ALL_BLOCK_DROPS.add(e);
				}
				break;
			}
			default:
				this.missing(key);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleNumber(Block e, String key, Number value) {
		switch (key) {
			case "hardness": {
				this.save(ReflectionHelper.getPrivateValue(Block.class, e, 11));
				e.setHardness(value.floatValue());
				break;
			}
			case "resistance": {
				this.save(ReflectionHelper.getPrivateValue(Block.class, e, 12));
				e.setResistance(value.floatValue());
				break;
			}
			case "slipperiness": {
				this.save(e.slipperiness);
				if (value.floatValue() < 0.1F) {
					// causes world corruption
					throw new ConfigException("Cannot set slipperiness to less than 0.1");
				}
				e.slipperiness = value.floatValue();
				break;
			}
			case "light_level": {
				this.save(e.getLightValue(e.getDefaultState()));
				e.setLightLevel(value.floatValue());
				break;
			}
			case "light_opacity": {
				this.save(e.getLightOpacity(e.getDefaultState()));
				e.setLightOpacity(value.intValue());
				break;
			}
			default:
				this.missing(key);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleConfig(Block e, String key, Config value) {
		switch (key) {
			case "harvest_level": {
				this.saveConfig("tool_class", e.getHarvestTool(e.getDefaultState()), "level",
						e.getHarvestLevel(e.getDefaultState()));
				for (IBlockState state : e.getBlockState().getValidStates()) {
					// TODO make tool_class work
					e.setHarvestLevel(value.getString("tool_class"), value.getInt("level"), state);
				}
				break;
			}
			case "drops": {
				this.save(null);
				if (value == null) {
					ModEventHandler.REMOVE_BLOCK_DROPS.get(e).clear();
					ModEventHandler.BLOCK_DROPS.get(e).clear();
					return;
				}
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
			case "flammability": {
				this.save(ConfigFactory.empty()
						.withValue("encouragement", ConfigValueFactory.fromAnyRef(Blocks.FIRE.getEncouragement(e)))
						.withValue("flammability", ConfigValueFactory.fromAnyRef(Blocks.FIRE.getFlammability(e))));
				Blocks.FIRE.setFireInfo(e, value.getInt("encouragement"), value.getInt("flammability"));
				break;
			}
			default:
				this.missing(key);
		}
	}
}