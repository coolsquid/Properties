package coolsquid.properties.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.OreGenEvent;

public class BiomeData {

	private static final Map<Biome, BiomeData> BIOME_DATA = new HashMap<>();
	private static final BiomeData EMPTY = new BiomeData();

	public int size = -1;
	public int foliageColor = -1;
	public int waterColor = -1;
	public int grassColor = -1;

	public IBlockState villageBlock;

	public Set<OreGenEvent.GenerateMinable.EventType> disabledMinables = EnumSet
			.noneOf(OreGenEvent.GenerateMinable.EventType.class);

	@Nonnull
	public static BiomeData getBiomeData(Biome type) {
		BiomeData data = BIOME_DATA.get(type);
		if (data == null) {
			data = new BiomeData();
			BIOME_DATA.put(type, data);
		}
		return data;
	}

	@Nullable
	public static BiomeData getBiomeDataOrEmpty(Biome type) {
		BiomeData data = BIOME_DATA.get(type);
		return data == null ? EMPTY : data;
	}

	public static void clear() {
		BIOME_DATA.clear();
	}
}