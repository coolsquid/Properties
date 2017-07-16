package coolsquid.properties.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;

public class BlockData {

	private static final Map<Block, BlockData> BLOCK_DATA = new HashMap<>();
	private static final BlockData EMPTY = new BlockData();

	public List<ItemStack> dropsToAdd;
	public Set<Item> dropsToRemove;
	public boolean clearDrops = false;

	public BlockData() {
		this(new ArrayList<>(), new HashSet<>());
	}

	public BlockData(List<ItemStack> dropsToAdd, Set<Item> dropsToRemove) {
		this.dropsToAdd = Preconditions.checkNotNull(dropsToAdd);
		this.dropsToRemove = Preconditions.checkNotNull(dropsToRemove);
	}

	@Nonnull
	public static BlockData getBlockData(Block type) {
		BlockData data = BLOCK_DATA.get(type);
		if (data == null) {
			data = new BlockData();
			BLOCK_DATA.put(type, data);
		}
		return data;
	}

	@Nullable
	public static BlockData getBlockDataOrEmpty(Block type) {
		BlockData data = BLOCK_DATA.get(type);
		return data == null ? EMPTY : data;
	}

	public static void clear() {
		BLOCK_DATA.clear();
	}
}