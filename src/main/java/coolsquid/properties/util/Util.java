package coolsquid.properties.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import coolsquid.properties.config.ConfigException;

import com.typesafe.config.Config;

public class Util {

	public static IBlockState getBlock(Object obj) {
		if (obj instanceof String) {
			return Block.REGISTRY.getObject(new ResourceLocation((String) obj)).getDefaultState();
		} else if (obj instanceof Config) {
			if (((Config) obj).hasPath("meta")) {
				return Block.REGISTRY.getObject(new ResourceLocation(((Config) obj).getString("block")))
						.getStateFromMeta(((Config) obj).getInt("meta"));
			} else {
				return Block.REGISTRY.getObject(new ResourceLocation(((Config) obj).getString("block")))
						.getDefaultState();
			}
		}
		throw new ConfigException("Unrecognized parameter type: %s", obj.getClass());
	}
}