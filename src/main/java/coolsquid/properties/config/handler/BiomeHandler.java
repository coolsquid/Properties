package coolsquid.properties.config.handler;

import java.util.ArrayList;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import coolsquid.properties.config.ConfigHandler;
import coolsquid.properties.util.BiomeData;
import coolsquid.properties.util.PrReflection;
import coolsquid.properties.util.Util;

import com.typesafe.config.Config;

public class BiomeHandler extends ConfigHandler<Biome> {

	static {
		MapGenVillage.VILLAGE_SPAWN_BIOMES = new ArrayList<>(MapGenVillage.VILLAGE_SPAWN_BIOMES);
	}

	@Override
	protected Iterable<Biome> getElements() {
		return Biome.REGISTRY;
	}

	@Override
	protected Biome getElement(String key) {
		return Biome.REGISTRY.getObject(new ResourceLocation(key));
	}

	@Override
	protected boolean checkString(Biome e, String key, String expectedValue) {
		switch (key) {
			case "mod": {
				return expectedValue.equals(e.getRegistryName().getResourceDomain());
			}
		}
		return false;
	}

	@Override
	protected void handleNumber(Biome e, String key, Number value) {
		switch (key) {
			case "base_height": {
				this.save(e.getBaseHeight());
				PrReflection.setFinalValue(Biome.class, e, value.floatValue(), 18);
				break;
			}
			case "height_variation": {
				this.save(e.getHeightVariation());
				PrReflection.setFinalValue(Biome.class, e, value.floatValue(), 19);
				break;
			}
			case "temperature": {
				this.save(e.getTemperature());
				PrReflection.setFinalValue(Biome.class, e, value.floatValue(), 20);
				break;
			}
			case "rainfall": {
				this.save(e.getRainfall());
				PrReflection.setFinalValue(Biome.class, e, value.floatValue(), 21);
				break;
			}
			case "size": {
				BiomeData.getBiomeData(e).size = value.intValue();
				break;
			}
			case "foliage_color": {
				BiomeData.getBiomeData(e).foliageColor = value.intValue();
				break;
			}
			case "water_color": {
				BiomeData.getBiomeData(e).waterColor = value.intValue();
				break;
			}
			case "grass_color": {
				BiomeData.getBiomeData(e).grassColor = value.intValue();
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	protected void handleBoolean(Biome e, String key, boolean value) {
		switch (key) {
			case "enable_snow": {
				this.save(e.isSnowyBiome());
				PrReflection.setFinalValue(Biome.class, e, value, 23);
				break;
			}
			case "enable_rain": {
				this.save(e.canRain());
				PrReflection.setFinalValue(Biome.class, e, value, 24);
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	protected void handleConfig(Biome e, String key, Config value) {
		switch (key) {
			case "flowers": {
				e.addFlower(Util.getBlock(value), value.getInt("weight"));
				break;
			}
			case "spawns": {
				EntityRegistry.addSpawn(
						(Class<? extends EntityLiving>) EntityList
								.getClass(new ResourceLocation(value.getString("mob"))),
						value.getInt("weight"), value.getInt("min"), value.getInt("max"),
						EnumCreatureType.valueOf(value.getString("type").toUpperCase()), e);
				break;
			}
			case "top_block": {
				this.save(e.topBlock);
				e.topBlock = Util.getBlock(value);
				break;
			}
			case "filler_block": {
				this.save(e.fillerBlock);
				e.fillerBlock = Util.getBlock(value);
				break;
			}
			case "decoration": {
				for (String option : value.root().keySet()) {
					switch (option) {
						case "big_mushrooms_per_chunk": {
							e.decorator.bigMushroomsPerChunk = value.getInt("big_mushrooms_per_chunk");
							break;
						}
						case "cacti_per_chunk": {
							e.decorator.cactiPerChunk = value.getInt("cacti_per_chunk");
							break;
						}
						case "clay_per_chunk": {
							e.decorator.clayPerChunk = value.getInt("clay_per_chunk");
							break;
						}
						case "dead_bushes_per_chunk": {
							e.decorator.deadBushPerChunk = value.getInt("dead_bushes_per_chunk");
							break;
						}
						case "flowers_per_chunk": {
							e.decorator.flowersPerChunk = value.getInt("flowers_per_chunk");
							break;
						}
						case "grass_per_chunk": {
							e.decorator.grassPerChunk = value.getInt("grass_per_chunk");
							break;
						}
						case "gravel_patches_per_chunk": {
							e.decorator.gravelPatchesPerChunk = value.getInt("gravel_patches_per_chunk");
							break;
						}
						case "mushrooms_per_chunk": {
							e.decorator.mushroomsPerChunk = value.getInt("mushrooms_per_chunk");
							break;
						}
						case "reeds_per_chunk": {
							e.decorator.reedsPerChunk = value.getInt("reeds_per_chunk");
							break;
						}
						case "sand_patches_per_chunk": {
							e.decorator.sandPatchesPerChunk = value.getInt("sand_patches_per_chunk");
							break;
						}
						case "trees_per_chunk": {
							e.decorator.treesPerChunk = value.getInt("trees_per_chunk");
							break;
						}
						case "waterlilies_per_chunk": {
							e.decorator.waterlilyPerChunk = value.getInt("waterlilies_per_chunk");
							break;
						}
						case "enable_lakes": {
							e.decorator.generateFalls = value.getBoolean("enable_lakes");
							break;
						}
					}
				}
				break;
			}
			case "village_block": {
				BiomeData.getBiomeData(e).villageBlock = Util.getBlock(value);
				break;
			}
			case "minables": {
				for (String option : value.root().keySet()) {
					if (!value.getBoolean(option)) {
						BiomeData.getBiomeData(e).disabledMinables
								.add(OreGenEvent.GenerateMinable.EventType.valueOf(option.toUpperCase()));
					}
				}
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	protected void handleString(Biome e, String key, String value) {
		switch (key) {
			case "top_block": {
				this.save(e.topBlock);
				e.topBlock = Util.getBlock(value);
				break;
			}
			case "filler_block": {
				this.save(e.fillerBlock);
				e.fillerBlock = Util.getBlock(value);
				break;
			}
			case "village_block": {
				BiomeData.getBiomeData(e).villageBlock = Util.getBlock(value);
				break;
			}
			default:
				this.missing(key);
		}
	}

	@Override
	protected void reset() {
		BiomeData.clear();
	}
}